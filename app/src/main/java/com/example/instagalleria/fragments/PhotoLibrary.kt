package com.example.instagalleria.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.instagalleria.R
import com.example.instagalleria.adapter.PhotoLibraryAdapter
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.UploadImage
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.progress_bar.*
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class PhotoLibrary : Fragment() {


    lateinit var adapter: PhotoLibraryAdapter

    lateinit var gridView: GridView
    lateinit var upload_button: Button

    var photo_list = ArrayList<Uri>()
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_left: ImageButton
    private lateinit var toolbar_right: ImageButton
    private lateinit var camera_photo: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var uri_value: Uri

    var camera = CameraFragment()
    var hashMap: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.photo_library, container, false)

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

        var bundle = Bundle()
        bundle = arguments!!

        var photos_list = bundle.get("upload_image")

        var str= bundle.getString("uri_string")

        gridView = view.findViewById(R.id.gridView)
        upload_button = view.findViewById(R.id.uploadMultiple)
        camera_photo = view.findViewById(R.id.cameraImg)

        if(str !=null) {
            uri_value = Uri.parse(str)

            // var currentPhotoPath= bundle.getString("photo_path")

            if (uri_value != null) {

                var cr = context!!.contentResolver
                val imgBitmap = MediaStore.Images.Media.getBitmap(cr, uri_value)
                camera_photo.visibility = View.VISIBLE
                camera_photo.setImageBitmap(imgBitmap)

            }

        }

        toolbar_left.setOnClickListener(View.OnClickListener { v ->

            var fragmentTransaction = fragmentManager!!.beginTransaction()
            var imageGalleryView = ImageGalleryViewFragment()

            fragmentTransaction.add(R.id.fragment_container, imageGalleryView)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()

            fragmentManager!!.popBackStack("image_gallery", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        })

        upload_button.setOnClickListener(View.OnClickListener { l ->


            if (photos_list != null) {

                adapter = PhotoLibraryAdapter(context as Context, photos_list as ArrayList<Uri>)

                gridView.adapter = adapter

                for (photoUri in photos_list) {

                    var cr = context!!.contentResolver

                    upload(photoUri)
                }
            } else {
                upload(uri_value)
            }

        })

//        val bmOptions = BitmapFactory.Options()
//        val photo_bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        return view;

    }


    //upload to firebase function
    public fun upload(filePathUri: Uri) {
        var uploadToDB: UploadImage
        progressBar.visibility = View.VISIBLE

        if (filePathUri != null) {

            // Create file metadata including the content type
            var metadata = StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build()

            val ref =
                Constants.storageRef.child(UUID.randomUUID().toString())  // try to use just the storagRef and see - when you use storageRef.putFile the images
            //directly gets stored in the storage firebase with the name uploads.  so have the storageRef.child , so that the images get created inside the folder uploads
            // with the names that get assigned using UUID   A class that represents an immutable universally unique identifier (UUID). A UUID represents a 128-bit value.


            // firebase storage
            ref.putFile(filePathUri, metadata)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    // Toast.makeText(context, "Uploaded to firebase storage ", Toast.LENGTH_SHORT).show()

//                    var handler = Handler()
//                    handler.postDelayed(Runnable {
//                        progressBar.setProgress(0)
//
//                    }, 500)


                    var name = taskSnapshot.storage.name
                    var urlTask: Task<Uri> = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    var downloadUrl = urlTask.getResult();

                    //  var db_id: String = db_storageRef.

                    hashMap.put("NAME", name)
                    hashMap.put("URI", downloadUrl.toString())

                    dbStorage(name)

                    fragmentManager!!.popBackStack()

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
                    Log.d(
                        Constants.TAG,
                        "Uploaded to db successfully"
                    )
                }
            }
            .addOnFailureListener(OnFailureListener { exception: Exception ->
                Log.d(
                    Constants.TAG,
                    "failure to upload to db"
                )
            })


    }


}