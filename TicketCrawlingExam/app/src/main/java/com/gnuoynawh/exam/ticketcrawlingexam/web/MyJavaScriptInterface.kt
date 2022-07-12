package com.gnuoynawh.exam.ticketcrawlingexam.web

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import com.gnuoynawh.exam.ticketcrawlingexam.WebViewActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MyJavaScriptInterface(
    private val activity: WebViewActivity,
    private val wv: WebView
) {

    @JavascriptInterface
    fun getInterParkBookList(html: String) {

        val list = ArrayList<Ticket>()

        // body 전체파싱
        val doc: Document = Jsoup.parse(html)

        // 예매리스트
        val bookList = doc.getElementById("itemList")
        val books = bookList?.select("li")

        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")

            val ticket: Ticket = Ticket()
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
                    "관람일시" -> ticket.date = contents
                    "장소" -> ticket.place = contents
                }
            }
            list.add(ticket)
        }

        activity.onResult(list)
    }

    @JavascriptInterface
    fun getMelonBookList(html: String) {

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
            list.add(ticket)
        }

        activity.onResult(list)
    }

    @JavascriptInterface
    fun getTicketLinkBookList(html: String) {

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

                Log.e("TEST", "book [$index1] = ${ticket.title}")

                val informs = element1.select("ul.reserve_info li")
                informs.forEachIndexed { index2, element2 ->
                    Log.e("TEST", "informs [$index2] all = ${element2.text()}")

                    val title = element2.select("s_tit").text()
                    val contents = element2.select("ng-binding").text()

                    when(title) {
                        "예약번호" -> ticket.number = contents
                        "관람일시" -> ticket.date = contents
                        // "공연장소" -> ticket.place = contents
                        "티켓" -> ticket.count = contents
                        "현재상태" -> ticket.state = contents
                    }
                }
                list.add(ticket)
            }
        }

        Log.e("TEST", "list [${list.size}]")
        //activity.onResult(list)
    }

    @JavascriptInterface
    fun getYes24BookList(html: String) {

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

                when(title) {
                    "예매번호" -> ticket.number = contents
                    "관람일시" -> ticket.date = contents
                    // "공연장소" -> ticket.place = contents
                    "매수" -> ticket.count = contents
                }
            }
            list.add(ticket)
        }

        activity.onResult(list)
    }

}