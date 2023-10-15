package com.example.accountkithms

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.accountkithms.databinding.ActivityScanBinding
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

    companion object {
        const val REQUEST_CODE_SCAN = 123
        const val CAMERA_PERMISSION_REQUEST = 124
        const val REQUEST_CODE_PHOTO = 125
        const val STORAGE_PERMISSION_REQUEST = 126
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDefaultScan.setOnClickListener {
            if (checkCameraPermission()) {
                if (checkGalleryPermission()) {
                    startScan()
                } else {
                    requestGalleryPermission()
                }
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun copyToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = android.content.ClipData.newPlainText("URL", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "URL Kopyalandı", Toast.LENGTH_SHORT).show()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST
        )
    }

    private fun startScan() {
        ScanUtil.startScan(
            this,
            REQUEST_CODE_SCAN,
            HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkGalleryPermission()) {
                    startScan()
                } else {
                    requestGalleryPermission()
                }
            } else {
                Toast.makeText(
                    this,
                    "Kamera izni verilmedi. Tarama işlemi gerçekleştirilemez.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan()
            } else {
                Toast.makeText(
                    this,
                    "Galeri izni verilmedi. Tarama işlemi gerçekleştirilemez.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SCAN && data != null) {
            val obj = data.getParcelableExtra<HmsScan?>(ScanUtil.RESULT)
            if (obj != null && !TextUtils.isEmpty(obj.originalValue)) {
                copyToClipboard(obj.originalValue)
                Toast.makeText(this, "URL Kopyalandı: ${obj.originalValue}", Toast.LENGTH_SHORT)
                    .show()
                Toast.makeText(this, obj.originalValue, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
