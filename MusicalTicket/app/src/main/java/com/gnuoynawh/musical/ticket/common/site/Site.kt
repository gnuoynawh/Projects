package com.gnuoynawh.musical.ticket.common.site

import android.content.Context
import android.webkit.WebView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.db.Ticket

abstract class Site(
    val context: Context
) {

    enum class STEP {
        NONE, MAIN, LOGIN, BOOKLIST, PARSE, DONE
    }

    enum class TYPE {
        INTERPARK, MELON, TICKETLINK, YES24
    }

    // 진행상태
    var step: STEP = STEP.NONE

    // 사이트 타입
    abstract val type: TYPE

    abstract val mainUrl: String            // 티켓사이트 메인
    abstract val loginUrl: String           // 로그인 페이지
    abstract val loginResultUrl: String     // 로그인결과 페이지
    abstract var bookListUrl: String        // 예매내역 페이지
    abstract val parseScript: String        // 예매내역 파싱 스크립트

    // 로그인 페이지로 이동
    abstract fun goLoginPage(webView: WebView)

    // 예매내역 페이지로 이동
    abstract fun goBookListPage(webView: WebView)

    // 예매내역 가져오기
    abstract fun doParsing(webView: WebView)

    // 예매내역 가져오기
    abstract fun getBookList(html: String): ArrayList<Ticket>

    // 예매내역 중복체크
    abstract fun verifyDuplicate(ticket: Ticket, list: ArrayList<Ticket>) : Boolean

    // 다음 진행상태로 변경
    fun nextStep() {
        when(step) {
            STEP.NONE -> step = STEP.MAIN
            STEP.MAIN -> step = STEP.LOGIN
            STEP.LOGIN -> step = STEP.BOOKLIST
            STEP.BOOKLIST -> step = STEP.PARSE
            STEP.PARSE -> step = STEP.DONE
            else -> {}
        }
    }

    fun getDateFormat(contents: String): String {
        if (contents.length < 10)
            return ""

        return if (contents.contains("년")) {
            val year = contents.trim().split("년")
            val month = year[1].trim().split("월")
            val day = month[1].trim().split("일")

            context.getString(R.string.ticket_date_value
                , year[0].toInt()
                , month[0].toInt()
                , day[0].toInt()
            )
        } else {
            contents.trim().substring(0, 10)
        }
    }

    fun getSite(): String {
        return when(type) {
            TYPE.INTERPARK -> "인터파크"
            TYPE.MELON -> "멜론"
            TYPE.TICKETLINK -> "티켓링크"
            TYPE.YES24 -> "yes24"
        }
    }
}