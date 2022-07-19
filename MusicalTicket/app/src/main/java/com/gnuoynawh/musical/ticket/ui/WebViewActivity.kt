//package com.gnuoynawh.musical.ticket.ui
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.webkit.CookieManager
//import android.webkit.WebSettings
//import android.webkit.WebView
//import android.widget.RelativeLayout
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.gnuoynawh.exam.ticketcrawlingexam.site.InterPark
//import com.gnuoynawh.exam.ticketcrawlingexam.site.Melon
//import com.gnuoynawh.exam.ticketcrawlingexam.site.TicketLink
//import com.gnuoynawh.exam.ticketcrawlingexam.site.Yes24
//import com.gnuoynawh.musical.ticket.R
//import com.gnuoynawh.musical.ticket.common.site.Site
//import com.gnuoynawh.musical.ticket.db.MTicketDatabase
//import com.gnuoynawh.musical.ticket.db.Ticket
//import com.gnuoynawh.musical.ticket.web.MyJavaScriptInterface
//import com.gnuoynawh.musical.ticket.web.MyWebViewClient
//import kotlinx.coroutines.launch
//
//class WebViewActivity: AppCompatActivity() {
//
//    private val db by lazy {
//        MTicketDatabase.getDatabase(this)?.getTicket()
//    }
//
//    private val webView: WebView by lazy {
//        findViewById(R.id.webView)
//    }
//
//    private val loadingView: RelativeLayout by lazy {
//        findViewById(R.id.layout_loading)
//    }
//
//    lateinit var site: Site
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_web)
//
//        updateData()
//        initWebView()
//    }
//
//    private fun updateData() {
//        site = when(intent.getSerializableExtra("site") as Site.TYPE) {
//            Site.TYPE.INTERPARK  -> InterPark()
//            Site.TYPE.MELON      -> Melon()
//            Site.TYPE.TICKETLINK -> TicketLink()
//            Site.TYPE.YES24      -> Yes24()
//        }
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    private fun initWebView() {
//
//        val settings = webView.settings
//        settings.javaScriptEnabled = true
//        settings.javaScriptCanOpenWindowsAutomatically = false
//        settings.domStorageEnabled = true
//        settings.builtInZoomControls = false
//        settings.displayZoomControls = false
//        settings.mediaPlaybackRequiresUserGesture = false
//
//        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
//        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        webView.settings.setSupportMultipleWindows(false)
//
//        //
//        webView.webViewClient = MyWebViewClient(this, webView, site)
//        webView.addJavascriptInterface(MyJavaScriptInterface(this, webView, site), "Android")
//
//        //
////        if (BuildConfig.DEBUG) {
////            WebView.setWebContentsDebuggingEnabled(true)
////        }
//
//        webView.loadUrl(site.mainUrl)
//        showLoading()
//
//        webView.clearHistory()
//        webView.clearFormData()
//        webView.clearCache(true)
//
//        val manager = CookieManager.getInstance()
//        manager.removeAllCookie()
//    }
//
//    fun showLoading() {
//        webView.visibility = View.INVISIBLE
//        loadingView.visibility = View.VISIBLE
//    }
//
//    fun hideLoading() {
//        webView.visibility = View.VISIBLE
//        loadingView.visibility = View.INVISIBLE
//    }
//
//    private fun onResult(list: ArrayList<Ticket>?) {
//        Log.e("TEST", "onResult : ${list?.size ?: 0}")
//
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//        overridePendingTransition(0,0)
//    }
//
//    fun onResultWithDB(list: ArrayList<Ticket>) {
//        Log.e("TEST", "onResultWithDB : ${list.size}")
//
//        // insert
//        lifecycleScope.launch {
//            db?.insert(*list.map { it }.toTypedArray())
//            onResult(null)
//        }
//    }
//}