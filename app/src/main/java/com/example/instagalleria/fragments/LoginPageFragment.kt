package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.instagalleria.R

class LoginPageFragment :Fragment(){


    private lateinit var username:EditText
    private lateinit var password:EditText
    private lateinit var login_button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var view:View = inflater.inflate(R.layout.user_login,container,false)

        username=view.findViewById(R.id.username)
        password=view.findViewById(R.id.password)
        login_button=view.findViewById(R.id.loginB)

        login_button.setOnClickListener(View.OnClickListener { v ->

            var fragmentTransaction = fragmentManager!!.beginTransaction()   // why do we add this
            var imageGalleryViewFragment = ImageGalleryViewFragment()

            fragmentTransaction.add(R.id.mainFrgament,imageGalleryViewFragment,"image_gallery")
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()

        })


        return view
    }
}