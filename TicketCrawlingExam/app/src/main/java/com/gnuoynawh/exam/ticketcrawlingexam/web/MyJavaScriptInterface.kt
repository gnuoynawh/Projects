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
            ticket.number = element.select("div.nameWrap span").text()
            ticket.thumb = element.select("span.img").select("img").attr("src")

            // 상세정보
            val informs = element.select("span.prodInfoWrap").select("dl")
            informs.forEachIndexed { _, data ->

                val title = data.select("dt").text()
                val contents = data.select("dd").text()

                when(title) {
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
                    "관람일" -> ticket.date = contents
                    "공연장소" -> ticket.place = contents
                    "매수" -> ticket.number = contents
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
        val bookList = doc.getElementById("itemList")
        val books = bookList?.select("li")

        books?.forEachIndexed { index, element ->
            Log.e("TEST", "books [$index] all = ${element.text()}")
        }
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
                    "관람일시" -> ticket.date = contents
                    // "공연장소" -> ticket.place = contents
                    "매수" -> ticket.number = contents
                }
            }
            list.add(ticket)
        }

        activity.onResult(list)
    }

}