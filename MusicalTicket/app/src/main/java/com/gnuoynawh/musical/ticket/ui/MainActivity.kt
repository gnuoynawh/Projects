package com.gnuoynawh.musical.ticket.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.gnuoynawh.exam.ticketcrawlingexam.site.InterPark
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.MTicketDatabase
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.popup.SitePopup
import com.gnuoynawh.musical.ticket.popup.WebViewPopup
import com.gnuoynawh.musical.ticket.ui.fragment.CalendarFragment
import com.gnuoynawh.musical.ticket.ui.fragment.TicketListFragment
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val db by lazy {
        MTicketDatabase.getDatabase(this)?.getTicket()
    }

    private val btnCalendar: AppCompatButton by lazy {
        findViewById(R.id.btn_calendar)
    }

    private val btnList: AppCompatButton by lazy {
        findViewById(R.id.btn_list)
    }

    private val btnAdd: AppCompatButton by lazy {
        findViewById(R.id.btn_add)
    }

    private val calendarFragment = CalendarFragment(this).newInstance()
    private val ticketListFragment = TicketListFragment(this).newInstance()

    var ticketData: LiveData<List<Ticket>>? = null
    var tickets: List<Ticket> = ArrayList<Ticket>()

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_calendar -> changeFragment(calendarFragment)
            R.id.btn_list -> {
                Log.e("TEST", "onClick <btn_list>")
                changeFragment(ticketListFragment)
            }
            R.id.btn_add -> {
                Log.e("TEST", "onClick <btn_add>")

                // 웹뷰 팝업
                WebViewPopup(this@MainActivity, InterPark())
                    .show(supportFragmentManager, "WebViewPopup")
//                // 사이트 선택 팝업
//                SitePopup.Builder(this)
//                    .setOnItemSelectedClickListener(object : SitePopup.OnItemSelectedClickListener {
//                        override fun onItemClick(v: View, site: Site) {
//
//                            // 웹뷰 팝업
//                            WebViewPopup(this@MainActivity, site)
//                                .show(supportFragmentManager, "WebViewPopup")
//                        }
//                    })
//                    .build()
//                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCalendar.setOnClickListener(this)
        btnList.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        // data
        lifecycleScope.launch {
            ticketData = db?.getAllTickets()
            ticketData?.observe(this@MainActivity) {
                Log.e("TEST", "size = ${it.size}")
                tickets = it

                //ticketListFragment.updateData(tickets)
            }
        }

        // init
        // changeFragment(calendarFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.body, fragment)
            .commit()
    }

}
