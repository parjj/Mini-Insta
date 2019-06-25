package com.example.instagalleria

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.provider.MediaStore
import android.graphics.Bitmap
import android.R.attr.data
import java.io.IOException
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.UUID.randomUUID
import android.app.ProgressDialog
import android.widget.*
import java.util.*


class MainActivity : AppCompatActivity() {


    val REQUEST_IMAGE_CAPTURE: Int = 1
    val REQUEST_IMAGE_CAPTURE_VIAINTENT: Int = 2

    private lateinit var take_photo: Button
    private lateinit var upload_photo: Button
    private lateinit var upload_button: Button
    private lateinit var camera_photo: ImageView
    private lateinit var rl1: RelativeLayout
    private lateinit var rl2: RelativeLayout
    private lateinit var lly1: LinearLayout
    private lateinit var lly2: LinearLayout
    //   private lateinit var toolbar_buttons: Toolbar

    private lateinit var filePath: Uri
    private lateinit var photoPath: Uri
    private lateinit var photoUri: Uri
    private lateinit var photoIntent: Intent

    val storage = FirebaseStorage.getInstance()

    // Create a storage reference from our app
    var storageRef: StorageReference = storage.getReference("uploads/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        take_photo = findViewById(R.id.takePhoto)
        upload_photo = findViewById(R.id.uploadPhoto)
        camera_photo = findViewById(R.id.cameraImg)
        upload_button = findViewById(R.id.uploadButton)
        rl1 = findViewById(R.id.rr1)
        rl2 = findViewById(R.id.rr2)
        lly1 = findViewById(R.id.ll1)
        lly2 = findViewById(R.id.ll2)
        //   toolbar_buttons = findViewById(R.id.toolbar)


        take_photo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openFile()
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == REQUEST_IMAGE_CAPTURE_VIAINTENT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                if (data != null && data.data != null) {
                    filePath = data.data
                    try {
                        lly2.visibility = View.VISIBLE
                        lly1.visibility = View.GONE
                        camera_photo.visibility = View.VISIBLE
                        upload_button.visibility = View.VISIBLE
                        /*rl1.visibility = View.GONE
                        rl2.visibility = View.GONE*/

                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                        camera_photo.setImageBitmap(bitmap)
                        upload_button.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                upload()

                            }
                        })


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun openFile() {
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_VIAINTENT)

    }

    fun upload() {
        if (filePath != null) {

            val ref = storageRef.child(  UUID.randomUUID().toString())
            ref.putFile(filePath)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                    Toast.makeText(this@MainActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(this@MainActivity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                })
                .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    Toast.makeText(this@MainActivity, "Uploaded " +progress.toInt() + "%", Toast.LENGTH_SHORT).show()
                })
        }


    }
}





