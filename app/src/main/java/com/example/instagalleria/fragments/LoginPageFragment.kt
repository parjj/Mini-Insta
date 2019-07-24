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
import com.example.instagalleria.model.Constants.Companion.TAG
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.R.attr.password


class LoginPageFragment : Fragment() {

    private val LOGIN_PAGE = "login_page"
    val IMAGE_GALLERY_FRAGMENT = "image_gallery"
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login_button: Button
    private lateinit var new_user: Button

    lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        auth = FirebaseAuth.getInstance()

        var view: View = inflater.inflate(R.layout.user_login, container, false)

        email = view.findViewById(R.id.username)
        password = view.findViewById(R.id.password)
        login_button = view.findViewById(R.id.loginB)
        new_user = view.findViewById(R.id.newUser)


        getUserProfile()

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
            val user = auth.getCurrentUser()

            loginCall(email_val, password_val)
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
            var profile_name = alertView.findViewById<EditText>(R.id.profileName);
            var register_button = alertView.findViewById<Button>(R.id.btn_register);

            register_button.setOnClickListener(View.OnClickListener { v ->

                var email_val: String
                var password_val: String
                var user_name: String
                email_val = txt_email.getText().toString()
                password_val = txt_password.getText().toString()
                user_name = profile_name.getText().toString()

                Log.d(TAG, "username" + email_val)
                Log.d(TAG, "password" + password_val)

                if (TextUtils.isEmpty(email_val)) {  // this is a good usage
                    Toast.makeText(context, "Please enter email...", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(password_val)) {
                    Toast.makeText(context, "Please enter password!", Toast.LENGTH_LONG).show();
                }

                registerationCall(email_val, password_val, user_name)
                alertDialog.dismiss()

            })
            alertDialog.show()
        })

        return view
    }

    //registeration call creating the user and pwd
    fun registerationCall(email: String, password: String, profileName: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registration successful!")

                    // call for setting the desired display name
                    authenticate(profileName)
                    transactionCall(profileName)
                } else {
                    Log.d(TAG, "Registration failed! Please try again later" + task.exception)
                }
            })
    }

    // set the diaply name
    fun authenticate(desiredName: String) {
        Log.d(TAG, "setting the  displayname" + desiredName)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(desiredName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User display name updated.")
                    }
                }
        }
    }


    // login call to image gallery view
    fun loginCall(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.getCurrentUser()
                    Log.d(TAG, "inside login method call check for displayname" + user)
                    transactionCall(user.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }

                // ...
            })

    }

    //to image gallery view
    fun transactionCall(username: String) {
        var bundle = Bundle()
        bundle.putString("profile_user", username)
        var fragmentTransaction = fragmentManager!!.beginTransaction()   // why do we add this
        var imageGalleryViewFragment = ImageGalleryViewFragment()

        imageGalleryViewFragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container, imageGalleryViewFragment,IMAGE_GALLERY_FRAGMENT )
        fragmentTransaction.addToBackStack(LOGIN_PAGE)
        fragmentTransaction.commit()

    }

    // check if user is already logged in or not
    fun getUserProfile() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            var name = currentUser.getDisplayName();
            Log.d(TAG, "on start method check for displayname" + name)
            if (name != null) {
                transactionCall(name)
            }

        }

    }
}


//---------------------------------------------------------------------------------------------------------

