package com.gnuoynawh.musical.ticket.ui

import android.app.Activity
import android.content.Intent
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
import com.bumptech.glide.Glide
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

class TicketModifyActivity: AppCompatActivity(), View.OnClickListener {

    private val db by lazy {
        MTicketDatabase.getDatabase(this)?.getTicket()
    }

    private val btnModify: AppCompatButton by lazy {
        findViewById(R.id.btn_modify)
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

    private val tvNumber: AppCompatTextView by lazy {
        findViewById(R.id.tv_number)
    }

    private val tvDate: AppCompatTextView by lazy {
        findViewById(R.id.tv_date)
    }

    private val edtTitle: AppCompatEditText by lazy { findViewById(R.id.edt_title) }
    private val edtSite: AppCompatEditText by lazy { findViewById(R.id.edt_site) }
    private val edtCount: AppCompatEditText by lazy { findViewById(R.id.edt_count) }
    private val edtPlace: AppCompatEditText by lazy { findViewById(R.id.edt_place) }

    private var filePath = ""
    private var fileType = ""

    private var ticket: Ticket? = null

    override fun onClick(v: View) {

        when(v.id) {
            R.id.btn_modify -> updateData()
            R.id.btn_take -> takePicture()
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
        setContentView(R.layout.activity_modify_ticket)

        btnModify.setOnClickListener(this)
        btnTake.setOnClickListener(this)
        btnAddThumb.setOnClickListener(this)
        btnRemoveThumb.setOnClickListener(this)
        tvDate.setOnClickListener(this)

        ticket = intent.getSerializableExtra("ticket") as Ticket
        setData()

    }

    private fun setData() {
        edtTitle.setText(ticket?.title)
        edtSite.setText(ticket?.site)
        tvNumber.text = ticket?.number
        tvDate.text = ticket?.date
        edtPlace.setText(ticket?.place)
        edtCount.setText(ticket?.count)

        filePath = ticket?.image.toString()
        fileType = ticket?.imageType.toString()

        Glide.with(this)
            .load(
                if (ticket?.imageType == Constants.IMAGE_TYPE_FILE)
                    File(filePath)
                else
                    filePath
            ).error(R.color.black)
            .into(ivThumb)
    }

    private fun updateData() {
        ticket?.title = edtTitle.text.toString()
        ticket?.site = edtSite.text.toString()
        ticket?.number = tvNumber.text.toString()
        ticket?.date = tvDate.text.toString()
        ticket?.place = edtPlace.text.toString()
        ticket?.count = edtCount.text.toString()

        ticket?.image = filePath
        ticket?.imageType = fileType

        lifecycleScope.launch {
            db?.insert(ticket!!)
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

        if (ticket?.imageType == Constants.IMAGE_TYPE_FILE) {
            val file = File(filePath)
            file.delete()
        }

        filePath = ""
        fileType = ""
    }

    private var takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

            val path = it.data?.getStringExtra("filePath")
            path?.let {

                Glide.with(this)
                    .load(File(path))
                    .error(R.color.black)
                    .into(ivThumb)

                filePath = path
                fileType = Constants.IMAGE_TYPE_FILE

            }
        }
    }

    private var addImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {

            // uri to File
            val file = File(it.data?.data.toString())
            val outputFile = newFile()
            Files.copy(file.toPath(), outputFile.toPath());

            val path = outputFile.path
            Glide.with(this)
                .load(File(path))
                .error(R.color.black)
                .into(ivThumb)

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