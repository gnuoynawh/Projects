package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.db.dao.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Melon: Site() {

    override val type: TYPE
        get() = TYPE.MELON

    override val mainUrl: String
        get() = "https://m.ticket.melon.com/main/index.htm"

    override val loginUrl: String
        get() = "https://member.melon.com/muid/family/ticket/login/mobile/login_inform.htm?cpId=IX25&returnPage=https%3A//m.ticket.melon.com/main/index.htm"

    override val loginResultUrl: String
        get() = mainUrl

    override var bookListUrl: String = "https://m.ticket.melon.com/myticket/rsrvList.htm"

    override val parseScript: String
        get() = "javascript:" +
                    "getRsrvList(24);\n" +
                    "setTimeout(function(){\n " +
                        "window.Android.getBookList(document.getElementsByTagName('body')[0].innerHTML);" +
                    "}, 2000);"

    override fun getBookList(html: String): ArrayList<Ticket> {

        Log.e("TEST", "getBookList()")

        val list = ArrayList<Ticket>()

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 예매리스트
        val bookList = doc.getElementById("part_rsrv_list")
        val books = bookList?.select("li")
        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")

            val ticket: Ticket = Ticket()
            ticket.title = element.select("div.tit").text()
            ticket.thumb = element.select("div.thumb img").attr("src")

            // 상세정보
            val informs = element.select("div.list dl")
            informs.forEachIndexed { _, data ->

                val title = data.select("dt").text()
                val contents = data.select("dd").text()

                when(title) {
                    "예매번호" -> ticket.number = contents
                    "관람일" -> ticket.date = contents
                    "공연장소" -> ticket.place = contents
                    "매수" -> ticket.count = contents
                }
            }

            if(!verifyDuplicate(ticket, list)) {
                list.add(ticket)
            }
        }

        return list
    }

    override fun goLoginPage(webView: WebView) {
        webView.loadUrl(loginUrl)
        step = STEP.MAIN
    }

    override fun goBookListPage(webView: WebView) {
        webView.loadUrl(bookListUrl)
        step = STEP.BOOKLIST
    }

    override fun doParsing(webView: WebView) {
        webView.loadUrl(parseScript)
        step = STEP.PARSE
    }

    override fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>): Boolean {
        list.forEachIndexed { _, item ->
            if (item.number == ticket.number)
                return true
        }

        return false
    }

}