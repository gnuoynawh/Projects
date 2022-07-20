package com.gnuoynawh.musical.ticket.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.gnuoynawh.musical.ticket.R
import com.gnuoynawh.musical.ticket.common.Constants
import com.gnuoynawh.musical.ticket.db.MTicketDatabase
import com.gnuoynawh.musical.ticket.db.Ticket
import com.gnuoynawh.musical.ticket.popup.DatePickerPopup
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*

class AddTicketActivity: AppCompatActivity(), View.OnClickListener {

    private val db by lazy {
        MTicketDatabase.getDatabase(this)?.getTicket()
    }

    private val btnAdd: AppCompatButton by lazy {
        findViewById(R.id.btn_add)
    }

    private val btnTake: AppCompatButton by lazy {
        findViewById(R.id.btn_take)
    }

    private val btnAddThumb: AppCompatButton by lazy {
        findViewById(R.id.btn_add_thumb)
    }

    private val btnRemoveThumb: AppCompatButton by lazy {
        findViewById(R.id.btn_remove_thumb)
    }

    private val ivThumb: AppCompatImageView by lazy {
        findViewById(R.id.iv_thumb)
    }

    private val tvDate: AppCompatTextView by lazy {
        findViewById(R.id.tv_date)
    }

    private val edtTitle: AppCompatEditText by lazy { findViewById(R.id.edt_title) }
    private val edtSite: AppCompatEditText by lazy { findViewById(R.id.edt_site) }
    private val edtNumber: AppCompatEditText by lazy { findViewById(R.id.edt_number) }
    private val edtCount: AppCompatEditText by lazy { findViewById(R.id.edt_count) }
    private val edtPlace: AppCompatEditText by lazy { findViewById(R.id.edt_place) }

    private var filePath = ""
    private var fileType = ""

    override fun onClick(v: View) {

        when(v.id) {
            R.id.btn_take -> takePicture()
            R.id.btn_add -> insertData()
            R.id.btn_add_thumb -> addImage()
            R.id.btn_remove_thumb -> removeImage()
            R.id.tv_date -> {
                DatePickerPopup.Builder(this)
                    .setOnDatePickerListener { year, monthOfYear, dayOfMonth ->
                        tvDate.text = getString(R.string.ticket_date_value, year, monthOfYear, dayOfMonth)
                        Log.e("TEST", "date = ${tvDate.text}")
                    }
                    .build().show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ticket)

        btnAdd.setOnClickListener(this)
        btnTake.setOnClickListener(this)
        btnAddThumb.setOnClickListener(this)
        btnRemoveThumb.setOnClickListener(this)
        tvDate.setOnClickListener(this)
    }

    private fun verify() : Boolean {
        when {
            edtTitle.text.isNullOrEmpty() -> {
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            edtSite.text.isNullOrEmpty() -> {
                Toast.makeText(this, "예매처를 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            edtNumber.text.isNullOrEmpty() -> {
                Toast.makeText(this, "예매번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            tvDate.text.isNullOrEmpty() -> {
                Toast.makeText(this, "관람일을 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            edtPlace.text.isNullOrEmpty() -> {
                Toast.makeText(this, "장소를 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            edtCount.text.isNullOrEmpty() -> {
                Toast.makeText(this, "매수를 입력하세요.", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {}
        }

        return false
    }

    private fun insertData() {

        if (verify())
            return

        val ticket = Ticket()
        ticket.title = edtTitle.text.toString()
        ticket.site = edtSite.text.toString()
        ticket.number = edtNumber.text.toString()
        ticket.date = tvDate.text.toString()
        ticket.place = edtPlace.text.toString()
        ticket.count = edtCount.text.toString()

        ticket.image = filePath
        ticket.imageType = fileType

        lifecycleScope.launch {
            db?.insert(ticket)
            finish()
        }
    }

    private fun takePicture() {
        val intent = Intent(this, CameraActivity::class.java)
        takePictureResultLauncher.launch(intent)
    }

    private fun addImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        addImageResultLauncher.launch(intent)

    }

    private fun removeImage() {
        ivThumb.setImageResource(0)

        val file = File(filePath)
        file.delete()

        filePath = ""
        fileType = ""
    }

    private var takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

            val path = it.data?.getStringExtra("filePath")
            ivThumb.setImageBitmap(BitmapFactory.decodeFile(path))

            filePath = path ?: ""
            fileType = Constants.IMAGE_TYPE_FILE
        }
    }

    private var addImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

            // uri to File
            val file = File(it.data?.data.toString())
            val outputFile = newFile()
            Files.copy(file.toPath(), outputFile.toPath());

            val path = outputFile.path
            ivThumb.setImageBitmap(BitmapFactory.decodeFile(path))

            filePath = path ?: ""
            fileType = Constants.IMAGE_TYPE_FILE
        }
    }

    private fun newFile() : File {
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"

        val storageDir = File(filesDir.path + "/photo")
        if (!storageDir.exists())
            storageDir.mkdir()

        //create a file to write bitmap data
        val outputFile = File(storageDir, fileName)
        outputFile.createNewFile()

        return outputFile
    }
}