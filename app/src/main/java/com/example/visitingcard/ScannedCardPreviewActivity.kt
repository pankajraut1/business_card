package com.example.visitingcard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.visitingcard.R
import android.widget.Toast


class ScannedCardPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_card_preview)

        val cardData = intent.getStringExtra("cardData")

        val textView = findViewById<TextView>(R.id.scannedCardText)
        val saveButton = findViewById<Button>(R.id.saveCardButton)

        textView.text = cardData ?: "No data found"

        saveButton.setOnClickListener {
            cardData?.let {
                CardStorageHelper.saveCard(this, it)
                Toast.makeText(this, "Card saved successfully!", Toast.LENGTH_SHORT).show()
                finish() // Go back after saving
            }
        }

    }
}
