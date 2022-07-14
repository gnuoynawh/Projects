package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Yes24: Site() {

    override val type: SiteType
        get() = SiteType.Yes24

    override val mainUrl: String
        get() = "http://m.ticket.yes24.com/"

    override val loginUrl: String
        get() = "javascript:" +
                    "jsf_base_GoYes24Login('');\n" +
                    "setTimeout(function(){\n " +
                        "window.Android.goNextStep();" +
                    "}, 100);"

    override val loginResultUrl: String
        get() = "m.ticket.yes24.com"

    override val bookListUrl: String
        get() = "http://m.ticket.yes24.com/MyPage/WatchList.aspx"

    override val parseScript: String
        get() = "javascript:" +
                "var from = document.getElementById('ddlSearchFrom');\n" +
                "var to = document.getElementById('ddlSearchTo');\n" +
                "from.selectedIndex = 1;\n" +
                "to.selectedIndex = 0;\n"+
                "document.querySelector('.btn_c.btn_gray').click();\n" +
                "setTimeout(function(){\n " +
                    "window.Android.getBookList(document.getElementsByTagName('body')[0].innerHTML);" +
                "}, 2000);"

    override fun getBookList(html: String): ArrayList<Ticket> {

        Log.e("TEST", "getBookList()")

        val list = ArrayList<Ticket>()

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 예매리스트
        val bookList = doc.getElementById("BoardList")
        val books = bookList?.select("li")

        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")

            val ticket: Ticket = Ticket()
            ticket.title = element.select("p.goods_name").text()
            ticket.thumb = element.select("div.goods_img img").attr("src")

            // 상세정보
            val informs = element.select("div.goods_infoUnitArea dl")
            informs.forEachIndexed { _, data ->

                val title = data.select("dt").text()
                val contents = data.select("dd").text()

                // 예매번호 예외
                val number = data.select("dd em.txt").text()

                when(title) {
                    "예매번호" -> ticket.number = number
                    "관람일시" -> ticket.date = contents
                    // "공연장소" -> ticket.place = contents
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
    }

    override fun goBookListPage(webView: WebView) {
        webView.loadUrl(bookListUrl)
        step = SiteStep.BookList
    }

    override fun getBookList(webView: WebView) {
        webView.loadUrl(parseScript)
        step = SiteStep.Parse
    }

    override fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>): Boolean {
        // 이미 추가된 리스트일 경우 제외
        list.forEachIndexed { _, item ->
            if (item.number == ticket.number)
                return true
        }

        return false
    }

}