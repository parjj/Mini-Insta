package com.example.instagalleria.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.instagalleria.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import android.app.AlertDialog
import android.util.Log
import android.widget.FrameLayout
import com.example.instagalleria.model.Constants.Companion.TAG




class LoginPageFragment : Fragment() {


    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login_button: Button
    private lateinit var new_user: Button
    private lateinit var  fl: FrameLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        auth = FirebaseAuth.getInstance()

        var view: View = inflater.inflate(R.layout.user_login, container, false)

        email = view.findViewById(R.id.username)
        password = view.findViewById(R.id.password)
        login_button = view.findViewById(R.id.loginB)
        new_user = view.findViewById(R.id.newUser)

        // login button click
        login_button.setOnClickListener(View.OnClickListener { v ->


            val email_val: String
            val password_val: String
            email_val = email.getText().toString()
            password_val = password.getText().toString()

            if (TextUtils.isEmpty(email_val)) {
                Toast.makeText(context, "Please enter email...", Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(password_val)) {
                Toast.makeText(context, "Please enter password!", Toast.LENGTH_LONG).show();
            }
            transactionCall()
        })


        // new user registeration
        new_user.setOnClickListener(View.OnClickListener { l ->

            val inflater = this.layoutInflater
            var alertView = inflater.inflate(R.layout.register_layout, null)

            var builder = AlertDialog.Builder(this.context)

            builder.setView(alertView)

            var alertDialog = builder.create();

            var txt_email = alertView.findViewById<EditText>(R.id.txt_username)
            var txt_password = alertView.findViewById<EditText>(R.id.txt_password);
            var register_button = alertView.findViewById<Button>(R.id.btn_register);



            register_button.setOnClickListener(View.OnClickListener { v ->

                var email_val: String
                var password_val: String
                email_val = txt_email.getText().toString()
                password_val = txt_password.getText().toString()

                Log.d(TAG, "username" + email_val)
                Log.d(TAG, "password" + password_val)

                if (TextUtils.isEmpty(email_val)) {  // this is a good usage
                    Toast.makeText(context, "Please enter email...", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(password_val)) {
                    Toast.makeText(context, "Please enter password!", Toast.LENGTH_LONG).show();
                }

                registerationCall(email_val, password_val)
                alertDialog.dismiss()

            })
            alertDialog.show()
        })

        return view
    }

    //registeration call creating the user and pwd
    fun registerationCall(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registration successful!")

                } else {
                    Log.d(TAG, "Registration failed! Please try again later" + task.exception)
                }
            })
    }

    // transaction to imagegallery view
    fun transactionCall(){

        var fragmentTransaction = fragmentManager!!.beginTransaction()   // why do we add this
        var imageGalleryViewFragment = ImageGalleryViewFragment()

        // var bundle = Bundle()
        //  bundle.putString("username_email", email_val)

        fragmentTransaction.add(R.id.fragment_container, imageGalleryViewFragment, "image_gallery")
        fragmentTransaction.addToBackStack("IG")

        fragmentTransaction.commit()
    }

    // check to see if the user is already signed in
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    fun loginUserCall(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
            }else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
            }

        })


    }

}