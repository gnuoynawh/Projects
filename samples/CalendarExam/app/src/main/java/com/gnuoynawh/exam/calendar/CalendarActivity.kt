package com.gnuoynawh.exam.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.gnuoynawh.exam.calendar.adapter.CalendarAdapter
import com.gnuoynawh.exam.calendar.data.Ticket
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity(), View.OnClickListener {

    private val currentDate = Calendar.getInstance()
    private val DAYS_COUNT = 42

    private var dateList: ArrayList<Date> = ArrayList<Date>()
    private var events: ArrayList<Ticket> = ArrayList<Ticket>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var dateAdapter: CalendarAdapter

    private lateinit var tvTitle: AppCompatTextView
    private lateinit var btnPrevious: AppCompatImageButton
    private lateinit var btnNext: AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        tvTitle = findViewById(R.id.tv_title)
        btnPrevious = findViewById(R.id.btn_previous)
        btnPrevious.setOnClickListener(this)
        btnNext = findViewById(R.id.btn_next)
        btnNext.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerview)

        initRecyclerView()
        updateCalendar()
        updateEvents()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_previous -> changeCurrentDate(-1)
            R.id.btn_next -> changeCurrentDate(1)
        }
    }

    private fun changeCurrentDate(value: Int) {
        currentDate.add(Calendar.MONTH, value)
        dateList.clear()
        updateCalendar()
    }

    private fun initRecyclerView() {
        dateAdapter = CalendarAdapter(this, dateList, events, currentDate.time)
        dateAdapter.setOnDateClickListener(object: CalendarAdapter.OnDateClickListener{
            @SuppressLint("SimpleDateFormat")
            override fun onClick(v: View, position: Int) {
                val fmt = SimpleDateFormat("yyyyMMdd")
                Toast.makeText(this@CalendarActivity, "click!! : ${fmt.format(dateList[position].time)}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = dateAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 7, VERTICAL, false)
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun updateEvents() {
        val cal = Calendar.getInstance()

        cal.set(2022, Calendar.JULY, 1)
        events.add(Ticket(cal.time, R.drawable.img_aida))

        cal.set(2022, Calendar.JULY, 6)
        events.add(Ticket(cal.time, R.drawable.img_death_note))

        cal.set(2022, Calendar.JULY, 15)
        events.add(Ticket(cal.time, R.drawable.img_kinky_boots))

        cal.set(2022, Calendar.JULY, 25)
        events.add(Ticket(cal.time, R.drawable.img_death_note))

        cal.set(2022, Calendar.JULY, 25)
        events.add(Ticket(cal.time, R.drawable.img_man_who_laughs))

        dateAdapter.updateEvents(events)
    }

    @SuppressLint("StringFormatInvalid")
    private fun updateCalendar() {
        val calendar = currentDate.clone() as Calendar
        calendar[Calendar.DAY_OF_MONTH] = 1
        val monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 1

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (dateList.size < DAYS_COUNT) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // update
        dateAdapter.updateData(dateList, currentDate.time)

        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH).plus(1)
        tvTitle.text = getString(R.string.calendar_title, year, month)
    }

    private val itemDecoration: ItemDecoration = object: ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val totalSpanCount = getTotalSpanCount(parent)
            val spanSize = getItemSpanSize(parent, position)

            if (totalSpanCount == spanSize) {
                return
            }

            val spacing: Int = dpToPx(this@CalendarActivity, 5.0f)

            outRect.top = if (isInTheFirstRow(position, totalSpanCount)) 0 else spacing
            outRect.left = spacing / 2
            outRect.right = spacing / 2

        }

        private fun isInTheFirstRow(position: Int, spanCount: Int): Boolean {
            return position < spanCount
        }

        private fun isFirstInRow(position: Int, spanCount: Int): Boolean {
            return position % spanCount == 0
        }

        private fun isLastInRow(position: Int, spanCount: Int): Boolean {
            return isFirstInRow(position + 1, spanCount)
        }

        private fun getTotalSpanCount(parent: RecyclerView): Int {
            val layoutManager = parent.layoutManager
            return if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
        }

        private fun getItemSpanSize(parent: RecyclerView, position: Int): Int {
            val layoutManager = parent.layoutManager
            return if (layoutManager is GridLayoutManager) layoutManager.spanSizeLookup.getSpanSize(position) else 1
        }

        private fun dpToPx(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}