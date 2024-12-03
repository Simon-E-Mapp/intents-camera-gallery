package com.example.implicitintent


import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //Camera
    private lateinit var takePicture : ActivityResultLauncher<Uri>
    private lateinit var imageUri : Uri
    //Gallery
    private lateinit var getFromGallery : ActivityResultLauncher<String>
    private lateinit var profileView : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkCameraPermission()
        profileView = findViewById(R.id.image_profile)

        val cameraButton = findViewById<Button>(R.id.btn_camera)
        val galleryButton = findViewById<Button>(R.id.btn_image)
        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)

        button.setOnClickListener{
           surfTheWeb()
        }
        button2.setOnClickListener{
            callAFriend()
        }
        button3.setOnClickListener{
            maps()
        }
        button4.setOnClickListener{
            sendStuff()
        }
        button5.setOnClickListener{
            openMail()
        }
        cameraButton.setOnClickListener{
            imageUri = createImageUri()
            takePicture.launch(imageUri)
        }
        galleryButton.setOnClickListener{ //öppnar gallery för att välja ny bild
            getFromGallery.launch("image/*")
        }

        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()){
            success -> if(success){
                profileView.setImageURI(imageUri)
            }
        }

        getFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri : Uri? -> profileView.setImageURI(uri)
        }

    }

    fun callAFriend(){ //DIAL gör att telefonappen öppnas upp. URI = Universal Resource Identifier
        val telURI = Uri.parse("tel:0701234567")
        val callIntent = Intent(Intent.ACTION_DIAL, telURI)
        startActivity(callIntent)
    }

    fun surfTheWeb(){
        val webURI = Uri.parse("https://www.android.com")
        val webIntent = Intent(Intent.ACTION_VIEW, webURI)
        startActivity(webIntent)
    }

    fun maps(){
        val mapCoords = Uri.parse("geo: 59, 18")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapCoords)
        startActivity(mapIntent)
    }

    fun sendStuff(){
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hej från Tomaten")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun openMail() {
        val mailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_TEXT, "Hej från Gurkan")
        }
        val chooser = Intent.createChooser(mailIntent, "Välj en mail-klient")
        startActivity(chooser)
    }

    private fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 2)
    }

    private fun createImageUri(): Uri{
        val contentResolver : ContentResolver = this.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
    }
}