package com.example.instagalleria.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import com.example.instagalleria.R
import com.example.instagalleria.adapter.PhotoLibraryAdapter

class PhotoLibrary : Fragment(){


   lateinit var adapter :PhotoLibraryAdapter

    lateinit var gridView:GridView
    lateinit var upload_button: Button

    var photo_list =ArrayList<Uri>()
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_left: ImageButton
    private lateinit var toolbar_right: ImageButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view= inflater.inflate(R.layout.photo_library,container,false)

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

        var bundle= Bundle()
        bundle= arguments!!

        var photos_list= bundle.get("upload_image")

        gridView= view.findViewById(R.id.gridView)
        upload_button= view.findViewById(R.id.uploadMultiple)

        adapter= PhotoLibraryAdapter(context as Context, photos_list as ArrayList<Uri>)

        gridView.adapter=adapter

        return view;
    }
}