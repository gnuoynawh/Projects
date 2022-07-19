package com.gnuoynawh.musical.ticket.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.site.Site
import com.gnuoynawh.musical.ticket.db.MTicketDatabase
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.popup.SitePopup
import com.gnuoynawh.musical.ticket.popup.WebViewPopup
import com.gnuoynawh.musical.ticket.ui.adapter.PagerAdapter
import com.gnuoynawh.musical.ticket.ui.fragment.CalendarFragment
import com.gnuoynawh.musical.ticket.ui.fragment.TicketListFragment
import kotlinx.coroutines.launch

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

    private val pager: ViewPager by lazy {
        findViewById(R.id.pager)
    }

    private lateinit var pagerAdapter: PagerAdapter

    var ticketData: LiveData<List<Ticket>>? = null
    var tickets: List<Ticket> = ArrayList<Ticket>()

    private lateinit var sitePopup: SitePopup

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_calendar ->
                pager.setCurrentItem(0, true)

            R.id.btn_list ->
                pager.setCurrentItem(1, true)

            R.id.btn_add -> {
                if (!sitePopup.isShowing) {
                    sitePopup.show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // button
        btnCalendar.setOnClickListener(this)
        btnList.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        // viewpager
        pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(CalendarFragment(this).newInstance())
        pagerAdapter.addFragment(TicketListFragment(this).newInstance())
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = pagerAdapter.count

        // popup
        sitePopup = SitePopup(this)
        sitePopup.setOnItemSelectedClickListener(object : SitePopup.OnItemSelectedClickListener {
            override fun onItemClick(v: View, site: Site) {

                // 웹뷰 팝업
                val popup = WebViewPopup(this@MainActivity, site)
                popup.setOnCallBackListener(object : WebViewPopup.OnCallBackListener {
                    override fun onResult(list: ArrayList<Ticket>) {
                        Log.e("TEST", "onResult : ${list.size}")

                        // set data
                        lifecycleScope.launch {
                            db?.insert(*list.map {
                                Log.e("TEST", "insert Ticket = ${it.number}")
                                it
                            }.toTypedArray())

                            popup.dismiss()
                        }
                    }
                })
                popup.show()
            }
        })

        // data
        lifecycleScope.launch {
            ticketData = db?.getAllTickets()
            ticketData?.observe(this@MainActivity) {
                Log.e("TEST", "ticketData?.observe size = ${it.size}")
                tickets = it

                Toast.makeText(this@MainActivity, "업데이트 완료", Toast.LENGTH_SHORT).show()
                pagerAdapter.notifyDataSetChanged()
            }
        }
    }
}
