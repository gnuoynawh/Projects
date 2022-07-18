package com.gnuoynawh.musical.ticket.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.ui.model.MainViewModel
import com.gnuoynawh.musical.ticket.view.TypeImageView
import java.util.*

class CalendarAdapter(
    val context: Context,
    val viewmodel : MainViewModel,
): RecyclerView.Adapter<CalendarAdapter.CalendarCellView>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class CalendarCellView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: AppCompatTextView = itemView.findViewById(R.id.tv_date)
        val ivThumb: AppCompatImageView = itemView.findViewById(R.id.iv_thumb)
        val ivThumbType: TypeImageView = itemView.findViewById(R.id.iv_thumb_type)
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun updateData() {
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return viewmodel.dateList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarCellView {
        val itemView = inflater.inflate(R.layout.row_calendar, parent, false)
        return CalendarCellView(itemView)
    }

    override fun onBindViewHolder(holder: CalendarCellView, position: Int) {

        val date: Date = viewmodel.dateList[position]
        val thisMonth = viewmodel.currentDate.time.year == date.year
                && viewmodel.currentDate.time.month == date.month

        // 날짜
        holder.tvDay.text = date.date.toString()
        holder.tvDay.setTextColor(
            if(thisMonth) Color.BLACK
            else          Color.GRAY
        )

        // 이미지
        /*holder.ivThumbType.drawType = TypeImageView.DrawType.DIAGONAL
        events.forEachIndexed { _, ticket ->
            if (isSameDay(date, ticket.date)) {

                if (holder.ivThumb.drawable == null) {
                    Log.e("TEST", "111 [$date] = ${ticket.img}")
                    holder.ivThumb.setImageResource(ticket.img)
                } else {
                    Log.e("TEST", "222 [$date] = ${ticket.img}")
                    holder.ivThumbType.setImageResource(ticket.img)
                }

                if(!thisMonth) {
                    holder.ivThumb.alpha = 0.5f
                    holder.ivThumbType.alpha = 0.5f
                }

                holder.tvDay.setTextColor(Color.WHITE)
            }
        }*/

        // 리스너
        holder.itemView.setOnClickListener {
            viewmodel.onCalendarClick(it, position)
            return@setOnClickListener
        }
    }
}