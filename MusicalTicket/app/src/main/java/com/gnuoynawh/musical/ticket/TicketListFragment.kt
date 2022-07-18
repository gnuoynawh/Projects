package com.gnuoynawh.musical.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gnuoynawh.musical.ticket.databinding.FragmentCalendarBinding
import com.gnuoynawh.musical.ticket.databinding.FragmentTicketListBinding

class TicketListFragment: Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTicketListBinding

    fun newInstance() : TicketListFragment {
        return TicketListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //return inflater.inflate(R.layout.fragment_ticket_list, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ticket_list, container, false)

        activity?.let {
            binding.viewModel= viewModel
            binding.lifecycleOwner = this
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTicketTitle.text = "티켓리스트 페이지 입니다요"

    }
}