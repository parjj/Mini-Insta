package com.example.instagalleria.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.instagalleria.R
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.UploadImage
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CameraFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE: Int = 1
    val REQUEST_IMAGE_FROM_PHONE: Int = 2

    private lateinit var take_photo: ImageButton
    private lateinit var upload_photo: ImageButton
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_left: ImageButton
    private lateinit var toolbar_right: ImageButton
    private lateinit var frameL1: FrameLayout
    private lateinit var frameL2: FrameLayout
    private lateinit var linearL: LinearLayout
    private lateinit var progressBar: ProgressBar


    //file paths
    private lateinit var currentPhotoPath: String
    lateinit var filePathUri: Uri
    private lateinit var photoUri: Uri
    private lateinit var photoIntent: Intent


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.camera_layout, container, false)

        take_photo = view.findViewById(R.id.takePhoto)
        upload_photo = view.findViewById(R.id.uploadPhoto)
        frameL1 = view.findViewById(R.id.frame1)
        frameL2 = view.findViewById(R.id.frame2)
        progressBar = view.findViewById(R.id.progress_bar)

        //toolbar activity
        toolbar = view.findViewById(R.id.toolbar_layout)
        toolbar_left = view.findViewById(R.id.leftSide)
        toolbar_right = view.findViewById(R.id.rightSide)

        toolbar_left.setBackgroundResource(R.drawable.icons8_home_26)
        toolbar_left.scaleX = 0.1f
        toolbar_left.scaleX = 0.1f
        toolbar_right.setBackgroundResource(R.drawable.icons8_camera_26)
        toolbar_right.scaleX = 0.1f
        toolbar_right.scaleX = 0.1f

        //take photo button
        take_photo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openPhoneCamera()
            }
        })

        //upload button
        upload_photo.setOnClickListener({ v -> openFile() })


        toolbar_left.setOnClickListener(View.OnClickListener { l ->
            fragmentManager!!.popBackStack()
        })
        return view

    }

    //upload button functions
    fun openFile() {
        var intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FROM_PHONE);
    }

    //take photo button functions
    private fun openPhoneCamera() {

        photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (photoIntent.resolveActivity(context!!.packageManager) != null) {
            try {
                getPhotoUri()
            } catch (exce: Exception) {
                exce.printStackTrace()
            }
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)   // check without using this uri  //1
            //
            //The answer to this related question explains that when a URI is passed as EXTRA_OUTPUT
            // on the ACTION_IMAGE_CAPTURE intent, the URI is not returned as data on the intent parameter to onActivityResult().
            //
            //This means you must save the URI in a class variable when it is generated so that it is available in onActivityResult().
            // It appears you already have photoURI declared as a class variable and that you
            // intended to define it's value with this code in onLaunchCamera(): so hence using the getUri function and file provider
            startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE)
        }

    }

    //file provider function for photoURi path
    fun getPhotoUri(): Uri {
        var photoFile: File? = null

        //File storageDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        storageDir!!.mkdirs()

        // Create an image file name      p
        photoFile = File.createTempFile(
            "photo",
            ".jpg",
            storageDir
        )
        Log.d("photo file ", photoFile!!.toString())

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.absolutePath
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                context!!,
                "com.example.instagalleria.fileprovider",
                photoFile
            )
            this.photoUri = photoURI
        }
        return photoUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == REQUEST_IMAGE_CAPTURE) {             // on take photo button click
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

                try {

                    filePathUri = photoUri
                    setImageView(filePathUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == REQUEST_IMAGE_FROM_PHONE) {                   // on upload button click
            if (resultCode == RESULT_OK) {
                if (data != null && data.data != null) {
                    filePathUri = data.data                                        //on single image select
                    setImageView(filePathUri)

                } else if (data.getClipData() != null) {                            // on multiple image select
                    var imageUrisList = ArrayList<Uri>()

                    var count = data.getClipData().getItemCount();
                    for (i in 0 until count) {
                        var imageUri = data.getClipData().getItemAt(i).getUri();
                        //filePathUri=imageUri
                        imageUrisList.add(imageUri)
                        //val imgBitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, imageUri)
                    }
                    setGridView(imageUrisList)
                }
            }
        }
    }

    fun setImageView(uri: Uri) {
        var bundle = Bundle()

        //  bundle.putParcelable("bitmap",bitmap)
        bundle.putString("uri_string", uri.toString())
        // bundle.putString("photo_path", str)

        callPhotoLibrary(bundle)
    }

    fun setGridView(imageUrisList: ArrayList<Uri>) {

        var bundle = Bundle()
        bundle.putSerializable("upload_image", imageUrisList)
        callPhotoLibrary(bundle)
    }

    fun callPhotoLibrary(bundle: Bundle) {

        var transaction = fragmentManager!!.beginTransaction()
        var photoLibrary = PhotoLibrary()
        photoLibrary.arguments = bundle
        transaction.add(R.id.fragment_container, photoLibrary, "photo_library")
        transaction.addToBackStack(null)
        transaction.commit()
    }


}

//------------------------------------------------------------------------------------------------------------------------------

//issue on multiple select as images gets loaded twice


fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    var bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    var path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
    var uri = Uri.parse(path);
    return uri
}

//    override fun onStop() {
//        super.onStop()
//        storageRef.child("uploads/").delete()
//    }


//The reason why we have saved image paths in the database is because firebase storage do not provide any api to list files or folders.
// The only way to get the storage files is by keeping an index of files in our database.
