package com.gnuoynawh.musical.ticket.ui.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.databinding.FragmentCalendarBinding
import com.gnuoynawh.musical.ticket.ui.MainActivity
import com.gnuoynawh.musical.ticket.ui.model.MainViewModel
import com.gnuoynawh.musical.ticket.ui.adapter.CalendarAdapter

class CalendarFragment(
    val mainActivity: MainActivity
): Fragment() {

    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: FragmentCalendarBinding
    lateinit var dateAdapter: CalendarAdapter

    fun newInstance() : CalendarFragment {
        return CalendarFragment(mainActivity)
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

        binding.btnPrevious.setOnClickListener {
            viewModel.changeCurrentDate(this, -1)
        }

        binding.btnNext.setOnClickListener {
            viewModel.changeCurrentDate(this, 1)
        }

        //
        dateAdapter = CalendarAdapter(mainActivity, viewModel)
        binding.recyclerview.apply {
            adapter = dateAdapter
            layoutManager = GridLayoutManager(activity, 7, RecyclerView.VERTICAL, false)
            addItemDecoration(itemDecoration)
        }

        //
        viewModel.updateCalendar(this)
    }

    private val itemDecoration: RecyclerView.ItemDecoration = object: RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val totalSpanCount = getTotalSpanCount(parent)
            val spanSize = getItemSpanSize(parent, position)

            if (totalSpanCount == spanSize) {
                return
            }

            val spacing: Int = dpToPx(mainActivity, 5.0f)

            outRect.top = if (isInTheFirstRow(position, totalSpanCount)) 0 else spacing
            outRect.left = spacing / 2
            outRect.right = spacing / 2
        }

        private fun isInTheFirstRow(position: Int, spanCount: Int): Boolean {
            return position < spanCount
        }

        private fun isFirstInRow(position: Int, spanCount: Int): Boolean {
            return position % spanCount == 0
        }

        private fun isLastInRow(position: Int, spanCount: Int): Boolean {
            return isFirstInRow(position + 1, spanCount)
        }

        private fun getTotalSpanCount(parent: RecyclerView): Int {
            val layoutManager = parent.layoutManager
            return if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
        }

        private fun getItemSpanSize(parent: RecyclerView, position: Int): Int {
            val layoutManager = parent.layoutManager
            return if (layoutManager is GridLayoutManager) layoutManager.spanSizeLookup.getSpanSize(position) else 1
        }

        private fun dpToPx(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}