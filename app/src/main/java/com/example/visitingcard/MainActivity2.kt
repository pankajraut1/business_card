package com.example.visitingcard

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {

    lateinit var submit: Button
    lateinit var name: EditText
    lateinit var occ: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var instagram: EditText
    lateinit var website: EditText
    lateinit var address: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("UserCardData", MODE_PRIVATE)
        val savedName = sharedPref.getString("Name", null)

        if (!savedName.isNullOrEmpty()) {
            val intent = Intent(this, VisitingCard::class.java)
            intent.putExtra("Name", sharedPref.getString("Name", ""))
            intent.putExtra("Occupation", sharedPref.getString("Occupation", ""))
            intent.putExtra("Email", sharedPref.getString("Email", ""))
            intent.putExtra("Phone", sharedPref.getString("Phone", ""))
            intent.putExtra("Instagram", sharedPref.getString("Instagram", ""))
            intent.putExtra("Website", sharedPref.getString("Website", ""))
            intent.putExtra("Address", sharedPref.getString("Address", ""))
            startActivity(intent)
            finish()
        }

        submit = findViewById(R.id.submit)
        name = findViewById(R.id.name)
        occ = findViewById(R.id.occupation)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phoneno)
        instagram = findViewById(R.id.instagram)
        website = findViewById(R.id.website)
        address = findViewById(R.id.address)

        submit.setOnClickListener {
            val nameStr = name.text.toString().trim()
            val occStr = occ.text.toString().trim()
            val emailStr = email.text.toString().trim()
            val phoneStr = phone.text.toString().trim()
            val instagramStr = instagram.text.toString().trim()
            val websiteStr = website.text.toString().trim()
            val addressStr = address.text.toString().trim()

            val noDigitsRegex = Regex(".*\\d.*")

            if (nameStr.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (occStr.isEmpty()) {
                Toast.makeText(this, "Please enter your occupation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneStr.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (noDigitsRegex.matches(nameStr)) {
                Toast.makeText(this, "Name cannot contain numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (noDigitsRegex.matches(occStr)) {
                Toast.makeText(this, "Occupation cannot contain numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!phoneStr.matches(Regex("^\\d{10}$"))) {
                Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (emailStr.isNotEmpty() && !emailStr.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (websiteStr.isNotEmpty() && !websiteStr.matches(Regex("^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$"))) {
                Toast.makeText(this, "Please enter a valid website URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VisitingCard::class.java)
            intent.putExtra("Name", nameStr)
            intent.putExtra("Occupation", occStr)
            intent.putExtra("Email", emailStr)
            intent.putExtra("Phone", phoneStr)
            intent.putExtra("Instagram", instagramStr)
            intent.putExtra("Website", websiteStr)
            intent.putExtra("Address", addressStr)

            val editor = getSharedPreferences("UserCardData", MODE_PRIVATE).edit()
            editor.putString("Name", nameStr)
            editor.putString("Occupation", occStr)
            editor.putString("Email", emailStr)
            editor.putString("Phone", phoneStr)
            editor.putString("Instagram", instagramStr)
            editor.putString("Website", websiteStr)
            editor.putString("Address", addressStr)
            editor.apply()

            startActivity(intent)
        }
    }
}
