package com.example.instagalleria.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.instagalleria.MainActivity
import com.example.instagalleria.R
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.OnBackPressed
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList

class CameraFragment : Fragment(), OnBackPressed {


    val REQUEST_IMAGE_CAPTURE: Int = 1
    val REQUEST_IMAGE_FROM_PHONE: Int = 2

    val PHOTO_UPLOAD_TAG = "photo_upload_tag"
    val CAMERA_FRAGMENT_BACKSTACK = "camer_fragment_backstack"

    //file paths
    private lateinit var currentPhotoPath: String
    lateinit var filePathUri: Uri
    private lateinit var photoUri: Uri
    private lateinit var photoIntent: Intent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.camera_layout, container, false)

        var take_photo = view.findViewById<ImageButton>(R.id.takePhoto)
        var upload_photo = view.findViewById<ImageButton>(R.id.uploadPhoto)

        //take photo button
        take_photo.setOnClickListener({ l -> openPhoneCamera() })

        //upload button
        upload_photo.setOnClickListener({ v -> openFile() })

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
        if (photoIntent.resolveActivity(this.context!!.packageManager) != null) {
            try {
                photoUri = getPhotoUri()

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

        var storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

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
            var photoURI = FileProvider.getUriForFile(
                this.context!!,
                "com.example.instagalleria.fileprovider",
                photoFile
            )
            this.photoUri = photoURI
        }
        return photoUri
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_IMAGE_CAPTURE) {                                 // on take photo button click
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                try {
                    val f = File(currentPhotoPath)
                    val contentUri = Uri.fromFile(f)
                    setImageView(contentUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == REQUEST_IMAGE_FROM_PHONE) {                       // on upload button click
            if (resultCode == RESULT_OK) {
                if (data != null && data.data != null) {
                    filePathUri = data.data                                         //on single image select
                    setImageView(filePathUri)

                } else if (data!!.getClipData() != null) {                            // on multiple image select
                    var imageUrisList = ArrayList<Uri>()

                    var count = data.getClipData().getItemCount();
                    for (i in 0 until count) {
                        var imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUrisList.add(imageUri)
                    }
                    setGridView(imageUrisList)
                }
            }
        }
    }

    //image view for the selected single photo
    fun setImageView(uri: Uri) {
        var bundle = Bundle()
        bundle.putString("uri_string", uri.toString())
        callPhotoLibrary(bundle)
    }

    // grid view for the multi-selected photos
    fun setGridView(imageUrisList: ArrayList<Uri>) {

        var bundle = Bundle()
        bundle.putSerializable("upload_image", imageUrisList)
        callPhotoLibrary(bundle)
    }

    //transfer to photo upload class
    fun callPhotoLibrary(bundle: Bundle) {

        var transaction = fragmentManager!!.beginTransaction()
        var photoUploadFragment = PhotoUploadFragment()
        photoUploadFragment.arguments = bundle
        transaction.add(R.id.fragment_container, photoUploadFragment, PHOTO_UPLOAD_TAG)
        transaction.addToBackStack(CAMERA_FRAGMENT_BACKSTACK)
        transaction.commit()
    }

    override fun OnBackPressed() {

        val activity = activity as MainActivity

        activity.fragment_toolbar_bottom.backFromcameraFragtoPhotoDetail()
        fragmentManager!!.popBackStack()
//
    }

}
