package com.example.instagalleria.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
    var list_of_likes=ArrayList<Long>()
    lateinit var liked_user:String

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

        imageGallery.fragment_login.fragment_toolbar_top.toolbar_title.setText("Photo Detail")
        imageGallery.fragment_login.fragment_toolbar_top.toolbar_back.visibility=View.VISIBLE


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
        likes_count = fetchCommentsFromDB()


        // listview long click remove
        listView.setOnItemClickListener { parent, view, position, id ->

            commentsDataList.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        return view

    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.hearts -> heartLikes()
            R.id.settings -> settings()
            R.id.comments -> postComments()             // call for commenting
            R.id.backtext -> fragmentManager!!.popBackStack()

        }

    }

    // heart likes capture
    fun heartLikes() {

        hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)

        var new_count = likes_count!!.plus(1)
        likes.text = "$new_count Likes"
        hashMap_forHearts.put("LIKES_COUNT", new_count.toString())
        hashMap_forHearts.put("LIKED_USER", imageGallery.userName)

        uploadLikes(hashMap_forHearts)
    }

    //upload the likes field in cloud db
    fun uploadLikes(map: HashMap<String, String>) {
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var sub_coll_likes: DocumentReference
                    sub_coll_likes =
                        document.reference.collection("likes").document("LIKES_FOR_" + imageFileName.substring(0,3))


                    sub_coll_likes.set(map , SetOptions.merge())
                        .addOnSuccessListener {
                            OnSuccessListener<Void> {
                                Log.d(
                                    Constants.TAG,
                                    "Uploaded  collection likes to cloud db successfully"
                                )
                            }
                        }
                        .addOnFailureListener(OnFailureListener { exception: Exception ->
                            Log.d(
                                Constants.TAG,
                                "failure to upload collection likes to cloud db"
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


    //delete function
    fun settings() {

        val inflater = this.layoutInflater
        var alertViewS = inflater.inflate(R.layout.settings_layout, null)  // why am i passing null
        var builderS = AlertDialog.Builder(this.context)
        builderS.setView(alertViewS)

        var alertDialogS = builderS.create()

        var deleteB = alertViewS.findViewById<EditText>(R.id.delete_id)
        var cancelB = alertViewS.findViewById<Button>(R.id.cancel_id)

        deleteB.setOnClickListener(View.OnClickListener { l->

            fragmentManager!!.popBackStack("imageViewAdapter_backStack",FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //deleteInFB()

        })


        cancelB.setOnClickListener(View.OnClickListener { v->

            alertDialogS.dismiss()


        })

        alertDialogS.show()
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
        var i: Long = 0
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener(OnSuccessListener { result ->
                Log.d(TAG, "success getting documents : images")
                for (document in result) {

                    //get from collection comments
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

                    //get from collection likes
                    document.reference.collection("likes").get()
                        .addOnSuccessListener(OnSuccessListener { result ->
                            Log.d(TAG, "success getting documents:likes ")
                            for (comment_data in result) {

                                var data = comment_data.data

                                var count = data.get("LIKES_COUNT")
                                liked_user = data.get("LIKED_USER") as String

                                if(imageGallery.userName.equals(liked_user)){
                                    hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)

                                }

                                likes.text= "$count Likes"



                            }

                        })
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents : likes")
                        }


                }

            })
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents : images")
            }

        return i
    }
}


// each user gets adding to the like

// just check if you need a field for likes so that u get the whole count
//also images on the toolbar check 




