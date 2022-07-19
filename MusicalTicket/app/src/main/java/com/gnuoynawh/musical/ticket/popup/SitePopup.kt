package com.gnuoynawh.musical.ticket.popup

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.gnuoynawh.exam.ticketcrawlingexam.site.InterPark
import com.gnuoynawh.exam.ticketcrawlingexam.site.Melon
import com.gnuoynawh.exam.ticketcrawlingexam.site.TicketLink
import com.gnuoynawh.exam.ticketcrawlingexam.site.Yes24
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.Site

class SitePopup(
    context: Context
): Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    interface OnItemSelectedClickListener {
        fun onItemClick(v: View, site: Site)
    }

    class Builder(var context: Context) {

        private lateinit var view: View

        private var listener: OnItemSelectedClickListener? = null

        fun setOnItemSelectedClickListener(listener: OnItemSelectedClickListener) : Builder {
            this.listener = listener
            return this
        }

        fun build(): SitePopup {
            val dialog = SitePopup(context)

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.popup_site, null)

            view.findViewById<AppCompatButton>(R.id.btn_interpark).setOnClickListener {
                listener?.onItemClick(it, InterPark())
                dialog.dismiss()
            }

            view.findViewById<AppCompatButton>(R.id.btn_melon).setOnClickListener {
                listener?.onItemClick(it, Melon())
                dialog.dismiss()
            }

            view.findViewById<AppCompatButton>(R.id.btn_ticketlink).setOnClickListener {
                listener?.onItemClick(it, TicketLink())
                dialog.dismiss()
            }

            view.findViewById<AppCompatButton>(R.id.btn_yes24).setOnClickListener {
                listener?.onItemClick(it, Yes24())
                dialog.dismiss()
            }

            dialog.setContentView(view)
            return dialog
        }
    }
}