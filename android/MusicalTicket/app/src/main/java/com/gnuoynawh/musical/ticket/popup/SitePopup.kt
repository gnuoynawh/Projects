package com.gnuoynawh.musical.ticket.popup

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.*

class SitePopup(
    context: Context
): Dialog(context, R.style.Dialog_Style) {

    init {
        setContentView(R.layout.popup_site)
        setCancelable(true)

        findViewById<AppCompatButton>(R.id.btn_interpark).setOnClickListener {
            listener?.onItemClick(it, InterPark(context))
            dismiss()
        }

        findViewById<AppCompatButton>(R.id.btn_melon).setOnClickListener {
            listener?.onItemClick(it, Melon(context))
            dismiss()
        }

        findViewById<AppCompatButton>(R.id.btn_ticketlink).setOnClickListener {
            listener?.onItemClick(it, TicketLink(context))
            dismiss()
        }

        findViewById<AppCompatButton>(R.id.btn_yes24).setOnClickListener {
            listener?.onItemClick(it, Yes24(context))
            dismiss()
        }
    }

    private var listener: OnItemSelectedClickListener? = null

    interface OnItemSelectedClickListener {
        fun onItemClick(v: View, site: Site)
    }

    fun setOnItemSelectedClickListener(listener: OnItemSelectedClickListener) {
        this.listener = listener
    }
}