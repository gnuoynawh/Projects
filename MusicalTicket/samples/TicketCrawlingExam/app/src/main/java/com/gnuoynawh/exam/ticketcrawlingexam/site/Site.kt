package com.gnuoynawh.exam.ticketcrawlingexam.site

import android.webkit.WebView
import com.gnuoynawh.exam.ticketcrawlingexam.db.dao.Ticket

abstract class Site {

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
}