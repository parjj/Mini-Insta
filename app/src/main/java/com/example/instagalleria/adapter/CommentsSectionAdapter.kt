package com.example.instagalleria.adapter

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.instagalleria.R
import com.example.instagalleria.model.CommentsData
import java.util.zip.Inflater

class CommentsSectionAdapter() : BaseAdapter() {


    lateinit var context: Context
    lateinit var username: String

    lateinit var userList: ArrayList<String>
    lateinit var userCommentsList :ArrayList<String>

    lateinit var commentsList: ArrayList<CommentsData>

    constructor(context: Context,commentsList: ArrayList<CommentsData>) : this() {
        this.context = context
        this.commentsList = commentsList
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var data= getItem(position) as CommentsData;

        var layoutInflater = parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var view = layoutInflater.inflate(R.layout.comments_layout, parent, false)

        var textUserView = view.findViewById<TextView>(R.id.commentsUserName)
        var commentsView = view.findViewById<TextView>(R.id.commentsByUser)


        textUserView.text = data.username
        commentsView.text=data.comments


        return view
    }


    override fun getItem(position: Int): Any {
        return commentsList[position]
    }

    override fun getItemId(position: Int): Long {
        return  0
    }

    override fun getCount(): Int {
      return  commentsList.size
    }


}