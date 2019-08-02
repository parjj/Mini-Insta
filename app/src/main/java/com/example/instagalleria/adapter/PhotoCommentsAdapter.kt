package com.example.instagalleria.adapter

import android.content.Context
import android.drm.DrmStore
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.instagalleria.R
import com.example.instagalleria.model.CommentsData
import java.util.ArrayList

class PhotoCommentsAdapter(var commentsList: ArrayList<CommentsData>) :
    RecyclerView.Adapter<PhotoCommentsAdapter.PhotoCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCommentViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.comments_layout, parent, false)

        val viewHolder = PhotoCommentViewHolder(view) // pass the view to View Holder
        return viewHolder

    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(photoCommentViewHolder: PhotoCommentViewHolder, pos: Int) {

        var commentsData = commentsList.get(pos)

        photoCommentViewHolder.textUserView.setText(commentsData.username)
        photoCommentViewHolder.commentsView.setText(commentsData.comments)
    }

     class PhotoCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textUserView = itemView.findViewById<TextView>(R.id.commentsUserName)
        var commentsView = itemView.findViewById<TextView>(R.id.commentsByUser)
        var layout = itemView.findViewById<ConstraintLayout>(R.id.constraintLayout_comments)

    }
}