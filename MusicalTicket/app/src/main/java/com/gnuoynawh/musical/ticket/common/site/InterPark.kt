package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class InterPark: Site() {

    override val type: TYPE
        get() = TYPE.INTERPARK

    override val mainUrl: String
        get() = "https://accounts.interpark.com/login/form"

    override val loginUrl: String
        get() = mainUrl

    override val loginResultUrl: String
        get() = "http://m.shop.interpark.com/"
                // "https://smshop.interpark.com/my/shop/index.html"
                // "http://mticket.interpark.com/MyTicket/"

    override var bookListUrl: String = "https://mticket.interpark.com/MyPage/BookedList?PeriodSearch=03#"

    override val parseScript: String
        get() = "javascript:" +
                    "for (var i = 1; i <= 5; i++) {" +
                        "var j = 1; " +
                        "setTimeout(function(){ " +
                            "j++;" +
                            "var _objItem = getStorageByKey(location.pathname);" +
                            "_objItem.clickCnt = j;" +
                            "setStorage(location.pathname, _objItem);" +
                            "loadReserveList();" +

                            "console.log('j = ' + j); " +
                            "if (j == 6) " +
                                "window.Android.getBookList(document.getElementsByTagName('body')[0].innerHTML);" +
                        "}, 2000 * i);" +
                    "}"

    override fun getBookList(html: String): ArrayList<Ticket> {

        Log.e("TEST", "getBookList()")

        val list = ArrayList<Ticket>()

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 예매리스트
        val bookList = doc.getElementById("itemList")
        val books = bookList?.select("li")

        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")

            val ticket = Ticket()
            ticket.site = getSite()
            ticket.title = element.select("div.nameWrap p").text()
            ticket.count = element.select("div.nameWrap span").text()
            ticket.thumb = element.select("span.img").select("img").attr("src")

            // 상세정보
            val informs = element.select("span.prodInfoWrap").select("dl")
            informs.forEachIndexed { _, data ->

                val title = data.select("dt").text()
                val contents = data.select("dd").text()

                when(title) {
                    "예매번호" -> ticket.number = contents
                    "관람일시" -> ticket.date = getDateFormat(contents)
                    "장소" -> ticket.place = contents
                }
            }

            if(!verifyDuplicate(ticket, list) && ticket.title.startsWith("뮤지컬")) {
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