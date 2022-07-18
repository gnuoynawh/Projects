package com.gnuoynawh.musical.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnuoynawh.musical.ticket.databinding.FragmentCalendarBinding

class CalendarFragment: Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding

    fun newInstance() : CalendarFragment {
        return CalendarFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //return inflater.inflate(R.layout.fragment_calendar, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        activity?.let {
            binding.viewModel= viewModel
            binding.lifecycleOwner = this
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCalendarTitle.text = "캘린더 페이지 입니다요"

    }
}