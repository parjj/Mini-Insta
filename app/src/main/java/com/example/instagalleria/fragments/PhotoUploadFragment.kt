package com.example.instagalleria.fragments

import android.content.Context
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
import com.example.instagalleria.adapter.PhotoUploadAdapter
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PhotoUploadFragment : Fragment() {

    lateinit var adapter: PhotoUploadAdapter

    lateinit var gridView: GridView
    lateinit var upload_button: Button

    private lateinit var camera_photo: ImageView
    // private lateinit var progressBar: ProgressBar
    private var photos_list: ArrayList<Uri>? = null

    private lateinit var uri_value: Uri

    var imageGallery = ImageGalleryViewFragment()
    var hashMap: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.photo_library, container, false)

        //  progressBar = view.findViewById(R.id.progress_bar)

        var bundle = arguments!!
        var str = bundle.getString("uri_string")

        imageGallery = fragmentManager!!.fragments.get(3) as ImageGalleryViewFragment

        gridView = view.findViewById(R.id.gridView_pl)
        upload_button = view.findViewById(R.id.uploadMultiple)
        camera_photo = view.findViewById(R.id.cameraImg)

        // getting the uri string from the camera fragment
        if (str != null) {
            uri_value = Uri.parse(str)
            if (uri_value != null) {

                var cr = this.context?.contentResolver
                val imgBitmap = MediaStore.Images.Media.getBitmap(cr, uri_value)
                camera_photo.visibility = View.VISIBLE
                camera_photo.setImageURI(uri_value)  // setting the image view
            }

        } else {
            photos_list = bundle.get("upload_image") as ArrayList<Uri>

            if (photos_list != null) {
                adapter = PhotoUploadAdapter(context as Context, photos_list as ArrayList<Uri>)
                gridView.adapter = adapter
            }
        }

        //upload button click
        upload_button.setOnClickListener(View.OnClickListener { l ->

            Toast.makeText(context,"Upload button clicked",Toast.LENGTH_SHORT).show()
            imageGallery.adapter.notifyDataSetChanged()

            if (photos_list != null) {
                for (photoUri in photos_list!!) {
                    upload(photoUri)
                }
            } else {
                upload(uri_value)
            }
            fragmentManager!!.popBackStack("toolbar_bottom_backStack", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        })

        return view;

    }


    //upload to firebase
     fun upload(filePathUri: Uri) {
        //  progressBar.visibility = View.VISIBLE

        if (filePathUri != null) {

            // Create file metadata including the content type
            var metadata = StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build()

            val ref = Constants.storageRef.child(UUID.randomUUID().toString())
            // try to use just the storagRef and see - when you use storageRef.putFile the images
            //directly gets stored in the storage firebase with the name uploads.  so have the storageRef.child , so that the images get created inside the folder uploads
            // with the names that get assigned using UUID   A class that represents an immutable universally unique identifier (UUID). A UUID represents a 128-bit value.


            // firebase storage
            ref.putFile(filePathUri, metadata)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                    Log.d(
                        Constants.TAG,
                        "successfull in uploading to FB storage "
                    )


                    var name = taskSnapshot.storage.name
                    var urlTask: Task<Uri> = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    var downloadUrl = urlTask.getResult();


                    var user = imageGallery.userName
                    hashMap.put("USER", user)
                    hashMap.put("NAME", name)
                    hashMap.put("URI", downloadUrl.toString())
                    //hashMap.put("LIKES","0")
                    dbStorage(name)

                })
                .addOnFailureListener(OnFailureListener { e ->
                    Log.d(
                        Constants.TAG,
                        "Failed upload to FB storage "
                    )
                })
//                .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { task ->
//
//                    var progress = (100.0 * task.getBytesTransferred()) / task.getTotalByteCount();
//                    progressBar.progress = progress.toInt()
//                    //Toast.makeText(context, "Upload is $progress% done",Toast.LENGTH_SHORT).show()
//                })

        }

    }

    //cloud db storage
    fun dbStorage(string: String) {

        //If the document does not exist, it will be created. If the document does exist,
        // its contents will be overwritten with the newly provided data,
        // unless you specify that the data should be merged into the existing document,

        var docRef = Constants.db_storageRef.document(string.substring(0, 6))

        docRef.set(hashMap, SetOptions.merge())
            .addOnSuccessListener {
                OnSuccessListener<Void> {
                    Log.d(
                        Constants.TAG,
                        "Uploaded to cloud db successfully"
                    )


                }
            }
            .addOnFailureListener(OnFailureListener { exception: Exception ->
                Log.d(
                    Constants.TAG,
                    "failure to upload to cloud db"
                )
            })


    }

}