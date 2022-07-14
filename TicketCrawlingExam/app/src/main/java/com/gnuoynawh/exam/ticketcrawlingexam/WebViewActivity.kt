package com.gnuoynawh.exam.ticketcrawlingexam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.site.*
import com.gnuoynawh.exam.ticketcrawlingexam.web.MyJavaScriptInterface
import com.gnuoynawh.exam.ticketcrawlingexam.web.MyWebViewClient

class WebViewActivity: AppCompatActivity() {

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val loadingView: RelativeLayout by lazy {
        findViewById(R.id.rl_loading)
    }

    lateinit var site: Site

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        updateData()
        initWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }

    private fun updateData() {
        site = when(intent.getSerializableExtra("site") as SiteType) {
            SiteType.InterPark  -> InterPark()
            SiteType.Melon      -> Melon()
            SiteType.TicketLink -> TicketLink()
            SiteType.Yes24      -> Yes24()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {

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

    fun onResult(list: ArrayList<Ticket>) {
        Log.e("TEST", "onResult : ${list.size}")

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("result", list)
        startActivity(intent)
        finish()
        overridePendingTransition(0,0)
    }

}