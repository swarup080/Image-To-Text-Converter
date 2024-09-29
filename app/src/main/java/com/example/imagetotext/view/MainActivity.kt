package com.example.imagetotext.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imagetotext.R
import com.example.imagetotext.adapter.SavedDataAdapter
import com.example.imagetotext.databinding.ActivityMainBinding
import com.example.imagetotext.model.SavedImageData
import com.example.imagetotext.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: SavedDataAdapter
    private var imageUri: Uri? = null

    // Activity Result Launchers
    private val cameraResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImageCapture(result)
        }

    private val galleryResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImagePick(result)
        }

    private val permissionsResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("Permissions", "All required permissions are granted.")
                selectImage()
            } else {
                Log.d("Permissions", "Not all required permissions are granted.")
                Toast.makeText(this, "Permissions are required to use this feature", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        adapter = SavedDataAdapter()

        // Handle image selection
        binding.btnSelectImage.setOnClickListener {
            Log.d("MainActivity", "Select Image button clicked.")
            checkPermissions()
        }
        binding.btnSave.setOnClickListener {
            Log.d("MainActivity", "Save button clicked.")
            saveData()
        }

        // Set up RecyclerView
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe ViewModel data
        viewModel.allData.observe(this) { data ->
            if (data.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                adapter.submitList(data)
            }
        }
    }


    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )

        // Request permissions
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            selectImage()
        } else {
            permissionsResultLauncher.launch(permissionsToRequest)
        }
    }


    private fun selectImage() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraResultLauncher.launch(takePictureIntent)
                    }
                    1 -> {
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryResultLauncher.launch(pickPhoto)
                    }
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun handleImageCapture(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            imageUri = getImageUriFromBitmap(imageBitmap)
            binding.imageView.setImageBitmap(imageBitmap)
            imageUri?.let { recognizeTextFromImage(it) }
        } else {
            Log.e("MainActivity", "Image capture result was not OK. Result code: ${result.resultCode}")
        }
    }

    private fun handleImagePick(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            binding.imageView.setImageURI(imageUri)
            imageUri?.let { recognizeTextFromImage(it) }
        } else {
            Log.e("MainActivity", "Image pick result was not OK. Result code: ${result.resultCode}")
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun recognizeTextFromImage(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    binding.recognizedText.text = visionText.text
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Text recognition failed: ${e.message}")
                    Toast.makeText(
                        this,
                        "Failed to recognize text: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error processing image: ${e.message}")
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveData() {
        val text = binding.recognizedText.text.toString()
        val imagePath = imageUri?.toString() ?: ""

        if (text.isNotEmpty() && imagePath.isNotEmpty()) {
            val savedData = SavedImageData(text = text, imagePath = imagePath)
            viewModel.insert(savedData)
            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
            binding.imageView.setImageResource(R.drawable.placeholder)
            binding.recognizedText.text = "Recognized text will appear here"
        } else {
            Toast.makeText(this, "Text or Image missing", Toast.LENGTH_SHORT).show()
        }
    }
}
