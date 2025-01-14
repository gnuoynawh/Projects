package com.gnuoynawh.musical.ticket.common.site

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.musical.ticket.common.Constants
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TicketLink(
    context: Context
): Site(context) {

    override val type: TYPE
        get() = TYPE.TICKETLINK

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

        detail?.forEachIndexed { _, element ->

            val book = element?.select("a")
            book?.forEachIndexed { index1, element1 ->

                val ticket = Ticket()
                ticket.site = getSite()
                ticket.title = element1.select("h4.tit").text()
                ticket.imageType = Constants.IMAGE_TYPE_URL

                Log.e("TEST", "books [$index1] all = ${element1.text()}")

                val informs = element1.select("ul.reserve_info li")
                informs.forEachIndexed { _, element2 ->

                    val values = element2.select("span")
                    val title = values[0].text()
                    val contents = values[1].text()

                    when(title) {
                        "예약번호" -> ticket.number = contents
                        "관람일시" -> ticket.date = getDateFormat(contents)
                        // "공연장소" -> ticket.place = contents
                        "티켓" -> ticket.count = contents
                        "현재상태" -> ticket.state = contents
                    }
                }

                if(!verifyDuplicate(ticket, list) && ticket.title.startsWith("뮤지컬")) {
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
        step = STEP.BOOKLIST
    }

    override fun doParsing(webView: WebView) {
        webView.loadUrl(parseScript)
        step = STEP.PARSE
    }

    override fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>) : Boolean {

        if (ticket.state.contains("취소"))
            return true

        list.forEachIndexed { _, item ->
            if (item.number == ticket.number)
                return true
        }

        return false
    }
}