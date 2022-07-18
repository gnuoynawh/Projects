package com.gnuoynawh.musical.ticket.ui.model

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.ui.fragment.CalendarFragment
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val currentDate: Calendar = Calendar.getInstance()
    val days = 42

    var dateList: ArrayList<Date> = ArrayList<Date>()

    fun changeCurrentDate(fragment: CalendarFragment, value: Int) {
        currentDate.add(Calendar.MONTH, value)
        dateList.clear()
        updateCalendar(fragment)
    }

    fun updateCalendar(fragment: CalendarFragment) {

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
        fragment.dateAdapter.updateData()
        fragment.binding.tvTitle.text = fragment.getString(R.string.calendar_title, year, month)
    }

    fun onCalendarClick(v: View, position: Int) {
        val fmt = SimpleDateFormat("yyyyMMdd")
        Toast.makeText(getApplication(), "click!! : ${fmt.format(dateList[position].time)}", Toast.LENGTH_SHORT).show()
    }
}