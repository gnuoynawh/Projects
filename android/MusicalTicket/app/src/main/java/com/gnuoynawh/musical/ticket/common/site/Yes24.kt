package com.gnuoynawh.musical.ticket.common.site

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.gnuoynawh.musical.ticket.common.Constants
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.Ticket
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Yes24(
    context: Context
): Site(context) {

    override val type: TYPE
        get() = TYPE.YES24

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

    override var bookListUrl: String = "http://m.ticket.yes24.com/MyPage/OrderList.aspx"

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

    override fun getBookList(html: String): ArrayList<Ticket> {
        Log.e("TEST", "getBookList(${resultList.size}) = $html")
        return resultList
    }

    override fun goLoginPage(webView: WebView) {
        webView.loadUrl(loginUrl)
    }

    override fun goBookListPage(webView: WebView) {
        webView.loadUrl(bookListUrl)
        step = STEP.BOOKLIST
    }

    override fun doParsing(webView: WebView) {
        this.webView = webView

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

    /////////////////////////////////////////

    // 웹뷰
    private var webView: WebView? = null

    // 결과 리스트
    private val resultList = ArrayList<Ticket>()

    // 페이징 처리
    private var currentPage = 1

    // 관람내역
    private val orderListUrl: String = "http://m.ticket.yes24.com/MyPage/WatchList.aspx"
                                    // "http://m.ticket.yes24.com/MyPage/CancelList.aspx"

    // 다음 페이지로 이동
    private fun goNextPage() {
        webView?.post {
            webView?.loadUrl(
                "javascript:jsf_go_pager($currentPage);\n" +
                        "setTimeout(function(){\n " +
                            "window.Android.goNextPage(document.getElementsByTagName('body')[0].innerHTML);" +
                        "}, 3000);"
            )
        }
    }

    // 페이지로부터 데이터 파싱
    fun getBookListFromPage(html: String) {
        Log.e("TEST", "getBookListFromPage()")

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 전체 건수 가져오기
        val totalCnt = doc.getElementById("emtotalCnt")?.text() ?: "0"
        val pages = calculatePage(totalCnt.toInt())

        Log.e("TEST", "pages = $pages, current = $currentPage")

        // 내역이 0 일때
        if (pages == 0) {
            done()
        } else {

            // 내역이 있을 경우 파싱
            parse(doc)

            if (pages == currentPage) {

                // 마지막 페이지이면 종료
                done()
            } else {

                // 페이지가 있다면 다음페이지로 이동
                currentPage++
                goNextPage()
            }
        }
    }

    // 예매 내역 파싱
    private fun parse(doc: Document) {
        val bookList = doc.getElementById("BoardList")
        val books = bookList?.select("li")

        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")

            val ticket = Ticket()
            ticket.site = getSite()
            ticket.title = element.select("p.goods_name").text()
            ticket.image = element.select("div.goods_img img").attr("src")
            ticket.imageType = Constants.IMAGE_TYPE_URL

            // 상세정보
            val informs = element.select("div.goods_infoUnitArea dl")
            informs.forEachIndexed { _, data ->

                val title = data.select("dt").text()
                val contents = data.select("dd").text()

                // 예매번호 예외
                val number = data.select("dd em.txt").text()

                when(title) {
                    "예매번호" -> ticket.number = number
                    "관람일시" -> ticket.date = getDateFormat(contents)
                    // "공연장소" -> ticket.place = contents
                    "매수" -> ticket.count = contents
                }
            }

            if(!verifyDuplicate(ticket, resultList) && ticket.title.startsWith("뮤지컬")) {
                resultList.add(ticket)
            }
        }
    }

    // 파싱 종료
    private fun done() {
        webView?.post {

            // 예매내역일 경우, 관람내역 파싱 시작
            if (webView?.url?.contains("OrderList") == true) {
                bookListUrl = orderListUrl
                currentPage = 1
                this.goBookListPage(webView!!)
            } else {
                webView?.loadUrl("javascript:window.Android.getBookList('');")
            }
        }
    }

    // 페이지 계산
    private fun calculatePage(count: Int): Int {
        val condition = 10

        return when {
            count == 0 ->
                0
            count % condition > 0 ->
                count / condition + 1
            else ->
                count / condition
        }
    }
}