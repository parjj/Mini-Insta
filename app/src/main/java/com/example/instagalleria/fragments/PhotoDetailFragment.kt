package com.example.instagalleria.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.example.instagalleria.R
import com.example.instagalleria.adapter.CommentsSectionAdapter
import com.example.instagalleria.adapter.PhotoUploadAdapter
import com.example.instagalleria.model.CommentsData
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.post_comments.*
import org.w3c.dom.Comment
import java.lang.Exception
import java.util.HashMap

class PhotoDetailFragment : Fragment(), View.OnClickListener {

    lateinit var imageView: ImageView
    lateinit var hearts_button: ImageButton
    lateinit var settings_button: ImageButton
    lateinit var comments_button: ImageButton
    lateinit var likes: TextView

    private lateinit var comments_data: CommentsData

    lateinit var listView: ListView
    private var commentsDataList = arrayListOf<CommentsData>()
    lateinit var adapter: CommentsSectionAdapter

    var imageGallery = ImageGalleryViewFragment()

    var likes_count: Long = 0

    private lateinit var uriString: String
    private lateinit var imageFileName: String
    private lateinit var comments_str: String
    var hashMap: HashMap<String, String> = HashMap<String, String>()
    var hashMap_forHearts: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_detail, container, false)

        var bundle = arguments
        uriString = bundle!!.getString("uri_image_value")  // comming from image view adapter
        imageFileName = bundle!!.getString("uri_image_fileName")  // comming from image view adapter

        imageGallery = fragmentManager!!.fragments.get(3) as ImageGalleryViewFragment

        //fetchCommentsFromDB()

        imageView = view.findViewById(R.id.image_detail)
        hearts_button = view.findViewById(R.id.hearts)
        settings_button = view.findViewById(R.id.settings)
        comments_button = view.findViewById(R.id.comments)
        likes = view.findViewById(R.id.likes)
        listView = view.findViewById(R.id.commets_list)

        Glide.with(context as Context).load(uriString).into(imageView)

        hearts_button.setOnClickListener(this) // hearts button click
        settings_button.setOnClickListener(this)// settings button click
        comments_button.setOnClickListener(this)// comments button click

        adapter = CommentsSectionAdapter(context as Context, imageGallery.userName, commentsDataList)
        listView.adapter = adapter


        // load all the comments for the image from DB
      likes_count=  fetchCommentsFromDB()

        return view

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.hearts -> heartsFunctionCall()
            R.id.settings -> settingsFunctionCall()
            R.id.comments -> postComments()             // call for commenting
            R.id.backtext -> fragmentManager!!.popBackStack()

        }

    }

    // heart likes capture
    fun heartsFunctionCall() {

        hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)

        var new_count = likes_count!!.plus(1)
        likes.text = "$new_count Likes"
        hashMap_forHearts.put("LIKES", new_count.toString())

        upDateLikes(hashMap_forHearts)
    }


//    fun fetchLikesFromDB(): Long {
//
//        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//
//                    var checkLikes = document.data
//
//                    if (document.data.containsKey("LIKES")) {
//                        var likes_no :Long  = checkLikes.get("LIKES") as Long
//
//                        if(likes_no >=0)
//                        {
//                            likes_count= likes_no+1
//                            hashMap_forHearts.put("LIKES", likes_count)
//
//
//                        }
//
//                    }else {
//
//                        hashMap_forHearts.put("LIKES", likes_count)
//                    }
//
//                   documentReferenceCall(hashMap_forHearts)
//                }
//            }
//            .addOnFailureListener(OnFailureListener { exception: Exception ->
//                Log.d(
//                    Constants.TAG,
//                    "failure to upload likes to cloud db"
//                )
//            })
//
//        return likes_count
//    }

    //creating likes field
    fun upDateLikes(map: HashMap<String, String>) {
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.update(hashMap_forHearts as Map<String, Int>)
                        .addOnSuccessListener {
                            OnSuccessListener<Void> {
                                Log.d(
                                    Constants.TAG,
                                    "Uploaded  field likes to cloud db successfully"
                                )


                            }
                        }
                        .addOnFailureListener(OnFailureListener { exception: Exception ->
                            Log.d(
                                Constants.TAG,
                                "failure to upload field likes to cloud db"
                            )
                        })
                }


            }
    }

    //post comments
    fun postComments() {

        val inflater = this.layoutInflater
        var alertView = inflater.inflate(R.layout.post_comments, null)  // why am i passing null
        var builder = AlertDialog.Builder(this.context)
        builder.setView(alertView)

        var alertDialog = builder.create()

        var editText = alertView.findViewById<EditText>(R.id.comments_AD)
        var post_button = alertView.findViewById<Button>(R.id.post_btn)

        //post button click
        post_button.setOnClickListener(View.OnClickListener { v ->

            comments_str = editText.text.toString()

            hashMap.put("COMMENTS", comments_str)
            hashMap.put("USERNAME", imageGallery.userName)

            //upload comments to cloud FB
            uploadCommentsToStorage(comments_str)

            // commentsList.add(comments_str)
            comments_data = CommentsData(imageGallery.userName, comments_str)
            commentsDataList.add(comments_data)
            alertDialog.dismiss()

        })



        alertDialog.show()

    }


    fun settingsFunctionCall() {


    }

    // upload the comments to cloud DB
    fun uploadCommentsToStorage(comments_value: String) {

        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var sub_coll: DocumentReference
                    sub_coll =
                        document.reference.collection("comments").document("CMNT_FOR_" + imageFileName)

                    sub_coll.set(hashMap, SetOptions.merge())
                        .addOnSuccessListener {
                            OnSuccessListener<Void> {
                                Log.d(
                                    Constants.TAG,
                                    "Uploaded comments to cloud db successfully"
                                )


                            }
                        }
                        .addOnFailureListener(OnFailureListener { exception: Exception ->
                            Log.d(
                                Constants.TAG,
                                "failure to upload comments to cloud db"
                            )
                        })
                }
            }
    }

    // fetch comments from coud DB
    fun fetchCommentsFromDB(): Long {
        var i :Long =0
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener(OnSuccessListener { result ->
                Log.d(TAG, "success getting documents : images")
                for (document in result) {

                    var map = document.data
                    var s=  map.get("LIKES")
                    if(s is String){
                        i= s.toLong()

                    }

                    likes.text = "$i Likes"
                    document.reference.collection("comments").get()
                        .addOnSuccessListener(OnSuccessListener { tResult ->
                            Log.d(TAG, "success getting documents:comments ")
                            for (comment_data in tResult) {

                                var data = comment_data.data

                                var comm = data.get("COMMENTS")
                                var comm_user = data.get("USERNAME")

                                comments_data = CommentsData(comm_user as String, comm as String)
                                commentsDataList.add(comments_data)
                                adapter.notifyDataSetChanged()

                            }

                        })
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents : comments")
                        }

                }

            })
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents : images")
            }

        return i
    }
}




