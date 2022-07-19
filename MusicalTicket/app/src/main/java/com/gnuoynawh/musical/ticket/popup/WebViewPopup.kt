package com.gnuoynawh.musical.ticket.popup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.RelativeLayout
import com.gnuoynawh.musical.ticket.BuildConfig
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.web.MyJavaScriptInterface
import com.gnuoynawh.musical.ticket.web.MyWebViewClient

class WebViewPopup(
    context: Context,
    val site: Site
): Dialog(context, R.style.Dialog_Style) {

    private var webView: WebView
    private var loadingView: RelativeLayout

    init {
        setContentView(R.layout.popup_webview)

        webView = findViewById(R.id.webView)
        loadingView = findViewById(R.id.layout_loading)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView() {

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.domStorageEnabled = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.mediaPlaybackRequiresUserGesture = false

        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.setSupportMultipleWindows(false)

        //
        webView.webViewClient = MyWebViewClient(this, webView, site)
        webView.addJavascriptInterface(MyJavaScriptInterface(this, webView, site), "Android")

        //
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.loadUrl(site.mainUrl)
        showLoading()

        webView.clearHistory()
        webView.clearFormData()
        webView.clearCache(true)

        val manager = CookieManager.getInstance()
        manager.removeAllCookie()
    }

    fun showLoading() {
        webView.visibility = View.INVISIBLE
        loadingView.visibility = View.VISIBLE
    }

    fun hideLoading() {
        webView.visibility = View.VISIBLE
        loadingView.visibility = View.INVISIBLE
    }

    fun onResultWithDB(list: ArrayList<Ticket>) {
        Log.e("TEST", "onResultWithDB : ${list.size}")

        listener?.onResult(list)
    }

    private var listener: OnCallBackListener? = null

    fun setOnCallBackListener(listener: OnCallBackListener) {
        this.listener = listener
    }

    interface OnCallBackListener {
        fun onResult(list: ArrayList<Ticket>)
    }
}