package com.gnuoynawh.exam.ticketcrawlingexam

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import java.util.*

class TicketAdapter(
    var context: Context,
    var list: ArrayList<Ticket>
): RecyclerView.Adapter<TicketAdapter.TicketView>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketView {
        val layout =  LayoutInflater.from(context).inflate(R.layout.row_ticket, parent, false)
        return TicketView(layout)
    }

    override fun onBindViewHolder(holder: TicketView, position: Int) {
        val ticket: Ticket = list[position]

        // image
        Glide.with(context)
            .load(ticket.thumb)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.ivThumb)

        // inform
        holder.tvTitle.text = ticket.title
        holder.tvDate.text = ticket.date
        holder.tvPlace.text = ticket.place
        holder.tvNumber.text = ticket.count

        // 리스너
        holder.itemView.setOnClickListener {
            listener?.onClick(it, position)
        }
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
        val tvTitle: AppCompatTextView = itemView.findViewById(R.id.tv_title)
        val tvDate: AppCompatTextView = itemView.findViewById(R.id.tv_date)
        val tvPlace: AppCompatTextView = itemView.findViewById(R.id.tv_place)
        val tvNumber: AppCompatTextView = itemView.findViewById(R.id.tv_number)
    }
}