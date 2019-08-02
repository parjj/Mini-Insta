package com.example.instagalleria.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.instagalleria.MainActivity
import com.example.instagalleria.R
import com.example.instagalleria.adapter.CommentsSectionAdapter
import com.example.instagalleria.adapter.PhotoCommentsAdapter
import com.example.instagalleria.model.CommentsData
import com.example.instagalleria.model.Constants
import com.example.instagalleria.model.Constants.Companion.TAG
import com.example.instagalleria.model.OnBackPressed
import com.example.instagalleria.model.UploadImage
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import android.support.v4.content.ContextCompat.getSystemService





class PhotoDetailFragment : Fragment(), View.OnClickListener, OnBackPressed {

    lateinit var imageView: ImageView
    lateinit var hearts_button: ImageButton
    lateinit var settings_button: ImageButton
    lateinit var comments_button: ImageView
    lateinit var likes: TextView
    lateinit var commentsnts_linearLL: LinearLayout
    lateinit var settings_linearLL: LinearLayout
    lateinit var post_comment: EditText
    lateinit var post_button: Button
    lateinit var deleteB: Button
    lateinit var cancelB: Button

    lateinit var listView: ListView
    lateinit var list_adapter: CommentsSectionAdapter

    private lateinit var comments_data: CommentsData

    //    lateinit var recyclerView: RecyclerView
    var commentsDataList = ArrayList<CommentsData>()
    lateinit var adapter: PhotoCommentsAdapter

    var imageGallery = ImageGalleryViewFragment()

