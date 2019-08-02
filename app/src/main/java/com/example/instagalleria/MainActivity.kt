package com.example.instagalleria

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.instagalleria.fragments.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var fragment_toolbar_top : ToolbarTopFragment
    lateinit var fragment_toolbar_bottom : ToolbarBottomFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment_toolbar_top = supportFragmentManager!!.findFragmentByTag("toolbar_top_tag") as ToolbarTopFragment
        fragment_toolbar_bottom = supportFragmentManager!!.findFragmentByTag("toolbar_bottom_tag") as ToolbarBottomFragment

        var fragmentTransaction = supportFragmentManager!!.beginTransaction()   // why do we add this
        var loginPageFragment = LoginPageFragment()
        fragmentTransaction.add(R.id.fragment_container, loginPageFragment, null)
        fragmentTransaction.commit()

    }


//    override fun onBackPressed() {
//
//        var list = supportFragmentManager.fragments
//        var i =supportFragmentManager.fragments.size
//        if(i <= list.size) {
//            if (supportFragmentManager.fragments.get(5) is CameraFragment) {
//                var cf = supportFragmentManager.fragments.get(5)
//                if (list.contains(cf as CameraFragment)) {
//
//                    cf.OnBackPressed()
//                }
//            } else if (supportFragmentManager.fragments.get(5) is PhotoDetailFragment) {
//                var cf = supportFragmentManager.fragments.get(5)
//                if (list.contains(cf as PhotoDetailFragment)) {
//                    cf.OnBackPressed()
//                }
//            }
//        }else{
//            this.finish()
//        }
//    }
override fun onBackPressed() {

}

}
