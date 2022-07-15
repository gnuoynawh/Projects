package com.gnuoynawh.exam.ticketcrawlingexam

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.exam.ticketcrawlingexam.data.Ticket
import java.text.SimpleDateFormat
import java.util.ArrayList

class ResultActivity: AppCompatActivity() {

    private var tickets: ArrayList<Ticket> = ArrayList<Ticket>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter

    private val tvCount: AppCompatTextView by lazy {
        findViewById(R.id.tv_count)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        tickets = intent.getSerializableExtra("result") as ArrayList<Ticket>

        ticketAdapter = TicketAdapter(this, tickets)
        ticketAdapter.setOnItemClickListener(object: TicketAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Toast.makeText(this@ResultActivity, "click! ${tickets[position].title}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = ticketAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        tvCount.text = "${tickets.size} 건"
    }
}