package sk.banik.finance.bill_to_toshl

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CAMERA = 0
    }

    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private var qrCodeFoundButton: Button? = null
    private var qrCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.activity_main_previewView)

        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton)
        qrCodeFoundButton?.visibility = View.INVISIBLE
        qrCodeFoundButton?.isClickable = false

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        requestAndRunCamera()
    }

    private fun requestAndRunCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView!!.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        val preview : Preview = Preview.Builder().build()
        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            QRCodeImageDecoder(object : QRCodeDecodingListener {
                override fun onQRCodeDecodedSuccessfully(qrCode: String?) {
                    this@MainActivity.qrCode = qrCode
                    qrCodeFoundButton!!.text = getString(R.string.qr_code_found, this@MainActivity.qrCode)
                    qrCodeFoundButton!!.visibility = View.VISIBLE
                }

                override fun onQRCodeDecodeFailed() {
                    qrCodeFoundButton!!.visibility = View.INVISIBLE
                }
            })
        )

        val camera = cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, imageAnalysis, preview)
    }
}