    var likes_count = 0L
    var liked_user_name: String? = null
    var comm_from_db : String?= null
    var userNameCheck = "false"
    lateinit var currentUserNameCheck: String

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
    private lateinit var mainActivity:MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.image_detail, container, false)
        var bundle = arguments
        uriString = bundle!!.getString("uri_image_value")  // comming from image view adapter
        imageFileName = bundle!!.getString("uri_image_fileName")  // comming from image view adapter
        imageUserName = bundle!!.getString("uri_image_userName")  // comming from image view adapter

        imageGallery = fragmentManager!!.fragments.get(3) as ImageGalleryViewFragment

        settings_linearLL = view.findViewById(R.id.settings_layout)
        deleteB = view.findViewById(R.id.delete_id)
        cancelB = view.findViewById(R.id.cancel_id)


        currentUser = imageGallery.user_displayName

        mainActivity = activity as MainActivity
        mainActivity.fragment_toolbar_bottom.photoDeatilSetting()

        // load all the comments for the image from DB
        fetchCommentsFromDB()

        //recycler view
        // recyclerView = view.findViewById(R.id.commets_list)

        //list view
        listView = view.findViewById(R.id.commets_list)

        var inflater = this.layoutInflater
        var viewL = inflater.inflate(R.layout.header_view, null, false)

        imageView = viewL.findViewById(R.id.image_detail)
        hearts_button = viewL.findViewById(R.id.hearts)
        settings_button = viewL.findViewById(R.id.settings)
        comments_button = viewL.findViewById(R.id.comments)
        likes = viewL.findViewById(R.id.likes)
        post_comment = viewL.findViewById(R.id.comments_AD)
        post_button = viewL.findViewById(R.id.post_btn)
        commentsnts_linearLL = viewL.findViewById(R.id.commentsLinear)


        // recycler view
        //        var linearLayoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
        //        recyclerView.layoutManager = linearLayoutManager
        //        adapter = PhotoCommentsAdapter(commentsDataList)
        //        recyclerView.adapter = adapter


        listView.addHeaderView(viewL)
        list_adapter = CommentsSectionAdapter(context!!, commentsDataList)
        listView.adapter = list_adapter
        Glide.with(context as Context).load(uriString).into(imageView)

        post_comment.setText(R.string.empty)

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

                                        if (comments_doc.get("USERNAME")!!.equals(currentUser)) {
                                            comments_doc.reference.delete()
                                            commentsDataList.remove(commentsDataList[position])
                                            list_adapter.notifyDataSetChanged()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "You din't comment for this picture",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

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
            R.id.settings -> settings()  // delete photo function
            R.id.comments -> postComments()      // posting comments function
        }

    }

    // heart likes count and image setting functionality
    fun heartLikes() {

        if (usersList.size == 0) {    // when for the first time user uploads the images userlist 0
            if (likes_count >= 1) {
                if (hashMap_forHearts.get("USER_LIKED").equals("true")) {
                    hashMap_forHearts.put("USER_LIKED", "false")
                    hearts_button.setImageResource(R.drawable.icn_like_inactive_optimized)
                    likes_count = likes_count!!.minus(1)
                }
            } else {
                hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                hearts_button.setImageResource(R.drawable.icn_like_active_optimized)
                likes_count = likes_count!!.plus(1)
            }

        } else if (usersList.contains(currentUser)) {

            if (currentUserNameCheck.equals("true")) {
                hashMap_forHearts.put("USER_LIKED", "false")
                hearts_button.setImageResource(R.drawable.icn_like_inactive_optimized)
                likes_count = likes_count!!.minus(1)

            } else if (currentUserNameCheck.equals("false")) {

                hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                hearts_button.setImageResource(R.drawable.icn_like_active_optimized)
                likes_count = likes_count!!.plus(1)

            }
        } else {
            if (hearts_button.background.constantState.equals(
                    ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.icn_like_active_optimized,
                        null
                    )!!.constantState
                )
            ) {
                hashMap_forHearts.put("USER_LIKED", "false")   // likes the image
                hearts_button.setImageResource(R.drawable.icn_like_inactive_optimized)
                likes_count = likes_count!!.minus(1)

            } else {

                hashMap_forHearts.put("USER_LIKED", "true")   // likes the image
                hearts_button.setImageResource(R.drawable.icn_like_active_optimized)
                likes_count = likes_count!!.plus(1)
            }
        }
        if (likes_count < 0) {
            likes_count = 0
        }
        likes.text = "$likes_count Likes"
        hashMap_forHearts.put("LIKES_COUNT", likes_count.toString())
        hashMap_forHearts.put("USERNAME", currentUser)

        uploadLikes(hashMap_forHearts)
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

        commentsnts_linearLL.visibility = View.VISIBLE
        hearts_button.visibility = View.GONE
        settings_button.visibility = View.GONE
        comments_button.visibility = View.GONE
        likes.visibility = View.GONE


        //post button click
        post_button.setOnClickListener(View.OnClickListener { v ->

                    val ipmm = this.activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        ipmm!!.hideSoftInputFromWindow(post_comment.getWindowToken(), 0)


            comments_str = post_comment.text.toString()

            hashMap.put("COMMENTS", comments_str)
            hashMap.put("USERNAME", currentUser)

            //upload comments to cloud FB
            uploadCommentsToStorage(hashMap)

            commentsList.add(comments_str)
            comments_data = CommentsData(currentUser, comments_str)
            commentsDataList.add(comments_data)
            list_adapter.notifyDataSetChanged()

            commentsnts_linearLL.visibility = View.GONE
            hearts_button.visibility = View.VISIBLE
            settings_button.visibility = View.VISIBLE
            comments_button.visibility = View.VISIBLE
            likes.visibility = View.VISIBLE

        })

    }


    //delete function
    fun settings() {

        settings_linearLL.visibility=View.VISIBLE

        //delete button click
        deleteB.setOnClickListener(View.OnClickListener { l ->

             Constants.storageRef.child(imageFileName).delete()
            //commentsDataList.remove(CommentsData(imageUserName, comm_from_db))
            list_adapter.notifyDataSetChanged()
            //  commentsDataList.removeAt(position)
            Constants.db_storageRef.whereEqualTo("URI", uriString).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        var nameString = document.data
                        var profile_user = nameString.get("USER")
                        if (profile_user != null) {
                            if (profile_user.equals(currentUser)) {
                                document.reference.delete()
                                imageGallery.uploadList.remove(UploadImage(imageFileName, uriString, imageUserName))
                                imageGallery.adapter.notifyDataSetChanged()

                                fragmentManager!!.popBackStack()

                            } else {
                                Toast.makeText(context, "You din't upload this picture", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            settings_linearLL.visibility=View.GONE

        })

        // cancel  button click
        cancelB.setOnClickListener(View.OnClickListener
        { v -> this.activity!!.finish()
            settings_linearLL.visibility=View.GONE
        })

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

                                var comnt = data.get("COMMENTS")
                                comm_from_db = comnt as String
                                var comm_user = data.get("USERNAME")

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
                                userNameCheck = data.get("USER_LIKED") as String

                                usersList.add(liked_user_name.toString())


                                if (liked_user_name.equals(currentUser)) {
                                    if (userNameCheck.equals("true")) {
                                        currentUserNameCheck = userNameCheck as String
                                        hearts_button.setImageResource(R.drawable.icn_like_active_optimized)
                                    } else {
                                        currentUserNameCheck = "false"
                                        hearts_button.setImageResource(R.drawable.icn_like_inactive_optimized)

                                    }
                                }
                            }
                            likes.text = "$likes_count Likes"
                        })
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents : likes")
                        }
                }
            })
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents : images")
            }

    }


    override fun OnBackPressed() {
        mainActivity.fragment_toolbar_top.goBack()
    }

}