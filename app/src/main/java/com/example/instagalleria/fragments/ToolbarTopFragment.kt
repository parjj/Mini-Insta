package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.instagalleria.R
import com.google.firebase.auth.FirebaseAuth

class ToolbarTopFragment() :Fragment(){


     lateinit var toolbar_top: Toolbar
     lateinit var toolbar_back: TextView
     lateinit var toolbar_title: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var view = inflater.inflate(R.layout.toolbar_top, container, false)


        //toolbar top
        toolbar_top = view.findViewById(R.id.toolbar_top)
        toolbar_back = view.findViewById(R.id.backtext)
        toolbar_title = view.findViewById(R.id.title)
        toolbar_back.visibility = View.GONE
      //  toolbar_back.text="BACK"
        toolbar_title.setText("Insta Gallery")
        toolbar_title.gravity = Gravity.CENTER_VERTICAL
        toolbar_top.inflateMenu(R.menu.settings)


        toolbar_back.setOnClickListener(View.OnClickListener { l->

            var fm=fragmentManager!!.fragments.get(1) as ToolbarBottomFragment
            fm.toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
            fm.toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)

            fragmentManager!!.popBackStack("imageViewAdapter_backStack",1)


            toolbar_title.setText("Insta Gallery")
            toolbar_back.visibility = View.GONE

        })


        //sign out menu
        toolbar_top.setOnMenuItemClickListener {

            FirebaseAuth.getInstance().signOut()
           // this.activity!!.finish()
           // login_tag

            var fr= activity!!.supportFragmentManager.fragments.get(2)

               fragmentManager!!.beginTransaction().add(R.id.fragment_container
               ,fr).commit()

            true
        }

        return view
    }
}