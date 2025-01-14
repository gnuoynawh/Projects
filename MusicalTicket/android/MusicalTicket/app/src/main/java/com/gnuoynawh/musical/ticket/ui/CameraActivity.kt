package com.gnuoynawh.musical.ticket.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.gnuoynawh.musical.ticket.R
import java.io.*
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    enum class CAMERA {
        FRONT, BACK
    }

    private var cameraType: CAMERA = CAMERA.BACK

    // layout
    private lateinit var previewView: PreviewView
    private lateinit var ivResult: AppCompatImageView
    private lateinit var layoutTake: LinearLayout
    private lateinit var btnTake: AppCompatButton
    private lateinit var btnChange: AppCompatButton
    private lateinit var layoutDone: LinearLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var btnNext: AppCompatButton

    // camera
    private var imageCapture: ImageCapture? = null
    private var outputFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera)
        setLayout()

        // 카메라 초기화
        startCamera()
    }

    /**
     * 화면 초기화
     */
    private fun setLayout() {

        // 촬영
        previewView = findViewById(R.id.previewView)
        layoutTake = findViewById(R.id.layout_take)
        layoutTake.visibility = View.VISIBLE
        btnTake = findViewById(R.id.btn_take)
        btnTake.setOnClickListener(mBtnClickListener)
        btnChange = findViewById(R.id.btn_change_camera)
        btnChange.setOnClickListener(mBtnClickListener)

        // 결과
        ivResult = findViewById(R.id.iv_result)
        ivResult.visibility = View.GONE
        layoutDone = findViewById(R.id.layout_done)
        layoutDone.visibility = View.GONE
        btnRetry = findViewById(R.id.btn_retry)
        btnRetry.setOnClickListener(mBtnClickListener)
        btnNext = findViewById(R.id.btn_next)
        btnNext.setOnClickListener(mBtnClickListener)

    }

    /**
     * 버튼 클릭 이벤트
     */
    private val mBtnClickListener = View.OnClickListener {
        when(it.id) {
            R.id.btn_take -> takePicture()
            R.id.btn_change_camera -> {
                cameraType = if(cameraType == CAMERA.FRONT) CAMERA.BACK else CAMERA.FRONT
                startCamera()
            }
            R.id.btn_retry -> retryCamera()
            R.id.btn_next -> done()
        }
    }

    /**
     * 카메라 초기화
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this
                    , if(cameraType == CAMERA.FRONT) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                    , preview
                    , imageCapture).also {
                        it.cameraControl.enableTorch(false) // flash off
                    }

            } catch(exc: Exception) {
                Log.e("TEST", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 카메라 촬영
     */
    private fun takePicture() {

        // Get a stable reference of the modifiable image capture use case
        imageCapture?.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                resultImage(image)
                super.onCaptureSuccess(image)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })
    }

    /**
     * 카메라 촬영 후 결과 처리
     */
    private fun resultImage(image: ImageProxy) {

        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        val bitmap = imageProxyToBitmap(image)

        // output to File
        convertBitmapToFile(fileName, bitmap)

        // UI
        previewView.visibility = View.INVISIBLE
        ivResult.visibility = View.VISIBLE
        ivResult.setImageBitmap(bitmap)

        layoutTake.visibility = View.GONE
        layoutDone.visibility = View.VISIBLE
    }

    /**
     * 촬영 결과물 파일로 저장
     */
    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap) {

        val storageDir = File(filesDir.path + "/photo")
        if (!storageDir.exists())
            storageDir.mkdir()

        //create a file to write bitmap data
        outputFile = File(storageDir, fileName)
        outputFile!!.createNewFile()

        //convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(outputFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 촬영 결과물을 bitmap 으로 변경
     * imageProxy To bitmap
     */
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return byteArrayToBitmap(rotateImageWithMirror(bytes, 90))
        // return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 재촬영
     */
    private fun retryCamera() {

        previewView.visibility = View.VISIBLE
        ivResult.visibility = View.GONE
        ivResult.setImageBitmap(null)

        layoutTake.visibility = View.VISIBLE
        layoutDone.visibility = View.GONE

        startCamera()
    }

    /**
     * 화면 종료
     */
    private fun done() {
        val it = Intent()
        it.putExtra("filePath", outputFile?.path)
        setResult(RESULT_OK, it)
        finish()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun rotateImageWithMirror(data: ByteArray, angle: Int): ByteArray {
        var bmp = BitmapFactory.decodeByteArray(data, 0, data.size, null)
        val mat = Matrix()

        // 프론트 카메라일때만 mirror
        if (cameraType == CAMERA.FRONT) {
            mat.preScale(-1f, 1f)
        }

        mat.postRotate(angle.toFloat())
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
