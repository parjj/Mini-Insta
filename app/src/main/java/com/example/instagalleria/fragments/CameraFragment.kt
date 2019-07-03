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

    companion object {
        public val TAG: String = "Photo Application"
    }
    val REQUEST_IMAGE_CAPTURE: Int = 1
    val REQUEST_IMAGE_FROM_PHONE: Int = 2

    private lateinit var take_photo: ImageButton
    private lateinit var upload_photo: ImageButton
    private lateinit var upload_button: Button
    private lateinit var camera_photo: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_left: ImageButton
    private lateinit var toolbar_right: ImageButton
    private lateinit var frameL1: FrameLayout
    private lateinit var frameL2: FrameLayout
    private lateinit var linearL: LinearLayout
    private lateinit var progressBar: ProgressBar



    //file paths
    private lateinit var currentPhotoPath: String
    private lateinit var filePathUri: Uri
    private lateinit var photoUri: Uri
    private lateinit var photoIntent: Intent

    var hashMap: HashMap<String, String> = HashMap<String, String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.camera_layout, container, false)

        take_photo = view.findViewById(R.id.takePhoto)
        upload_photo = view.findViewById(R.id.uploadPhoto)
        camera_photo = view.findViewById(R.id.cameraImg)
        upload_button = view.findViewById(R.id.uploadButton)
        frameL1 = view.findViewById(R.id.frame1)
        frameL2 = view.findViewById(R.id.frame2)
        linearL = view.findViewById(R.id.linear2)
        progressBar = view.findViewById(R.id.pbLoading)

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
                    visibilitycheck()
                    val bmOptions = BitmapFactory.Options()
                    val photo_bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
                    filePathUri = photoUri

                    setImageView(photo_bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == REQUEST_IMAGE_FROM_PHONE) {                   // on upload button click
            if (resultCode == RESULT_OK) {
                if (data != null && data.data != null) {
                    visibilitycheck()
                    filePathUri = data.data                                        //on single image select
                    var cr = context!!.contentResolver
                    val imgBitmap = MediaStore.Images.Media.getBitmap(cr, filePathUri)
                    setImageView(imgBitmap)

                } else if (data.getClipData() != null) {                            // on multiple image select
                    var imageUrisList = ArrayList<Uri>()

                    var count = data.getClipData().getItemCount();
                    for (i in 0 until count) {
                        var imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUrisList.add(imageUri)
                        //val imgBitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, imageUri)
                    }
                    setGridView(imageUrisList)
                }
            }
        }
    }

    fun setImageView(bitmap: Bitmap) {

        camera_photo.setImageBitmap(bitmap)
        upload_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                upload()
                // uploadToPhotoLibrary()
            }
        })
    }

    fun visibilitycheck() {
        linearL.visibility = View.VISIBLE
        frameL1.visibility = View.GONE
        frameL2.visibility = View.GONE
    }

    fun setGridView(imageUrisList: ArrayList<Uri>) {

        // var images_from_db = getAllDocumentsFromDB()

        var transaction = fragmentManager!!.beginTransaction()
        var photoLibrary = PhotoLibrary()
        var bundle = Bundle()
        bundle.putSerializable("upload_image", imageUrisList)
        transaction.add(R.id.mainFrgament, photoLibrary, "photo_library")
        photoLibrary.arguments = bundle
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()

    }

    //upload to firebase function
    fun upload() {
        var uploadToDB: UploadImage

        if (filePathUri != null) {

            // Create file metadata including the content type
            var metadata = StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build()

            progressBar.visibility = View.VISIBLE
            val ref =
                Constants.storageRef.child(UUID.randomUUID().toString())  // try to use just the storagRef and see - when you use storageRef.putFile the images
            //directly gets stored in the storage firebase with the name uploads.  so have the storageRef.child , so that the images get created inside the folder uploads
            // with the names that get assigned using UUID   A class that represents an immutable universally unique identifier (UUID). A UUID represents a 128-bit value.


            // firebase storage
            ref.putFile(filePathUri, metadata)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(context, "Uploaded to firebase storage ", Toast.LENGTH_SHORT).show()

                    var handler = Handler()
                    handler.postDelayed(Runnable {
                        progressBar.setProgress(0)

                    }, 500)

                    var name = taskSnapshot.storage.name
                    var urlTask: Task<Uri> = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    var downloadUrl = urlTask.getResult();

                    //  var db_id: String = db_storageRef.

                    hashMap.put("NAME", name)
                    hashMap.put("URI", downloadUrl.toString())

//                    uploadToDB = UploadImage(name, uri_str.toString())
//                    uploadList.add(uploadToDB)

                    //  hashMap.put("ID", db_id)
                    //    hashMap.put(db_id, uploadToDB)
                    dbStorage(name)

                })
                .addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                })
                .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { task ->

                    var progress = (100.0 * task.getBytesTransferred()) / task.getTotalByteCount();
                    progressBar.progress = progress.toInt()
                    //Toast.makeText(context, "Upload is $progress% done",Toast.LENGTH_SHORT).show()
                })

        }

    }

    //cloud db storage
    fun dbStorage(string: String) {

        //If the document does not exist, it will be created. If the document does exist,
        // its contents will be overwritten with the newly provided data,
        // unless you specify that the data should be merged into the existing document,

        Constants.db_storageRef.document(string.substring(0, 6))
            .set(hashMap, SetOptions.merge())


            .addOnSuccessListener {
                OnSuccessListener<Void> {
                    Toast.makeText(
                        context,
                        "Uploaded to db successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener(OnFailureListener { exception: Exception ->
                Toast.makeText(
                    context,
                    "failure to upload to db",
                    Toast.LENGTH_SHORT
                ).show()
            })


    }

}

//------------------------------------------------------------------------------------------------------------------------------

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
