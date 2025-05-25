package com.example.visitingcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SavedCardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_cards)

        val savedCards = CardStorageHelper.getSavedCards(this)
        val container = findViewById<LinearLayout>(R.id.savedCardsContainer)

        savedCards.forEach { cardData ->
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(context, R.drawable.savecardbg)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 0, 16, 24)
                }
                elevation = 4f
                setPadding(24, 24, 24, 24)
            }

            val lines = cardData.split("\n")
            lines.forEach { line ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, 8, 0, 8)
                }

                val icon = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                        marginEnd = 16
                    }
                    when {
                        line.startsWith("Phone:", true) -> setImageResource(R.drawable.ic_phone)
                        line.startsWith("Address:", true) -> setImageResource(R.drawable.ic_address)
                        line.startsWith("Email:", true) -> setImageResource(R.drawable.ic_email)
                        line.startsWith("Instagram:", true) -> setImageResource(R.drawable.ic_instagram)
                        line.startsWith("Website:", true) -> setImageResource(R.drawable.ic_website)
                        line.startsWith("Occupation:", true) -> setImageResource(R.drawable.ic_occupation)

                        else -> layoutParams.width = 0
                    }
                }

                val textView = TextView(this).apply {
                    text = line
                    textSize = if (line.startsWith("Name:", true)) 18f else 16f
                    setTextColor(
                        if (line.startsWith("Name:", true))
                            ContextCompat.getColor(context, R.color.saveName)
                        else
                            ContextCompat.getColor(context, android.R.color.white)
                    )
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                    }

                    when {
                        line.startsWith("Phone:", true) -> {
                            setOnClickListener {
                                val phone = line.substringAfter(":").trim()
                                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                            }
                        }
                        line.startsWith("Email:", true) -> {
                            setOnClickListener {
                                val email = line.substringAfter(":").trim()
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$email")
                                }
                                startActivity(Intent.createChooser(intent, "Send Email"))
                            }
                        }
                        line.startsWith("Instagram:", true) -> {
                            setOnClickListener {
                                val username = line.substringAfter(":").trim()
                                val url = "https://instagram.com/$username"
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                        line.startsWith("Website:", true) -> {
                            setOnClickListener {
                                var url = line.substringAfter(":").trim()
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    url = "https://$url"
                                }
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                        line.startsWith("Address:", true) -> {
                            setOnClickListener {
                                val query = line.substringAfter(":").trim()
                                val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                try {
                                    startActivity(mapIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Google Maps not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                row.addView(icon)
                row.addView(textView)
                cardLayout.addView(row)
            }

            container.addView(cardLayout)
        }

        val noteText = TextView(this).apply {
            text = "* Phone, email, Instagram, website and address are clickable"
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            setPadding(8, 32, 8, 32)
        }
        container.addView(noteText)
    }
}
