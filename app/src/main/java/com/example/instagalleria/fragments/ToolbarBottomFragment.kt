package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.instagalleria.R
import java.lang.Exception



class ToolbarBottomFragment() : Fragment(), View.OnClickListener {

    var TOOLBAR_BOTTOM_BACKSTACK = "toolbar_bottom_backStack"
    var CAMERA_FRAG_TAG = "camera_fragment_tag"

    lateinit var toolbar_bottom: Toolbar
    lateinit var toolbar_left: ImageView
    lateinit var toolbar_right: ImageView
    lateinit var fragment: ToolbarTopFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.toolbar_bottom, container, false)

        //toolbar activity
        toolbar_bottom = view.findViewById(R.id.toolbar_layout)
        toolbar_left = view.findViewById(R.id.leftSide)
        toolbar_right = view.findViewById(R.id.rightSide)
        fragment = fragmentManager!!.fragments.get(0) as ToolbarTopFragment

        toolbar_left.setOnClickListener(this)
        toolbar_right.setOnClickListener(this)

        return view;
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.leftSide -> toolbarLeftHomeCall()
            R.id.rightSide -> toolbarRightCameraCall()
        }
    }


    fun toolbarLeftHomeCall() {
        //home icon
        toolbar_left.setOnClickListener(View.OnClickListener { v ->
            home()
        })
    }


    fun toolbarRightCameraCall() {
        //camera icon  on clicking the camera it should jump to camera fragment
        toolbar_right.setOnClickListener(View.OnClickListener { l ->
            camera()
        })
    }

    fun camera() {

        for (fragment in activity!!.supportFragmentManager.getFragments()) {
            if( fragment !is ToolbarTopFragment && fragment !is ToolbarBottomFragment && fragment !is LoginPageFragment ) {
                activity!!.supportFragmentManager.beginTransaction().remove(fragment).commit()
            }

        }


        fragment.toolbar_title.setText("Photo Library")
        fragment.toolbar_back.visibility = View.GONE

        toolbar_left.setImageResource(R.drawable.home_active_optimized)
        toolbar_right.setImageResource(R.drawable.icn_photo_inactive_optimized)

        try {
            var fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            var cameraFragment = CameraFragment()

            fragmentTransaction.add(R.id.fragment_container, cameraFragment, CAMERA_FRAG_TAG)
            fragmentTransaction.addToBackStack(TOOLBAR_BOTTOM_BACKSTACK)

            fragmentTransaction.commit()
        } catch (exception: Exception) {
            exception.printStackTrace();
            Log.e("exception", exception.message);
        }
    }

    fun home() {

        for (fragment in activity!!.supportFragmentManager.getFragments()) {
            if( fragment !is ToolbarTopFragment && fragment !is ToolbarBottomFragment && fragment !is LoginPageFragment ) {
                activity!!.supportFragmentManager.beginTransaction().remove(fragment).commit()
            }

        }

        fragment.toolbar_title.setText(getString(R.string.app_name))
        toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
        toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)


        try {
            var fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            var ImageGalleryViewFragment = ImageGalleryViewFragment()
            var fragment_login = fragmentManager!!.fragments.get(2) as LoginPageFragment
            ImageGalleryViewFragment.arguments = fragment_login.bundle
            fragmentTransaction.add(R.id.fragment_container, ImageGalleryViewFragment, null)
            fragmentTransaction.addToBackStack(TOOLBAR_BOTTOM_BACKSTACK)

            fragmentTransaction.commit()
        } catch (exception: Exception) {
            exception.printStackTrace();
            Log.e("exception", exception.message);
        }

    }

    fun backandOnFromUpload() {

        toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)
        toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
        fragment.toolbar_title.setText(R.string.app_name)
        fragment.toolbar_back.visibility = View.GONE

    }

    fun photoDeatilSetting() {

        fragment.toolbar_title.setText("Photo Detail")
        fragment.toolbar_back.visibility = View.VISIBLE

        toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)
        toolbar_left.setImageResource(R.drawable.home_active_optimized)
    }

    fun backFromcameraFragtoPhotoDetail(){
        toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)
        toolbar_left.setImageResource(R.drawable.home_active_optimized)
        fragment.toolbar_title.setText(R.string.app_name)
        fragment.toolbar_back.visibility = View.VISIBLE

    }

}