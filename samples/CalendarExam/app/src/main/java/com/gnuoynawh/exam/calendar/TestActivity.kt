package com.gnuoynawh.exam.calendar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.gnuoynawh.exam.calendar.view.OverlapImageView
import com.gnuoynawh.exam.calendar.view.TypeImageView

class TestActivity: AppCompatActivity(), View.OnClickListener {

    private val imageView1: TypeImageView by lazy {
        findViewById(R.id.imageview1)
    }

    private val imageView2: TypeImageView by lazy {
        findViewById(R.id.imageview2)
    }

    private val imageView3: TypeImageView by lazy {
        findViewById(R.id.imageview3)
    }

    private lateinit var btnVertical: AppCompatButton
    private lateinit var btnHorizontal: AppCompatButton
    private lateinit var btnDiagonal: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        btnVertical = findViewById(R.id.btn_vertical)
        btnVertical.setOnClickListener(this)
        btnHorizontal = findViewById(R.id.btn_horizontal)
        btnHorizontal.setOnClickListener(this)
        btnDiagonal = findViewById(R.id.btn_diagonal)
        btnDiagonal.setOnClickListener(this)

        imageView1.drawType = TypeImageView.DrawType.VERTICAL
        imageView2.drawType = TypeImageView.DrawType.HORIZONTAL
        imageView3.drawType = TypeImageView.DrawType.DIAGONAL
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_vertical -> {
                imageView1.drawType = TypeImageView.DrawType.VERTICAL
                imageView1.invalidate()
            }
            R.id.btn_horizontal -> {
                imageView1.drawType = TypeImageView.DrawType.HORIZONTAL
                imageView1.invalidate()
            }
            R.id.btn_diagonal -> {
                imageView1.drawType = TypeImageView.DrawType.DIAGONAL
                imageView1.invalidate()
            }
        }
    }
}