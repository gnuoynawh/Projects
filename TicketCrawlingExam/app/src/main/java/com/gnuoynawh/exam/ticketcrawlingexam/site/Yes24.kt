package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.web.MyYes24JavaScriptInterface
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * todo : 예스24는 리스트가 관람내역, 예매내역이 존재하는데, 현재는 관람내역만 작업해둠.
 *        - 관람내역 (http://m.ticket.yes24.com/MyPage/WatchList.aspx),
 *        - 예매내역 (http://m.ticket.yes24.com/MyPage/OrderList.aspx)
 *
 * todo : 10건 이상일 경우, 페이징 처리가 됨
 */
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
        get() = "http://m.ticket.yes24.com/MyPage/CancelList.aspx"
                //"http://m.ticket.yes24.com/MyPage/WatchList.aspx"

    override val parseScript: String
        get() = "javascript:" +
                "var from = document.getElementById('ddlSearchFrom');\n" +
                "var to = document.getElementById('ddlSearchTo');\n" +
                "from.selectedIndex = 1;\n" +
                "to.selectedIndex = 0;\n"+
                "document.querySelector('.btn_c.btn_gray').click();\n" +
                "setTimeout(function(){\n " +
                    "window.Android.goNextPage(document.getElementsByTagName('body')[0].innerHTML);" +
                "}, 2000);"

    private val resultList = ArrayList<Ticket>()
    private var currentPage = 1

    private fun goNextPage() {
        val nextScript =
            "javascript:jsf_go_pager($currentPage);\n" +
            "setTimeout(function(){\n " +
                "window.Android.goNextPage(document.getElementsByTagName('body')[0].innerHTML);" +
            "}, 2000);"

        webView?.loadUrl(nextScript)
    }

    private fun finish() {
        webView?.loadUrl("window.Android.getBookList(document.getElementsByTagName('body')[0].innerHTML);")
    }

    fun getBookListFromPage(html: String) {
        Log.e("TEST", "getBookListFromPage()")

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 전체 건수 가져오기
        val totalCnt = doc.getElementById("emtotalCnt")?.text() ?: "0"
        val pages = calculatePage(totalCnt.toInt())

        if (pages > currentPage) {

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

                if(!verifyDuplicate(ticket, resultList)) {
                    resultList.add(ticket)
                }
            }

            currentPage++
            goNextPage()
        } else {
            finish()
        }
    }

    override fun getBookList(html: String): ArrayList<Ticket> {
        return resultList
    }

    override fun goLoginPage(webView: WebView) {
        webView.loadUrl(loginUrl)
    }

    override fun goBookListPage(webView: WebView) {
        webView.loadUrl(bookListUrl)
        step = SiteStep.BookList
    }

    /**
     *
     */
    var webView: WebView? = null
    override fun getBookList(webView: WebView) {
        this.webView = webView
        resultList.clear()

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

    private fun calculatePage(count: Int): Int {
        val condition = 10

        return when {
            count == 0 ->
                1
            count % condition > 0 ->
                count / condition + 1
            else ->
                count / condition
        }
    }
}