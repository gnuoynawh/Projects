package com.gnuoynawh.musical.ticket.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
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

    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private val db by lazy {
        MTicketDatabase.getDatabase(this)?.getTicket()
    }

    private val btnCalendar: AppCompatButton by lazy {
        findViewById(R.id.btn_calendar)
    }

    private val btnList: AppCompatButton by lazy {
        findViewById(R.id.btn_list)
    }

    private val btnGetBookList: AppCompatButton by lazy {
        findViewById(R.id.btn_get_book_list)
    }

    private val btnAdd: AppCompatButton by lazy {
        findViewById(R.id.btn_add)
    }

    private val pager: ViewPager by lazy {
        findViewById(R.id.pager)
    }

    var ticketData: LiveData<List<Ticket>>? = null
    var tickets: List<Ticket> = ArrayList<Ticket>()

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var sitePopup: SitePopup

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_calendar ->
                pager.setCurrentItem(0, true)

            R.id.btn_list ->
                pager.setCurrentItem(1, true)

            R.id.btn_get_book_list -> {
                if (!sitePopup.isShowing) {
                    sitePopup.show()
                }
            }

            R.id.btn_add -> {
                val intent = Intent(this@MainActivity, TicketAddActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // button
        btnCalendar.setOnClickListener(this)
        btnList.setOnClickListener(this)
        btnGetBookList.setOnClickListener(this)
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

        // 권한체크
        if (checkPermission()) {
            initData()
        }
    }

    private fun initData() {
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

    fun deleteData(number: String) {
        lifecycleScope.launch {
            db?.delete(number)
            Toast.makeText(this@MainActivity, "삭제 완료 : $number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (checkPermission()) {
            initData()
        }
    }

    private fun checkPermission(): Boolean {
        if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            || (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            || (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이미지를 가져오기 위해서 카메라 및 저장소 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }

            requestPermissions(permissions, 2000)
            return false
        }

        return true
    }
}
