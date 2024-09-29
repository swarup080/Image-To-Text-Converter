package com.example.imagetotext.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.imagetotext.databinding.ActivityYourScansBinding

class YourScansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYourScansBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding setup
        binding = ActivityYourScansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val text = intent.getStringExtra("TEXT")

        // Bind data to UI
        binding.recognizedText.text = text
        Glide.with(this)
            .load(imagePath)
            .into(binding.imageView)
        // Set up click listener to copy text to clipboard
        binding.recognizedText.setOnClickListener {
            copyToClipboard(text ?: "")
        }
    }
    // Function to copy text to clipboard
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Recognized Text", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}