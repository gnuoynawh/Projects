package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient(
    private val context: Context,
    private val wv: WebView
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        AlertDialog.Builder(context)
            .setMessage("이 사이트의 보안 인증서는 신뢰하는 보안 인증서가 아닙니다. 계속하시겠습니까?")
            .setPositiveButton("계속하기") { _, _ ->
                handler!!.proceed()
            }.setNegativeButton("취소") { _, _ ->
                handler!!.cancel()
            }.create()
            .show()
    }
}