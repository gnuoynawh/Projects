package com.gnuoynawh.exam.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class IntroActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        findViewById<AppCompatButton>(R.id.btn_calendar).setOnClickListener {
            val intent = Intent(this@IntroActivity, CalendarActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.btn_calendar_type).setOnClickListener {
            val intent = Intent(this@IntroActivity, CalendarTypeActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.btn_test).setOnClickListener {
            val intent = Intent(this@IntroActivity, TestActivity::class.java)
            startActivity(intent)
        }
    }
}