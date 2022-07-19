package com.gnuoynawh.musical.ticket.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.ui.MainActivity
import com.gnuoynawh.musical.ticket.ui.adapter.CalendarAdapter
import com.gnuoynawh.musical.ticket.ui.adapter.TicketListAdapter

class TicketListFragment(
    val activity: MainActivity
): Fragment() {

    lateinit var tvCount: AppCompatTextView
    lateinit var recyclerView: RecyclerView

    lateinit var listAdapter: TicketListAdapter

    fun newInstance() : TicketListFragment {
        return TicketListFragment(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_ticket_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCount = view.findViewById(R.id.tv_count)
        tvCount.text = "0 건"
        recyclerView = view.findViewById(R.id.recyclerview)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        listAdapter = TicketListAdapter(activity, activity.tickets)
        listAdapter.setOnItemClickListener(object: TicketListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Toast.makeText(activity, "click! ${activity.tickets[position].title}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun updateData(list: List<Ticket>) {
        listAdapter.updateData(list)
        tvCount.text = "${list.size} 건"
    }
}