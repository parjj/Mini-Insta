package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.instagalleria.R
import com.example.instagalleria.model.Constants.Companion.TAG
import java.lang.Exception

class ToolbarBottomFragment() :Fragment(){

    var CAMERA_FRAGMENT ="camera_fragment"
    var TOOLBAR_BOTTOM_FRAGMENT ="toolbar_bottom_fragment"


    public lateinit var toolbar_bottom: Toolbar
    public lateinit var toolbar_left: ImageButton
    public lateinit var toolbar_right: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var view = inflater.inflate(R.layout.toolbar_layout, container, false)

        //toolbar activity
        toolbar_bottom = view.findViewById(R.id.toolbar_layout)
        toolbar_left = view.findViewById(R.id.leftSide)
        toolbar_right = view.findViewById(R.id.rightSide)

        toolbar_left.setBackgroundResource(R.drawable.icons8_home_26)
        toolbar_left.scaleX = 0.1f
        toolbar_left.scaleX = 0.1f
        toolbar_right.setBackgroundResource(R.drawable.icons8_camera_26)
        toolbar_right.scaleX = 0.1f
        toolbar_right.scaleX = 0.1f


        // to camera fragment
        toolbar_right.setOnClickListener(View.OnClickListener { l ->

        try {
            var fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            var cameraFragment = CameraFragment()

            fragmentTransaction.add(R.id.fragment_container, cameraFragment,CAMERA_FRAGMENT )
            fragmentTransaction.addToBackStack(TOOLBAR_BOTTOM_FRAGMENT)

            fragmentTransaction.commit()
        }catch(exception:Exception){
            exception.printStackTrace();
            Log.e("exception", exception.message);
        }

        })

        return view;
    }
}