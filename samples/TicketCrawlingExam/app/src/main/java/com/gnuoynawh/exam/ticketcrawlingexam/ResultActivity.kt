package com.gnuoynawh.exam.ticketcrawlingexam

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.exam.ticketcrawlingexam.db.TicketDatabase
import com.gnuoynawh.exam.ticketcrawlingexam.db.dao.Ticket
import kotlinx.coroutines.launch


class ResultActivity: AppCompatActivity() {

    private val db by lazy {
        TicketDatabase.getDatabase(this)?.getTicket()
    }

    var ticketData: LiveData<List<Ticket>>? = null

    private var tickets: List<Ticket> = ArrayList<Ticket>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter

    private val tvCount: AppCompatTextView by lazy {
        findViewById(R.id.tv_count)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // tickets = intent.getSerializableExtra("result") as ArrayList<Ticket>

        ticketAdapter = TicketAdapter(this, tickets)
        ticketAdapter.setOnItemClickListener(object: TicketAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Toast.makeText(this@ResultActivity, "click! ${tickets[position].title}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = ticketAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            ticketData = db?.getAllTickets()
            ticketData?.observe(this@ResultActivity) {

                Log.e("TEST", "size = ${it.size}")

                updateData(it)
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun updateData(list: List<Ticket>) {
        tickets = list

        ticketAdapter.updateData(tickets)
        tvCount.text = "${tickets.size} 건"
    }
}