package com.example.instagalleria.fragments

import android.content.Context
import android.graphics.Camera
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.instagalleria.R
import com.example.instagalleria.adapter.ImageViewAdapter
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.UploadImage

class ImageGalleryViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_left: ImageButton
    private lateinit var toolbar_right: ImageButton

    var uploadList = ArrayList<UploadImage>()
    lateinit var adapter :ImageViewAdapter


    private lateinit var toolbar_top: Toolbar
    private lateinit var toolbar_back: TextView
    private lateinit var toolbar_title: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_gallery_layout, container, false)

        // recycler view functions
        recyclerView = view.findViewById(R.id.recyclerView)

        var gridLayoutManager = GridLayoutManager(this.context, 5)
        recyclerView.layoutManager = gridLayoutManager

        getAllDocumentsFromDB()    // fetching data(all images) from the firebase DB

        adapter = ImageViewAdapter(context!!,uploadList)
        recyclerView.adapter= adapter


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


        //toolbar top
        toolbar_top = view.findViewById(R.id.toolbar_top)
        toolbar_back = view.findViewById(R.id.backtext)
        toolbar_title = view.findViewById(R.id.title)
        toolbar_back.visibility=View.GONE
        toolbar_title.setText("Insta Gallery")
        toolbar_title.gravity= Gravity.CENTER_VERTICAL


        toolbarListeners()

        return view
    }

    fun toolbarListeners(){

        toolbar_right.setOnClickListener(View.OnClickListener { l->

            var fragmentTransaction = fragmentManager!!.beginTransaction()   // to camera fragment
            var cameraFragment = CameraFragment()

            fragmentTransaction.add(R.id.fragment_container,cameraFragment,"camera_fragment")
            fragmentTransaction.addToBackStack("CF")

            fragmentTransaction.commit()


        })
    }

    // retrieve from database collection cloud storage
    fun getAllDocumentsFromDB() {
        Log.d(TAG, "getAllDocumentsFromDB....Started");

        Constants.db_storageRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    var nameString = document.data

                    var img_name = nameString.get("NAME")
                    var img_uri = nameString.get("URI")


                    val uploadImage = UploadImage(img_name as String, img_uri as String)
                    uploadList.add(uploadImage)
                    Log.d(TAG, "getAllDocumentsFromDB....Added " + uploadList.size);

                    Log.d(TAG, "${document.id} => ${document.data}")
                }

                Log.d(TAG, "getAllDocumentsFromDB....Upload Count :" + uploadList.size);
                adapter.notifyDataSetChanged()   // this function call runs on its own thread

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        Log.d(TAG, "getAllDocumentsFromDB....Ended");

    }


    override fun onStart() {
        super.onStart()
        if(adapter !=null){
            adapter.notifyDataSetChanged()
        }
    }

}


//next to do is live listner for the db