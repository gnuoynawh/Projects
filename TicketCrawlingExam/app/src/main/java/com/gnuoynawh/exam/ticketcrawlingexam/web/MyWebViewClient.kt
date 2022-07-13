package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewActivity
import com.gnuoynawh.exam.ticketcrawlingexam.data.Site
import com.gnuoynawh.exam.ticketcrawlingexam.data.SiteStep
import com.gnuoynawh.exam.ticketcrawlingexam.data.SiteType

class MyWebViewClient(
    private val activity: WebViewActivity,
    private val webView: WebView,
    private val site: Site
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    var delay: Long = 2000

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

//        if (site.step == SiteStep.Main)
//            delay = 10
        if (site.stop)
            return

        Handler(Looper.myLooper()!!).postDelayed({
            when(site.step) {
                SiteStep.Main -> {
                    site.goLoginPage(webView)
                    //delay = 2000
                }
                SiteStep.Login -> site.goBookListPage(webView)
//                SiteStep.BookList -> site.searchBookList(webView)
//                SiteStep.Search -> site.parseBookList(webView)
//                SiteStep.Parse -> activity.hideLoading()
            }
        }, delay)
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        AlertDialog.Builder(activity)
            .setMessage("이 사이트의 보안 인증서는 신뢰하는 보안 인증서가 아닙니다. 계속하시겠습니까?")
            .setPositiveButton("계속하기") { _, _ ->
                handler!!.proceed()
            }.setNegativeButton("취소") { _, _ ->
                handler!!.cancel()
            }.create()
            .show()
    }
}