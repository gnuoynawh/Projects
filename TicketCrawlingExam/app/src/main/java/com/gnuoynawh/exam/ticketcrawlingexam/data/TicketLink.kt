package com.gnuoynawh.exam.ticketcrawlingexam.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TicketLink : Site() {

    override val type: SiteType
        get() = SiteType.TicketLink

    override val loginUrl: String
        get() = "javascript:" +
                    "document.getElementById('loginBtn').click();\n" +
                    "setTimeout(function(){\n " +
                        "window.Android.goNextStep();" +
                    "}, 100);"

    override val bookListUrl: String
        get() = "http://m.ticketlink.co.kr/my/reserve/list?page=1&productClass=ALL&searchType=PERIOD&period=MONTH_3&targetDay=RESERVE&year=&month="

    override val seachScript: String
        get() = "javascript:" +
                    "window.Android.stop();\n" +
                    "for (var i = 2; i < 10; i++) {\n" +
                        "setTimeout(function(){\n " +
                            "window.scrollTo(0, document.body.scrollHeight);" +
                            "if (i == 9) window.Android.goNextStep();" +
                        "}, 300);" +
                    "}"

    override val parseScript: String
        get() = "javascript:window.Android.getTicketLinkBookList(document.getElementsByTagName('body')[0].innerHTML);"

    override val mainUrl: String
        get() = "http://m.ticketlink.co.kr/"


    override fun getBookList(html: String): ArrayList<Ticket> {

        val list = ArrayList<Ticket>()

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 예매리스트
        val detailList = doc.getElementsByClass("detail_cont")
        val detail = detailList?.select("ul.reserve_detail")

        Log.e("TEST", "detailList size = ${detailList?.size}")
        detail?.forEachIndexed { index, element ->
            Log.e("TEST", "detail [$index]")

            // 아래 스크롤
            // window.scrollTo(0, document.body.scrollHeight);

            val book = element?.select("a")
            book?.forEachIndexed { index1, element1 ->

                val ticket = Ticket()
                ticket.title = element1.select("h4.tit").text()

                Log.e("TEST", "======== book [$index1] = ${ticket.title}")

                val informs = element1.select("ul.reserve_info li")
                informs.forEachIndexed { index2, element2 ->

                    val values = element2.select("span")
                    val title = values[0].text()
                    val contents = values[1].text()

                    Log.e("TEST", "informs [$index2] title = $title, contents = $contents")

                    when(title) {
                        "예약번호" -> ticket.number = contents
                        "관람일시" -> ticket.date = contents
                        // "공연장소" -> ticket.place = contents
                        "티켓" -> ticket.count = contents
                        "현재상태" -> ticket.state = contents
                    }
                }

                if(!verifyDuplicate(ticket, list)) {
                    list.add(ticket)
                }
            }
        }

        return list
    }

    override fun goLoginPage(webView: WebView) {
        Log.e("TEST", "goLoginPage()!!!!")
        webView.loadUrl(loginUrl)
    }

    override fun goBookListPage(webView: WebView) {
        Log.e("TEST", "goBookListPage()!!!!")
        webView.loadUrl(bookListUrl)
        goNextStep()
    }

    override fun searchBookList(webView: WebView) {
        Log.e("TEST", "searchBookList()!!!!")
        webView.loadUrl(seachScript)
    }

    override fun parseBookList(webView: WebView) {
        Log.e("TEST", "parseBookList()!!!!")
        webView.loadUrl(parseScript)
        goNextStep()
    }


}