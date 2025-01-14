package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewActivity
import com.gnuoynawh.exam.ticketcrawlingexam.site.Site
import com.gnuoynawh.exam.ticketcrawlingexam.site.Yes24

class MyJavaScriptInterface(
    private val activity: WebViewActivity,
    private val wv: WebView,
    private val site: Site
) {

    @JavascriptInterface
    fun goNextStep() {
        site.nextStep()
    }

    @JavascriptInterface
    fun goNextPage(html: String) {
        (site as Yes24).getBookListFromPage(html)
    }

    @JavascriptInterface
    fun getBookList(html: String) {
        // activity.onResult(site.getBookList(html))
        activity.onResultWithDB(site.getBookList(html))
    }
}