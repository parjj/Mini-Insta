package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.instagalleria.R
import java.lang.Exception

class ToolbarBottomFragment() : Fragment() {

    var TOOLBAR_BOTTOM_BACKSTACK = "toolbar_bottom_backStack"


    lateinit var toolbar_bottom: Toolbar
    lateinit var toolbar_left: ImageView
    lateinit var toolbar_right: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var view = inflater.inflate(R.layout.toolbar_bottom, container, false)

        //toolbar activity
        toolbar_bottom = view.findViewById(R.id.toolbar_layout)
        toolbar_left = view.findViewById(R.id.leftSide)
        toolbar_right = view.findViewById(R.id.rightSide)

        //home icon
        toolbar_left.setOnClickListener(View.OnClickListener { v ->


            var frag= fragmentManager!!.fragments.get(0) as ToolbarTopFragment
            frag.toolbar_title.setText(getString(R.string.app_name))



            toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
            toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)

            fragmentManager!!.popBackStack(TOOLBAR_BOTTOM_BACKSTACK, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            // the backstack is the key

        })


        //camera icon
        toolbar_right.setOnClickListener(View.OnClickListener { l ->

                 try {
                var fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                var cameraFragment = CameraFragment()

                fragmentTransaction.add(R.id.fragment_container, cameraFragment)
                fragmentTransaction.addToBackStack(TOOLBAR_BOTTOM_BACKSTACK)

                fragmentTransaction.commit()
            } catch (exception: Exception) {
                exception.printStackTrace();
                Log.e("exception", exception.message);
            }

        })

        return view;
    }
}