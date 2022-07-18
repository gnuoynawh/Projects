package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket

abstract class Site {

    var step: SiteStep = SiteStep.None

    abstract val type: SiteType
    abstract val mainUrl: String
    abstract val loginUrl: String
    abstract val loginResultUrl: String
    abstract var bookListUrl: String
    abstract val parseScript: String

    /**
     *
     */
    abstract fun goLoginPage(webView: WebView)

    /**
     *
     */
    abstract fun goBookListPage(webView: WebView)

    /**
     *
     */
    abstract fun doParsing(webView: WebView)

    /**
     *
     */
    abstract fun getBookList(html: String): ArrayList<Ticket>

    /**
     *
     */
    abstract fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>) : Boolean

    /**
     *
     */
    fun goNextStep() {
        Log.e("TEST", "goNextStep() before step = $step")
        when(step) {
            SiteStep.None -> step = SiteStep.Main
            SiteStep.Main -> step = SiteStep.Login
            SiteStep.Login -> step = SiteStep.BookList
            SiteStep.BookList -> step = SiteStep.Parse
            SiteStep.Parse -> step = SiteStep.Done
            else -> {}
        }
        Log.e("TEST", "goNextStep() after step = $step")
    }
}