package com.example.instagalleria.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.instagalleria.R
import com.example.instagalleria.model.Constants.Companion.TAG


class PhotoDetail : Fragment(), View.OnClickListener {

    lateinit var imageView: ImageView
    lateinit var hearts_button: ImageButton
    lateinit var settings_button: ImageButton
    lateinit var comments_button: ImageButton
    lateinit var username: TextView
    lateinit var editText: EditText

    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_back: Button
    private lateinit var toolbar_title: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_detail, container, false)

        var bundle = arguments

        var uriSrting = bundle!!.getString("uri_string")

        imageView = view.findViewById(R.id.image_detail)
        hearts_button = view.findViewById(R.id.hearts)
        settings_button = view.findViewById(R.id.settings)
        comments_button = view.findViewById(R.id.comments)
        username = view.findViewById(R.id.username_detail)
        editText = view.findViewById(R.id.caption)

        //toolbar
        toolbar = view.findViewById(R.id.toolbar_top)
        toolbar_back = view.findViewById(R.id.backtext)
        toolbar_title = view.findViewById(R.id.title)
        toolbar_back.setText("BACK")
        toolbar_title.setText("Photo Detail")

        Glide.with(context as Context).load(uriSrting).into(imageView)

        hearts_button.setOnClickListener(this)
        settings_button.setOnClickListener(this)
        comments_button.setOnClickListener(this)

        //username.setText(user)

        return view

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.hearts -> heartsFunction()
            R.id.settings -> Log.d(TAG, "Youclick the settings button")
            R.id.comments -> Log.d(TAG, "Youclick the comments button")
            R.id.backtext -> fragmentManager!!.popBackStack()

        }

    }

    fun heartsFunction() {

        hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)


        Log.d(TAG, "Youclick the hearts button")
    }


}