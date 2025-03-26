package com.gnuoynawh.ticket.sample.textreader

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gnuoynawh.ticket.sample.textreader.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService

    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.imageCaptureButton.setOnClickListener {
            if (viewBinding.viewFinder.visibility == View.VISIBLE) {
                takePhoto()
            } else {
                reTake(true, null, null)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun reTake(retake: Boolean, text: String?, bitmap: Bitmap?) {
        cameraExecutor.shutdown()
        runOnUiThread {
            if (retake) {
                viewBinding.textView.text = ""
                viewBinding.textView.visibility = View.GONE
                viewBinding.imageView.visibility = View.GONE
                viewBinding.imageView.setBackgroundResource(0)

                viewBinding.viewFinder.visibility = View.VISIBLE
                viewBinding.imageCaptureButton.text = getString(R.string.take_photo)
            } else {
                viewBinding.textView.text = text ?: ""
                viewBinding.textView.visibility = View.VISIBLE
                viewBinding.imageView.visibility = View.VISIBLE
                Glide.with(applicationContext)
                    .load(bitmap)
                    .into(viewBinding.imageView)

                viewBinding.viewFinder.visibility = View.GONE
                viewBinding.imageCaptureButton.text = getString(R.string.re_take)
            }
        }
    }

    private fun takePhoto() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        imageCapture?.apply {
            takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e(TAG, "Photo capture failed: ${exception.message}")
                    reTake(true, null, null)
                }

                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)
                    Log.e(TAG, "Photo capture success")

                    val bitmap =
                        imageProxyToBitmap(imageProxy)?.rotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                    imageProxy.image?.let {
                        process(
                            InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees),
                            bitmap
                        )
                    }
                }
            })
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap =
        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)

    private fun process(image: InputImage, bitmap: Bitmap?) {

        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        recognizer.process(image)
            .addOnFailureListener { e ->
                Log.e(TAG, "recognizer.process err : ${e.message}")
            }
            .addOnSuccessListener { resultText ->
                var values = ""
                for (block in resultText.textBlocks) {
                    val blockText = block.text
//                    val blockCornerPoints = block.cornerPoints
//                    val blockFrame = block.boundingBox

                    Log.e(TAG, "=======================")
                    Log.e(TAG, "blockText : $blockText")

                    for (line in block.lines) {
                        val lineText = line.text
//                        val lineCornerPoints = line.cornerPoints
//                        val lineFrame = line.boundingBox

//                        for (element in line.elements) {
//                            val elementText = element.text
//                            val elementCornerPoints = element.cornerPoints
//                            val elementFrame = element.boundingBox
//                        }

                        Log.e(TAG, "lineText : $lineText")
                        values += "\n"
                        values += lineText
                    }
                }

                reTake(false, values, bitmap)
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
