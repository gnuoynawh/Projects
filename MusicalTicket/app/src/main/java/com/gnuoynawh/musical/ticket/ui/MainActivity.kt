package com.gnuoynawh.musical.ticket.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.databinding.ActivityMainBinding
import com.gnuoynawh.musical.ticket.ui.fragment.CalendarFragment
import com.gnuoynawh.musical.ticket.ui.fragment.TicketListFragment
import com.gnuoynawh.musical.ticket.ui.model.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
        binding.viewModel = viewModel

        binding.btnCalendar.setOnClickListener {
            changeFragment(true)
        }

        binding.btnList.setOnClickListener {
            changeFragment(false)
        }

        binding.btnAdd.setOnClickListener {
            
        }

        // init
        changeFragment(true)
    }

    private fun changeFragment(calendar: Boolean) {
        val fragment = if(calendar) CalendarFragment(this).newInstance()
                       else         TicketListFragment().newInstance()

        supportFragmentManager.beginTransaction().replace(R.id.body, fragment).commit()
    }
}