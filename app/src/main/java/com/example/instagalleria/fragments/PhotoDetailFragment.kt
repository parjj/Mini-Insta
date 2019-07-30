package com.example.instagalleria.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.instagalleria.R
import com.example.instagalleria.adapter.CommentsSectionAdapter
import com.example.instagalleria.adapter.PhotoCommentsAdapter
import com.example.instagalleria.model.CommentsData
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.UploadImage
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.image_detail.*
import kotlinx.android.synthetic.main.user_login.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PhotoDetailFragment : Fragment(), View.OnClickListener {

    lateinit var imageView: ImageView
    lateinit var hearts_button: ImageButton
    lateinit var settings_button: ImageButton
    lateinit var comments_button: ImageView
    lateinit var likes: TextView

    lateinit var listView: ListView
    lateinit var list_adapter: CommentsSectionAdapter

    private lateinit var comments_data: CommentsData

    //    lateinit var recyclerView: RecyclerView
    var commentsDataList = ArrayList<CommentsData>()
    lateinit var adapter: PhotoCommentsAdapter

    var imageGallery = ImageGalleryViewFragment()

    var likes_count: Long = 0
    var position: Int = 0
    var liked_user_name: String? = null
    var userCheck: String = "true"
    var comm_from_db: String? = null
    var userNameCheck: String? = "false"
    lateinit var currentUserNameCheck:String

    private lateinit var uriString: String
    private lateinit var imageFileName: String
    private lateinit var imageUserName: String
    private lateinit var currentUser: String
    private lateinit var comments_str: String
    private var commentsList = ArrayList<String>()
    private var usersList = ArrayList<String>()
    private var no_of_likes = ArrayList<Long>()
    var hashMap: HashMap<String, String> = HashMap<String, String>()
    var hashMap_forHearts: HashMap<String, String> = HashMap<String, String>()
    var fetch_datas: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_detail, container, false)

        var bundle = arguments
        uriString = bundle!!.getString("uri_image_value")  // comming from image view adapter
        imageFileName = bundle!!.getString("uri_image_fileName")  // comming from image view adapter
        imageUserName = bundle!!.getString("uri_image_userName")  // comming from image view adapter
        position = bundle!!.getInt("image_pos")  // comming from image view adapter

        imageGallery = fragmentManager!!.fragments.get(3) as ImageGalleryViewFragment

        currentUser = imageGallery.user_displayName

        imageGallery.fragment_login.fragment_toolbar_top.toolbar_title.setText("Photo Detail")
        imageGallery.fragment_login.fragment_toolbar_top.toolbar_back.visibility = View.VISIBLE
        imageGallery.fragment_login.fragment_toolbar_bottom.toolbar_right.setImageResource(R.drawable.icn_photo_inactive_optimized)
        imageGallery.fragment_login.fragment_toolbar_bottom.toolbar_left.setImageResource(R.drawable.home_active_optimized)

        imageGallery.fragment_login.fragment_toolbar_bottom.toolbar_left.setOnClickListener(View.OnClickListener { t ->

            imageGallery.fragment_login.fragment_toolbar_top.toolbar_title.setText(getString(R.string.app_name))
            imageGallery.fragment_login.fragment_toolbar_top.toolbar_back.visibility = View.GONE
            imageGallery.fragment_login.fragment_toolbar_bottom.toolbar_left.setImageResource(R.drawable.home_inactive_optimized)
            imageGallery.fragment_login.fragment_toolbar_bottom.toolbar_right.setImageResource(R.drawable.icn_photo_active_optimized)
            fragmentManager!!.popBackStack("imageViewAdapter_backStack", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        })


        // load all the comments for the image from DB
        fetchCommentsFromDB()

        imageView = view.findViewById(R.id.image_detail)
        hearts_button = view.findViewById(R.id.hearts)
        settings_button = view.findViewById(R.id.settings)
        comments_button = view.findViewById(R.id.comments)
        likes = view.findViewById(R.id.likes)
        // recyclerView = view.findViewById(R.id.commets_list)
        listView = view.findViewById(R.id.commets_list)

//        var linearLayoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
//        recyclerView.layoutManager = linearLayoutManager
//        adapter = PhotoCommentsAdapter(commentsDataList)
//        recyclerView.adapter = adapter

        list_adapter = CommentsSectionAdapter(context!!, commentsDataList)
        listView.adapter = list_adapter

        Glide.with(context as Context).load(uriString).into(imageView)

        hearts_button.setOnClickListener(this) // hearts button click
        settings_button.setOnClickListener(this)// settings button click
        comments_button.setOnClickListener(this)// comments button click

        // listview long click remove   comments delete function
        listView.setOnItemLongClickListener(AdapterView.OnItemLongClickListener { parent, view, position, id ->


            var popupMenu = PopupMenu(this.context, view)

            var inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.pop_up_delete, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->


                Constants.db_storageRef.whereEqualTo("URI", uriString).get()
                    .addOnSuccessListener(OnSuccessListener { result ->
                        for (document in result) {

                            //get from collection comments
                            document.reference.collection("comments")
                                .whereEqualTo("COMMENTS", commentsDataList.get(position).comments).get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (comments_doc in querySnapshot) {
                                        comments_doc.reference.delete()
                                        commentsDataList.remove(commentsDataList[position])
                                        list_adapter.notifyDataSetChanged()
                                    }
                                }
                                .addOnFailureListener(OnFailureListener { exception: Exception ->
                                    Log.d(Constants.TAG, "failure to delete comments from  cloud db")
                                })
                        }
                    })
                    .addOnFailureListener(OnFailureListener { exception: Exception ->
                        Log.d(Constants.TAG, "failure to delete comments from  cloud db")
                    })

                true
            })
            true

        })

        return view
    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.hearts -> heartLikes()  // on heart button click
            R.id.settings -> settings()
            R.id.comments -> postComments()             // call for commenting


        }

    }

    // heart likes capture
    fun heartLikes() {

        Toast.makeText(context, "You clicked heart button ", Toast.LENGTH_SHORT).show()
        var new_count: Long = 0

        if (usersList.size == 0) {
            if (likes_count >= 1) {
                if (hashMap_forHearts.get("USER_LIKED").equals("true")) {
                    hashMap_forHearts.put("USER_LIKED", "false")
                    hearts_button.setBackgroundResource(R.drawable.icn_like_inactive_optimized)
                    likes_count = likes_count!!.minus(1)
                }
            } else {
                hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)
                likes_count = likes_count!!.plus(1)
            }

        } else if (usersList.contains(currentUser)) {

            if (currentUserNameCheck.equals("true")) {
                hashMap_forHearts.put("USER_LIKED", "false")
                hearts_button.setBackgroundResource(R.drawable.icn_like_inactive_optimized)
                likes_count = likes_count!!.minus(1)

            } else if (currentUserNameCheck.equals("false")) {

                hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)
                likes_count = likes_count!!.plus(1)

            }
        } else {
                if (hearts_button.background.constantState.equals(ResourcesCompat.getDrawable(getResources(), R.drawable.icn_like_active_optimized, null)!!.constantState)) {
                    hashMap_forHearts.put("USER_LIKED", "false")   // likes the image
                    hearts_button.setBackgroundResource(R.drawable.icn_like_inactive_optimized)
                    likes_count = likes_count!!.minus(1)

                } else {

                    hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                    hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)
                    likes_count = likes_count!!.plus(1)
                }
            }
        likes.text = "$likes_count Likes"
        hashMap_forHearts.put("LIKES_COUNT", likes_count.toString())
        hashMap_forHearts.put("USERNAME", currentUser)

        uploadLikes(hashMap_forHearts)
    }


    fun dislike(newCount: Long): Long {
        hearts_button.setOnClickListener(View.OnClickListener { v ->
            hearts_button.setBackgroundResource(R.drawable.icn_like_inactive_optimized)
            newCount!!.minus(1)
            hearts.setOnClickListener(this)
            hashMap_forHearts.put("USER_LIKED", "false")

        })
        return newCount
    }

    //upload the likes field in cloud db
    fun uploadLikes(map: HashMap<String, String>) {
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var sub_coll_likes: DocumentReference
                    sub_coll_likes =
                        document.reference.collection("likes")
                            .document("LIKES_BY_" + currentUser)


                    sub_coll_likes.set(map, SetOptions.merge())
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

        var post_comment = alertView.findViewById<EditText>(R.id.comments_AD)
        var post_button = alertView.findViewById<Button>(R.id.post_btn)

        //post button click
        post_button.setOnClickListener(View.OnClickListener { v ->

            comments_str = post_comment.text.toString()

            hashMap.put("COMMENTS", comments_str)
            hashMap.put("USERNAME", currentUser) // username not receiving properly

            //upload comments to cloud FB
            uploadCommentsToStorage(hashMap)

            commentsList.add(comments_str)
            comments_data = CommentsData(currentUser, comments_str)
            commentsDataList.add(comments_data)
            list_adapter.notifyDataSetChanged()

            Log.d(TAG, "inside post" + commentsDataList.toString())

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

        var deleteB = alertViewS.findViewById<Button>(R.id.delete_id)
        var cancelB = alertViewS.findViewById<Button>(R.id.cancel_id)

        //delete button click
        deleteB.setOnClickListener(View.OnClickListener { l ->

            Constants.storageRef.child(imageFileName).delete()
            //commentsDataList.remove(CommentsData(imageUserName, comm_from_db))
            list_adapter.notifyDataSetChanged()
            //  commentsDataList.removeAt(position)
            Constants.db_storageRef.whereEqualTo("URI", uriString).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.delete()
                    }
                }
            imageGallery.uploadList.remove(UploadImage(imageFileName, uriString, imageUserName))
            imageGallery.adapter.notifyDataSetChanged()

            alertDialogS.dismiss()
            fragmentManager!!.popBackStack("imageViewAdapter_backStack", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        })

        // cancel  button click
        cancelB.setOnClickListener(View.OnClickListener
        { v ->

            alertDialogS.dismiss()


        })

        alertDialogS.show()
    }

    // upload the comments to cloud DB
    fun uploadCommentsToStorage(comments_map: HashMap<String, String>) {

        var comnts_fileName = comments_map.get("COMMENTS")

        if (comnts_fileName.equals(comm_from_db)) {

            comnts_fileName = comnts_fileName + UUID.randomUUID().toString()
        }

        if (comnts_fileName!!.length < 3) {
            comnts_fileName = comnts_fileName.plus("00")
        }
        Constants.db_storageRef.whereEqualTo("URI", uriString).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var sub_coll: DocumentReference
                    sub_coll =
                        document.reference.collection("comments").document("CMNT_FOR_" + comnts_fileName)

                    sub_coll.set(comments_map)
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

    // fetch comments from cloud DB
    fun fetchCommentsFromDB() {

        var count: String = "0"
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

                                var co = data.get("COMMENTS")
                                comm_from_db = co as String
                                var comm_user = data.get("USERNAME")

                                Log.d(TAG, "inside fetch comments " + comm_from_db)
                                Log.d(TAG, "inside fetch  username" + comm_user)

                                comments_data = CommentsData(comm_user as String, comm_from_db as String)
                                commentsDataList.add(comments_data)
                                // commentsDataList.add(comments_data)


                            }


                            //list.addAll(commentsDataList)
                            //  recyclerView.adapter!!.notifyDataSetChanged()
                            list_adapter.notifyDataSetChanged()
                            Log.d(TAG, "full list of datalist  " + commentsDataList)
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

                                count = data.get("LIKES_COUNT") as String
                                if (count is String) {
                                    likes_count = count.toLong()


                                }

                                no_of_likes.add(likes_count)
                                liked_user_name = data.get("USERNAME") as String?
                                userNameCheck = data.get("USER_LIKED") as String?

                                usersList.add(liked_user_name.toString())


                                if (liked_user_name.equals(currentUser)) {
                                    if (userNameCheck.equals("true")) {
                                        currentUserNameCheck=userNameCheck as String
                                        hearts_button.setBackgroundResource(R.drawable.icn_like_active_optimized)
                                    } else {
                                        currentUserNameCheck = "false"
                                        hearts_button.setBackgroundResource(R.drawable.icn_like_inactive_optimized)

                                    }
                                }


                            }
                            likes.text = "$likes_count Likes"
                        })
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents : likes")
                        }
                }
                //setComments(commentsDataList)

            })
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents : images")
            }

    }


//
//    fun setComments( items: ArrayList<CommentsData>) {
//        if(items !=null) {
//            this.commentsDataList.clear();
//            this.commentsDataList.addAll(items);
//            adapter.notifyDataSetChanged()
//        }
//        else{
//             Log.d(TAG,"its null")
//        }
//}
}


// each user gets adding to the like

// just check if you need a field for likes so that u get the whole count
//also images on the toolbar check 




