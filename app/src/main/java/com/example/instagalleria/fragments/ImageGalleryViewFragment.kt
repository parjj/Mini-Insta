package com.example.instagalleria.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import com.example.instagalleria.R
import com.example.instagalleria.adapter.ImageViewAdapter
import com.example.instagalleria.fragments.CameraFragment.Companion.TAG
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.UploadImage

class ImageGalleryViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    var uploadList = ArrayList<UploadImage>()
    lateinit var adapter :ImageViewAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_gallery_layout, container, false)

        // recycler view functions
        recyclerView = view.findViewById(R.id.recyclerView)

        var gridLayoutManager = GridLayoutManager(this.context, 5)
        recyclerView.layoutManager = gridLayoutManager


        getAllDocumentsFromDB()



        adapter = ImageViewAdapter(context!!,uploadList)
        recyclerView.adapter= adapter

        return view

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


                    val uploadImage = UploadImage(img_name as String, Uri.parse(img_uri.toString()))
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


}
