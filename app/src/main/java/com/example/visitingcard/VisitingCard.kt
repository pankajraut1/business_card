package com.example.visitingcard

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

class VisitingCard : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var occ: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var instagram: TextView
    private lateinit var website: TextView
    private lateinit var address: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_visiting_card)

        name = findViewById(R.id.nameText)
        occ = findViewById(R.id.occupation)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phoneno)
        instagram = findViewById(R.id.instagram)
        website = findViewById(R.id.website)
        address = findViewById(R.id.address)

        val btnQr = findViewById<Button>(R.id.btnQr)
        val viewSavedBtn = findViewById<Button>(R.id.viewSavedCardsBtn)
        val scanQrBtn = findViewById<Button>(R.id.scanQrBtn)
        val sharePdfBtn = findViewById<Button>(R.id.sharePdfBtn)
        val shareImageBtn = findViewById<Button>(R.id.shareImageBtn)
        val editBtn = findViewById<Button>(R.id.editBtn)

        val nameStr = intent.getStringExtra("Name") ?: ""
        val occStr = intent.getStringExtra("Occupation") ?: ""
        val emailStr = intent.getStringExtra("Email") ?: ""
        val phoneStr = intent.getStringExtra("Phone") ?: ""
        val instagramStr = intent.getStringExtra("Instagram") ?: ""
        val websiteStr = intent.getStringExtra("Website") ?: ""
        val addressStr = intent.getStringExtra("Address") ?: ""

        name.text = nameStr
        occ.text = occStr
        email.text = emailStr
        phone.text = phoneStr
        instagram.text = instagramStr
        website.text = websiteStr
        address.text = addressStr

        // Hide empty fields
        email.visibility = if (emailStr.isBlank()) TextView.GONE else TextView.VISIBLE
        phone.visibility = if (phoneStr.isBlank()) TextView.GONE else TextView.VISIBLE
        instagram.visibility = if (instagramStr.isBlank()) TextView.GONE else TextView.VISIBLE
        website.visibility = if (websiteStr.isBlank()) TextView.GONE else TextView.VISIBLE
        address.visibility = if (addressStr.isBlank()) TextView.GONE else TextView.VISIBLE

        phone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.text}")))
        }

        email.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${email.text}"))
            startActivity(Intent.createChooser(emailIntent, "Send email"))
        }

        website.setOnClickListener {
            val url = if (websiteStr.startsWith("http")) websiteStr else "https://$websiteStr"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        instagram.setOnClickListener {
            val url = if (instagramStr.startsWith("http")) instagramStr else "https://instagram.com/$instagramStr"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        btnQr.setOnClickListener {
            val dataToEncode = """
                Name: $nameStr
                Occupation: $occStr
                Phone: $phoneStr
                Address: $addressStr
                Email: $emailStr
                Instagram: $instagramStr
                Website: $websiteStr
                
            """.trimIndent()

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(dataToEncode, BarcodeFormat.QR_CODE, 600, 600)

            val imageView = ImageView(this)
            imageView.setImageBitmap(bitmap)

            AlertDialog.Builder(this)
                .setTitle("Scan this QR Code")
                .setView(imageView)
                .setPositiveButton("Close", null)
                .show()
        }

        viewSavedBtn.setOnClickListener {
            startActivity(Intent(this, SavedCardsActivity::class.java))
        }

        scanQrBtn.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        sharePdfBtn.setOnClickListener {
            generateAndSharePDF(nameStr, occStr, phoneStr, addressStr, emailStr, instagramStr, websiteStr)
        }

        shareImageBtn.setOnClickListener {
            val cardView = findViewById<RelativeLayout>(R.id.card)

            val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            cardView.draw(canvas)

            val file = File(cacheDir, "shared_card.png")
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share Business Card as Image"))
        }

        editBtn.setOnClickListener {
            getSharedPreferences("UserCardData", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun generateAndSharePDF(
        name: String,
        occ: String,
        email: String,
        phone: String,
        instagram: String,
        website: String,
        address: String
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#e67e22")
        }
        canvas.drawRect(0f, 0f, pageInfo.pageWidth.toFloat(), pageInfo.pageHeight.toFloat(), backgroundPaint)

        val paintText = Paint().apply {
            textSize = 30f
            color = Color.WHITE
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        var y = 120f
        val startX = 50f

        fun drawField(label: String, value: String) {
            if (value.isNotBlank()) {
                canvas.drawText("$label: $value", startX, y, paintText)
                y += 55f
            }
        }

        drawField("Name", name)
        drawField("Occupation", occ)
        drawField("Address", address)
        drawField("Phone", phone)
        drawField("Email", email)
        drawField("Instagram", instagram)
        drawField("Website", website)


        pdfDocument.finishPage(page)

        val safeName = name.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(cacheDir, "${safeName}_card.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Business Card as PDF"))
    }
}
