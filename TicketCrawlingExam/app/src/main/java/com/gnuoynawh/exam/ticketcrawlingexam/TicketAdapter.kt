package com.gnuoynawh.exam.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    var context: Context,
    var list: ArrayList<Date>,
    var events: ArrayList<Ticket>,
    var today: Date)
    : RecyclerView.Adapter<CalendarAdapter.CalendarCellView>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarCellView {
        val layout =  LayoutInflater.from(context).inflate(R.layout.row_calendar, parent, false)
        return CalendarCellView(layout)
    }

    override fun onBindViewHolder(holder: CalendarCellView, position: Int) {
        val date: Date = list[position]
        val thisMonth = today.year.equals(date.year) && today.month.equals(date.month)

        // 날짜
        holder.tvDay.text = date.date.toString()
        holder.tvDay.setTextColor(if(thisMonth) Color.BLACK
                                  else          Color.GRAY)

        // 이미지
        holder.ivThumb.visibility = View.INVISIBLE
        events.forEachIndexed { _, ticket ->
            if (isSameDay(date, ticket.date)) {
                holder.ivThumb.visibility = View.VISIBLE
                holder.ivThumb.setImageResource(ticket.img)

                if(!thisMonth)
                    holder.ivThumb.alpha = 0.5f

                holder.tvDay.setTextColor(Color.WHITE)
            }
        }

        // 리스너
        holder.itemView.setOnClickListener {
            listener?.onClick(it, position)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd")
        return fmt.format(date1).equals(fmt.format(date2))
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun updateData(list: ArrayList<Date>, today: Date) {
        this.today = today
        this.list = list
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun updateEvents(events: ArrayList<Ticket>) {
        this.events = events
        this.notifyDataSetChanged()
    }

    interface OnDateClickListener {
        fun onClick(v: View, position: Int)
    }

    private var listener: OnDateClickListener? = null

    public fun setOnDateClickListener(listener: OnDateClickListener) {
        this.listener = listener
    }

    class CalendarCellView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: AppCompatTextView = itemView.findViewById(R.id.tv_date)
        val ivThumb: AppCompatImageView = itemView.findViewById(R.id.iv_thumb)
    }
}