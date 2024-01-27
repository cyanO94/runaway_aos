package com.example.runaway_aos.ui

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject

class RunawayJavaScriptInterface(private val webView: WebView) {
    @JavascriptInterface
    fun getGeoLocation() {
        val latitude = 37.7749
        val longitude = -122.4194
        val jsonObj = JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }.toString()
        val script =
            "var event_device = new CustomEvent(\"onGeoLocationCallback\", {detail: '${jsonObj}'});\n" +
                    "window.dispatchEvent(event_device);"

        Log.e("test", script)
        webView.post {
            webView.evaluateJavascript(script) {}
        }
    }
}