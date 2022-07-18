package com.gnuoynawh.musical.ticket

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gnuoynawh.musical.ticket.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.btnCalendar.setOnClickListener {
            changeFragment(calendar = true)
        }
        binding.btnList.setOnClickListener {
            changeFragment(calendar = false)
        }

        // init
        changeFragment(calendar = true)
    }

    fun changeFragment(calendar: Boolean) {
        viewModel.changeFragment(this,
            if(calendar) CalendarFragment().newInstance()
            else         TicketListFragment().newInstance()
        )
    }
}