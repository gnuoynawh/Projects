package com.gnuoynawh.exam.ticketcrawlingexam.data

import android.util.Log
import android.webkit.WebView

abstract class Site {

    var step: SiteStep = SiteStep.Main

    abstract val type: SiteType
    abstract val loginUrl: String
    abstract val bookListUrl: String
    abstract val seachScript: String
    abstract val parseScript: String
    abstract val mainUrl: String

    abstract fun goLoginPage(webView: WebView)
    abstract fun goBookListPage(webView: WebView)
    abstract fun searchBookList(webView: WebView)
    abstract fun parseBookList(webView: WebView)

    abstract fun getBookList(html: String): ArrayList<Ticket>

    fun goNextStep() {

        stop = false

        Log.e("TEST", "goNextStep() before step = $step")
        when(step) {
            SiteStep.Main -> step = SiteStep.Login
            SiteStep.Login -> step = SiteStep.BookList
            SiteStep.BookList -> step = SiteStep.Search
            SiteStep.Search -> step = SiteStep.Parse
            else -> {}
        }
        Log.e("TEST", "goNextStep() after step = $step")
    }

    var stop = false

    fun stop() {
        stop = true
    }

    /**
     *
     */
    fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>) : Boolean {

        // 예매 취소면 제외
        if (ticket.state.contains("취소"))
            return true

        // 이미 추가된 리스트일 경우 제외
        list.forEachIndexed { _, item ->
            if (item.number == ticket.number)
                return true
        }

        return false
    }
}