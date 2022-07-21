package com.gnuoynawh.musical.ticket.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.ui.MainActivity
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCount = view.findViewById(R.id.tv_count)
        tvCount.text = "${activity.tickets.size} 건"

        recyclerView = view.findViewById(R.id.recyclerView)
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

        val itemTouchHelper = ItemTouchHelper(swipeDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    val swipeDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val position = viewHolder.adapterPosition
            val ticket = listAdapter.getData(position)
            activity.deleteData(ticket.number)

        }

        
    }


}