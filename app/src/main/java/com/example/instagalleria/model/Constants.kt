package com.example.instagalleria.model

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.DocumentReference




class Constants {


    companion object {

        public val TAG: String = "Photo Application"

        val STORAGE_PATH_UPLOADS = "images/"
        val DATABASE_PATH_UPLOADS = "imagesDB"


        // Create a storage reference from our app
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference("images/")
        val db_storage = FirebaseFirestore.getInstance()
        val db_storageRef: CollectionReference = db_storage.collection("imagesDB")


//        val fragment_toolbar_top:Fragment=!!.findFragmentByTag("toolbar_top_tag")
//        val fragment_toolbar_bottom= fragmentManager!!.findFragmentByTag("toolbar_bottom_tag")

    }
}