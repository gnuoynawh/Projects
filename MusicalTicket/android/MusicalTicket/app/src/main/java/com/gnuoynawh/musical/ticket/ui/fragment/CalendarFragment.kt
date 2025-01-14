package com.gnuoynawh.musical.ticket.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.ui.MainActivity
import com.gnuoynawh.musical.ticket.ui.adapter.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment(
    val activity: MainActivity
): Fragment() {

    lateinit var btnPrevious: AppCompatImageButton
    lateinit var btnNext: AppCompatImageButton
    lateinit var tvTitle: AppCompatTextView
    lateinit var recyclerView: RecyclerView

    lateinit var dateAdapter: CalendarAdapter

    fun newInstance() : CalendarFragment {
        return CalendarFragment(activity)
    }

    private val currentDate: Calendar = Calendar.getInstance()
    private val days = 42

    private var dateList: ArrayList<Date> = ArrayList<Date>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPrevious = view.findViewById(R.id.btn_previous)
        btnPrevious.setOnClickListener {
            changeCurrentDate(-1)
        }

        btnNext = view.findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            changeCurrentDate(1)
        }

        tvTitle = view.findViewById(R.id.tv_title)
        recyclerView = view.findViewById(R.id.recyclerview)

        initRecyclerView()
        updateCalendar()
    }

    private fun initRecyclerView() {
        dateAdapter = CalendarAdapter(activity, dateList, activity.tickets, currentDate.time)
        dateAdapter.setOnDateClickListener(object: CalendarAdapter.OnDateClickListener{
            @SuppressLint("SimpleDateFormat")
            override fun onClick(v: View, position: Int) {
                val fmt = SimpleDateFormat("yyyyMMdd")
                Toast.makeText(activity, "click!! : ${fmt.format(dateList[position].time)}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = dateAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 7, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun changeCurrentDate(value: Int) {
        currentDate.add(Calendar.MONTH, value)
        dateList.clear()
        updateCalendar()
    }

    private fun updateCalendar() {
        val calendar = currentDate.clone() as Calendar
        calendar[Calendar.DAY_OF_MONTH] = 1
        val monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 1

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (dateList.size < days) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH).plus(1)

        // update
        dateAdapter.updateData(dateList, currentDate.time)
        tvTitle.text = getString(R.string.calendar_title, year, month)
    }

    private val itemDecoration: RecyclerView.ItemDecoration = object: RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val totalSpanCount = getTotalSpanCount(parent)
            val spanSize = getItemSpanSize(parent, position)

            if (totalSpanCount == spanSize) {
                return
            }

            val spacing: Int = dpToPx(activity, 5.0f)

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