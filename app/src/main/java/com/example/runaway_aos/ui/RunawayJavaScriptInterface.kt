package com.example.runaway_aos.ui

import android.content.Intent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.runaway_aos.LocationUpdateListener
import com.example.runaway_aos.LocationUpdateListenerHolder
import com.example.runaway_aos.services.RunningService
import com.example.runaway_aos.util.Constants.ACTION_START_SERVICE
import com.example.runaway_aos.util.Constants.ACTION_STOP_SERVICE
import org.json.JSONObject
import timber.log.Timber

class RunawayJavaScriptInterface(private val webView: WebView) : LocationUpdateListener {
    var runningService: Intent = Intent(webView.context, RunningService::class.java)

    @JavascriptInterface
    fun startRun() {
        Timber.e("ACTION_START_SERVICE")
        LocationUpdateListenerHolder.listener = this
        sendCommandToService(ACTION_START_SERVICE)
    }

    @JavascriptInterface
    fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun sendCommandToService(action: String) =
        runningService.also {
            it.action = action
            webView.context.startService(it)
        }

    override fun onLocationUpdate(latitude: Double, longitude: Double) {
        val jsonObj = JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }.toString()
        val script =
            "var event_device = new CustomEvent(\"onGeoLocationCallback\", {detail: '${jsonObj}'});\n" +
                    "window.dispatchEvent(event_device);"
        webView.post {
            webView.evaluateJavascript(script) {}
        }
    }
}