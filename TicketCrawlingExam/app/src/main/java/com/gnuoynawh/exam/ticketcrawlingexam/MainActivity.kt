package com.gnuoynawh.exam.ticketcrawlingexam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.gnuoynawh.exam.ticketcrawlingexam.site.SiteType

class MainActivity  : AppCompatActivity(), View.OnClickListener {

    /**
     * Todo
     *  - 티켓링크 : 예약정보가 없음... ㅠㅠ
     *  - 예스24 : 10개 넘어갈때 페이징처리, 장소정보가 없음
     */
    lateinit var btn1: AppCompatButton
    lateinit var btn2: AppCompatButton
    lateinit var btn3: AppCompatButton
    lateinit var btn4: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn4 = findViewById(R.id.btn_4)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent = Intent(this, WebViewActivity::class.java)

        when (v.id) {
            R.id.btn_1 -> intent.putExtra("site", SiteType.InterPark)
            R.id.btn_2 -> intent.putExtra("site", SiteType.Melon)
            R.id.btn_3 -> intent.putExtra("site", SiteType.TicketLink)
            R.id.btn_4 -> intent.putExtra("site", SiteType.Yes24)
        }

        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}