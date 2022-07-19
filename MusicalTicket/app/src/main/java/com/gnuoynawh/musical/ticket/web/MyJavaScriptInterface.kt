package com.gnuoynawh.musical.ticket.web

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.site.Yes24
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.ui.WebViewActivity

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
    fun getBookList(html: String) {
        activity.onResultWithDB(site.getBookList(html))
    }

    /**
     * Only Site == Yes24
     */
    @JavascriptInterface
    fun goNextPage(html: String) {
        (site as Yes24).getBookListFromPage(html)
    }
}