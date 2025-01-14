package com.gnuoynawh.musical.ticket.common

import android.app.Application
import com.gnuoynawh.musical.ticket.db.MTicketDatabase

class MTicketApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // DB init
        MTicketDatabase.getDatabase(this)

    }
}