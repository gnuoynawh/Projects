package com.gnuoynawh.exam.ticketcrawlingexam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.gnuoynawh.exam.ticketcrawlingexam.data.Site
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.web.MyJavaScriptInterface
import com.gnuoynawh.exam.ticketcrawlingexam.web.MyWebViewClient

class WebViewActivity: AppCompatActivity(), View.OnClickListener {

    lateinit var btn1: AppCompatButton
    lateinit var btn2: AppCompatButton
    lateinit var btn3: AppCompatButton
    lateinit var webView: WebView

    lateinit var siteType: Site

    private var loginUrl = ""
    private var myUrl = ""
    private var bookListUrl = ""
    private var seachScript = ""
    private var parseScript = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        siteType = intent.getSerializableExtra("site") as Site
        updateData()

        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)

        webView = findViewById(R.id.webView)
        initWebView(loginUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_1 -> webView.loadUrl(bookListUrl)
            R.id.btn_2 -> webView.loadUrl(seachScript)
            R.id.btn_3 -> webView.loadUrl(parseScript)
        }
    }

    private fun updateData() {
        when(siteType) {
            Site.InterPark -> {
                loginUrl = "https://accounts.interpark.com/login/form"
                myUrl = "http://m.shop.interpark.com/"
                    // "https://smshop.interpark.com/my/shop/index.html"
                    // "http://mticket.interpark.com/MyTicket/"
                bookListUrl = "https://mticket.interpark.com/MyPage/BookedList?PeriodSearch=03#"
                seachScript = "javascript:" +
                        "for (var i = 2; i < 10; i++) {\n" +
                        "var _objItem = getStorageByKey(location.pathname);\n" +
                        "_objItem.clickCnt = i;\n" +
                        "setStorage(location.pathname, _objItem);\n" +
                        "loadReserveList()\n" +
                        "}"
                parseScript = "javascript:window.Android.getInterParkBookList(document.getElementsByTagName('body')[0].innerHTML);"
            }
            Site.Melon -> {
                loginUrl = "https://member.melon.com/muid/family/ticket/login/mobile/login_inform.htm?cpId=IX25&returnPage=https%3A//m.ticket.melon.com/main/index.htm"
                myUrl = "https://m.ticket.melon.com/main/index.htm"
                bookListUrl = "https://m.ticket.melon.com/myticket/rsrvList.htm"
                seachScript = "javascript:getRsrvList(24)"
                parseScript = "javascript:window.Android.getMelonBookList(document.getElementsByTagName('body')[0].innerHTML);"
            }
            Site.TicketLink -> {
                loginUrl = ""
                bookListUrl = ""
                seachScript = ""
                parseScript = ""
            }
            Site.Yes24 -> {
                loginUrl = ""
                bookListUrl = ""
                seachScript = ""
                parseScript = ""
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {

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
        webView.webViewClient = MyWebViewClient(this, webView)
        webView.addJavascriptInterface(MyJavaScriptInterface(this, webView), "Android")

        //
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.loadUrl(url)
        webView.visibility = View.VISIBLE

        webView.clearHistory()
        webView.clearFormData()
        webView.clearCache(true)

        val manager = CookieManager.getInstance()
        manager.removeAllCookie()
    }

    public fun onResult(list: ArrayList<Ticket>) {

        Log.e("TEST", "onResult : ${list.size}")

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("result", list)
        startActivity(intent)
        finish()
        overridePendingTransition(0,0)
    }

}