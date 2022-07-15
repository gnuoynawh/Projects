package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewActivity
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewYes24Activity
import com.gnuoynawh.exam.ticketcrawlingexam.site.Site
import com.gnuoynawh.exam.ticketcrawlingexam.site.Yes24
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MyYes24JavaScriptInterface(
    private val activity: WebViewYes24Activity,
    private val wv: WebView,
    private val site: Site
) {

    @JavascriptInterface
    fun goNextStep() {
        site.goNextStep()
    }

    @JavascriptInterface
    fun goNextPage(html: String) {
        Log.e("TEST", "goNextPage()")

    }

    @JavascriptInterface
    fun getBookList(html: String) {
        Log.e("TEST", "getBookList()")
        activity.onResult(site.getBookList(html))
    }
}