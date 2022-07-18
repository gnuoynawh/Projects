package com.gnuoynawh.exam.ticketcrawlingexam.common

import android.app.Application
import com.gnuoynawh.exam.ticketcrawlingexam.db.TicketDatabase

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // DB init
        TicketDatabase.getDatabase(this)

    }
}