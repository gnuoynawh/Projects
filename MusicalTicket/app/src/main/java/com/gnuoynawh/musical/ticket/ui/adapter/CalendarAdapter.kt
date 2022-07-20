package com.gnuoynawh.musical.ticket.ui.adapter

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
import com.bumptech.glide.Glide
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.Constants
import com.gnuoynawh.musical.ticket.db.Converters
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.view.TypeImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    var context: Context,
    var list: List<Date>,
    var events: List<Ticket>,
    var today: Date
): RecyclerView.Adapter<CalendarAdapter.CalendarCellView>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarCellView {
        val itemView = inflater.inflate(R.layout.row_calendar, parent, false)
        return CalendarCellView(itemView)
    }

    override fun onBindViewHolder(holder: CalendarCellView, position: Int) {

        val date: Date = list[position]
        val thisMonth = today.year == date.year && today.month == date.month

        // 날짜
        holder.tvDay.text = date.date.toString()
        holder.tvDay.setTextColor(
            if(thisMonth) Color.BLACK
            else          Color.GRAY
        )

        // 이미지 default
        holder.ivThumb.setImageResource(0)
        holder.ivThumbType.setImageResource(0)
        holder.ivThumbType.drawType = TypeImageView.DrawType.DIAGONAL

        // 이미지 설정
        events.forEachIndexed { _, ticket ->
            if (isSameDay(date, ticket.date)) {

                Glide.with(context)
                    .load(
                        if (ticket.imageType == Constants.IMAGE_TYPE_FILE)
                            File(ticket.image)
                        else
                            ticket.image
                    ).error(R.color.black)
                    .into(
                        if (holder.ivThumb.drawable == null)
                            holder.ivThumb
                        else
                            holder.ivThumbType
                    )

                if(!thisMonth) {
                    holder.ivThumb.alpha = 0.5f
                    holder.ivThumbType.alpha = 0.5f
                }

                holder.tvDay.setTextColor(Color.WHITE)
            }
        }

        // 리스너
        holder.itemView.setOnClickListener {
            listener?.onClick(it, position)
            return@setOnClickListener
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun isSameDay(date1: Date, date2: String): Boolean {
        return SimpleDateFormat("yyyy.MM.dd").format(date1).equals(date2)
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
        val ivThumbType: TypeImageView = itemView.findViewById(R.id.iv_thumb_type)
    }

}