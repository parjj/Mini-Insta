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
import com.example.instagalleria.model.OnBackPressed
import com.example.instagalleria.model.UploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ImageGalleryViewFragment : Fragment(), OnBackPressed {

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

        val activity = activity as MainActivity
        activity.fragment_toolbar_top.view?.visibility = View.VISIBLE
        activity.fragment_toolbar_bottom.view?.visibility = View.VISIBLE

        return view
    }

    override fun onResume() {
        super.onResume()
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


    override fun onStart() {
        super.onStart()
        refresh()
    }

    fun refresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged()
        }
    }

   override fun OnBackPressed() {
       this.activity!!.finish()
    }
}