package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TicketLink: Site() {

    override val type: SiteType
        get() = SiteType.TicketLink

    override val mainUrl: String
        get() = "https://m.ticketlink.co.kr/"

    override val loginUrl: String
        get() = "javascript:" +
                    "document.getElementById('loginBtn').click();\n" +
                    "setTimeout(function(){\n " +
                        "window.Android.goNextStep();" +
                    "}, 100);"

    override val loginResultUrl: String
        get() = mainUrl

    override var bookListUrl: String = "https://m.ticketlink.co.kr/my/reserve/list?page=1&productClass=ALL&searchType=PERIOD&period=MONTH_3&targetDay=RESERVE&year=&month="

    override val parseScript: String
        get() = "javascript:" +
                    "for (var i = 1; i <= 5; i++) {" +
                        "var j = 0; " +
                        "setTimeout(function(){ " +
                            "window.scrollTo(0, document.body.scrollHeight);" +
                            "j++;" +
                            "console.log('j = ' + j);" +
                            "if (j == 5) " +
                                "window.Android.getBookList(document.getElementsByTagName('body')[0].innerHTML);" +
                        "}, 2000 * i);" +
                    "}"

    override fun getBookList(html: String): ArrayList<Ticket> {

        Log.e("TEST", "getBookList()")

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
        webView.loadUrl(loginUrl)
    }

    override fun goBookListPage(webView: WebView) {
        webView.loadUrl(bookListUrl)
        step = SiteStep.BookList
    }

    override fun doParsing(webView: WebView) {
        webView.loadUrl(parseScript)
        step = SiteStep.Parse
    }

    override fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>) : Boolean {

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