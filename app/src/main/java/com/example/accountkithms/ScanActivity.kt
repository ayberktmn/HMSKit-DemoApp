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
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.HwLocationType
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import com.huawei.hms.site.api.model.TextSearchRequest
import com.huawei.hms.site.api.model.TextSearchResponse


class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

        companion object {
            const val REQUEST_CODE_SCAN = 123
            const val CAMERA_PERMISSION_REQUEST = 124          //bu sabit sayilar sayesinde kodun daha rahat yonetilmesi saglaniyor
            const val REQUEST_CODE_PHOTO = 125
            const val STORAGE_PERMISSION_REQUEST= 126
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityScanBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnDefaultScan.setOnClickListener {
                if (checkCameraPermission() && checkGaleryPermission()) {
                    startScan()
                    startGalery()
                } else {
                    requestCameraPermission()
                    requestGaleryPermission()
                }
            }
        }

    private fun copyToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = android.content.ClipData.newPlainText("URL", textToCopy)
        clipboardManager.setPrimaryClip(clipData) // Kopyalamayi saglar
        Toast.makeText(this, "URL Kopyalandı", Toast.LENGTH_SHORT).show()
    }

        private fun checkCameraPermission(): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun checkGaleryPermission(): Boolean {
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

    private fun requestGaleryPermission() {
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

    private fun startGalery() {
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
                    startScan()
                }
                else {
                    Toast.makeText(
                        this,
                        "Kamera izni verilmedi. Tarama işlemi gerçekleştirilemez.",
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
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO && data != null) {
                Toast.makeText(this, "Resim Secildi", Toast.LENGTH_SHORT).show()
                val path = getImagePath(this@ScanActivity, data)
                if (TextUtils.isEmpty(path)) {
                    return
                }
                // Obtain the bitmap image from the image path.
                val bitmap = ScanUtil.compressBitmap(this@ScanActivity, path)
                // Call the decodeWithBitmap method to pass the bitmap image.
                val result1 = ScanUtil.decodeWithBitmap(this@ScanActivity, bitmap, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(false).create())
                // Obtain the scanning result.
                if (result1 != null && result1.size > 0) {
                    Toast.makeText(this, "QR Kod Bulundu: $result1", Toast.LENGTH_SHORT).show()
                    if (!TextUtils.isEmpty(result1[0].getOriginalValue())) {
                        Toast.makeText(this, result1[0].getOriginalValue(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            }
        }

    private fun getImagePath(context: Context, data: Intent): String? {
        var imagePath: String? = null
        val uri = data.data
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                imagePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return imagePath
    }


