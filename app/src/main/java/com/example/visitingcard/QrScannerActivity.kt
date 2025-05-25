package com.example.visitingcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class QrScannerActivity : AppCompatActivity() {

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val intent = Intent(this, ScannedCardPreviewActivity::class.java)
            intent.putExtra("cardData", result.contents)
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = ScanOptions()
        options.setPrompt("Scan a business card QR")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        options.captureActivity = CaptureActivityPortrait::class.java
        barcodeLauncher.launch(options)
    }
}
