package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewActivity
import com.gnuoynawh.exam.ticketcrawlingexam.site.Site

class MyWebViewClient(
    private val activity: WebViewActivity,
    private val webView: WebView,
    private val site: Site
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        Log.e("TEST", "onPageFinished() : $url")

        if (site.step == Site.STEP.NONE && url?.contains(site.mainUrl) == true) {

            // 메인페이지 -> 로그인페이지로 이동
            site.goLoginPage(webView)

        } else if (site.step == Site.STEP.MAIN) {

            // 로그인 페이지
            activity.hideLoading()
            site.nextStep()

        } else if (site.step == Site.STEP.LOGIN && checkLoginResult(url)) {

            // 로그인 성공 체크 -> 예매내역 페이지로 이동
            activity.showLoading()
            site.goBookListPage(webView)

        } else if (site.step == Site.STEP.BOOKLIST) {

            // 예매내역 조회 -> html 파싱
            site.doParsing(webView)

        }
    }

    private fun checkLoginResult(url: String?): Boolean {
        return when(site.type) {
            Site.TYPE.INTERPARK,
            Site.TYPE.MELON,
            Site.TYPE.TICKETLINK -> url?.contains(site.loginResultUrl) == true
            Site.TYPE.YES24-> getCookie(url, "YesTicket").isNotEmpty()
        }
    }

    private fun getCookie(url: String?, cookieName: String): String {
        var cookieValue = ""
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie(url)
        if (cookies != null) {
            val temp = cookies.split(";")
            for (arg in temp) {
                if (arg.contains(cookieName)) {
                    val value = arg.split("=")
                    cookieValue = value[1]
                    break
                }
            }
        }
        return cookieValue
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