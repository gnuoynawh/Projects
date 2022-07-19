package com.gnuoynawh.musical.ticket.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.db.Converters
import com.gnuoynawh.musical.ticket.db.Ticket
import java.text.SimpleDateFormat

class TicketListAdapter(
    var context: Context,
    var list: List<Ticket>
): RecyclerView.Adapter<TicketListAdapter.TicketView>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketView {
        val layout =  LayoutInflater.from(context).inflate(R.layout.row_ticket_list, parent, false)
        return TicketView(layout)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: TicketView, position: Int) {
        val ticket: Ticket = list[position]

        // image
        Glide.with(context)
            .load(ticket.thumb)
            .error(R.color.black)
            .into(holder.ivThumb)

        // inform
        holder.tvNumber.text = ticket.number
        holder.tvTitle.text = ticket.title
        holder.tvDate.text = ticket.date
        holder.tvPlace.text = ticket.place
        holder.tvCount.text = ticket.count

        // 리스너
        holder.itemView.setOnClickListener {
            listener?.onClick(it, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun updateData(list: List<Ticket>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    private var listener: OnItemClickListener? = null

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class TicketView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: AppCompatImageView = itemView.findViewById(R.id.iv_thumb)
        val tvNumber: AppCompatTextView = itemView.findViewById(R.id.tv_number)
        val tvTitle: AppCompatTextView = itemView.findViewById(R.id.tv_title)
        val tvDate: AppCompatTextView = itemView.findViewById(R.id.tv_date)
        val tvPlace: AppCompatTextView = itemView.findViewById(R.id.tv_place)
        val tvCount: AppCompatTextView = itemView.findViewById(R.id.tv_count)
    }
}