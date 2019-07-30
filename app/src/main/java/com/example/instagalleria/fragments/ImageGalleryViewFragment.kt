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
import android.view.*
import android.widget.*
import com.example.instagalleria.MainActivity
import com.example.instagalleria.R
import com.example.instagalleria.adapter.ImageViewAdapter
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.UploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ImageGalleryViewFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView

    lateinit var user_displayName: String

    var uploadList = ArrayList<UploadImage>()
    lateinit var adapter: ImageViewAdapter

    lateinit var fragment_login: LoginPageFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_gallery_layout, container, false)

        getAllDocumentsFromDB()    // fetching data(all images) from the firebase DB

        var bundle = arguments

        user_displayName = bundle!!.getString("profile_user")

        // recycler view functions
        recyclerView = view.findViewById(R.id.recyclerView)

        var gridLayoutManager = GridLayoutManager(this.context, 3)
        recyclerView.layoutManager = gridLayoutManager as RecyclerView.LayoutManager?

        adapter = ImageViewAdapter(context!!, uploadList)
        recyclerView.adapter = adapter


        fragment_login = fragmentManager!!.fragments.get(2) as LoginPageFragment



        if ((!(fragment_login.fragment_toolbar_top!!.isVisible)) && (!(fragment_login.fragment_toolbar_bottom!!.isVisible)) ){
            fragment_login.fragment_toolbar_top!!.view!!.visibility = View.VISIBLE
            fragment_login.fragment_toolbar_bottom!!.view!!.visibility = View.VISIBLE


        }
//
//        fragment_login.fragment_toolbar_top.toolbar_title.setText(getString(R.string.app_name))
//        fragment_login.fragment_toolbar_top.toolbar_back.visibility = View.GONE

//        fragment_login.fragment_toolbar_top.toolbar_title.setText("Insta Gallery")
//        fragment_login.fragment_toolbar_top.toolbar_back.visibility=View.GONE
//
//        fragment_login.fragment_toolbar_bottom.toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
//        fragment_login.fragment_toolbar_bottom.toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)
//

        return view
    }


    // retrieve from database collection cloud storage
    fun getAllDocumentsFromDB() {
        Log.d(TAG, "getAllDocumentsFromDB....Started");

        Constants.db_storageRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    Log.d(TAG, "document names" + "" + "" + document.toString())

                    var nameString = document.data

                    var img_name = nameString.get("NAME")
                    var img_uri = nameString.get("URI")
                    var profile_user = nameString.get("USER")

                    val uploadImage = UploadImage(img_name as String, img_uri as String, profile_user as String)
                    uploadList.add(uploadImage)

                    Log.d(TAG, "The whole set of docs=   ${document.data}")
                }
                adapter.notifyDataSetChanged()   // this function call runs on its own thread

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        Log.d(TAG, "getAllDocumentsFromDB....Ended");

    }

    fun refresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged()
        }
    }
}

// issues on back from photo detail the frag to image gallery the toolbar top remains the same shows photo detail and back button
// list view not scrolling do i need to change