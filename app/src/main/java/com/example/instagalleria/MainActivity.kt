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

    lateinit var fragment_toolbar_top: ToolbarTopFragment
    lateinit var fragment_toolbar_bottom: ToolbarBottomFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment_toolbar_top = supportFragmentManager!!.findFragmentByTag("toolbar_top_tag") as ToolbarTopFragment
        fragment_toolbar_bottom =
            supportFragmentManager!!.findFragmentByTag("toolbar_bottom_tag") as ToolbarBottomFragment

        var fragmentTransaction = supportFragmentManager!!.beginTransaction()   // why do we add this
        var loginPageFragment = LoginPageFragment()
        fragmentTransaction.add(R.id.fragment_container, loginPageFragment, null)
        fragmentTransaction.commit()

    }

    override fun onBackPressed() {

        for(fragment in supportFragmentManager.fragments){
            if(fragment is CameraFragment){
                var cameraFragment = fragment as CameraFragment
                cameraFragment.OnBackPressed()
            }else if (fragment is PhotoDetailFragment){
                var photoDetailFragment = fragment as PhotoDetailFragment
                photoDetailFragment.OnBackPressed()
            }
        }
    }

}
