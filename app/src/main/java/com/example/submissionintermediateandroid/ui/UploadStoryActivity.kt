package com.example.submissionintermediateandroid.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionintermediateandroid.R
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.databinding.ActivityUploadStoryBinding
import com.example.submissionintermediateandroid.ui.viewmodels.UploadStoryViewModel
import com.example.submissionintermediateandroid.ui.viewmodels.ViewModelFactory
import com.example.submissionintermediateandroid.util.Constants.getImageUri
import com.example.submissionintermediateandroid.util.Constants.reduceFileImage
import com.example.submissionintermediateandroid.util.Constants.uriToFile
import com.example.submissionintermediateandroid.util.dataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadStoryActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null
    private val uploadStoryViewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(this, application.dataStore)
    }
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityUploadStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            uploadStory()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast(getString(R.string.media_selected))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.imgPhoto.setImageURI(it)
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.descriptionEditText.text.toString()
            val latitude = location?.latitude.toString()
            val longitude = location?.longitude.toString()

            showLoading(true)

            val requestBodyDesc = description.toRequestBody("text/plain".toMediaType())
            val requestBodyLat = latitude.toRequestBody("text/plain".toMediaType())
            val requestBodyLon = longitude.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            // check switch (add location or not)
            if (binding.switchAddLocation.isChecked) {
                uploadStoryViewModel.addStoryWithLocation(
                    multipartBody,
                    requestBodyDesc,
                    requestBodyLat,
                    requestBodyLon
                )
            } else {
                uploadStoryViewModel.addStory(
                    multipartBody,
                    requestBodyDesc
                )
            }

            // observing add story response
            uploadStoryViewModel.story.observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val response = result.data
                            showToast(response.message!!)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showToast("Error: ${result.error}")
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    //showLocation(location)
                    this.location = location
                } else {
                    Toast.makeText(
                        this@UploadStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}