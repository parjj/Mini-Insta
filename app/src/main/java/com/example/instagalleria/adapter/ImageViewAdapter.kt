package com.example.instagalleria.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.instagalleria.R
import com.example.instagalleria.model.UploadImage


class ImageViewAdapter(var context: Context, var images_urls:ArrayList<UploadImage> ) : RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        var view= LayoutInflater.from(parent.context).inflate(R.layout.listof_photos,parent,false)

        val viewHolder = ImageViewHolder(view) // pass the view to View Holder
        return viewHolder
    }

    override fun getItemCount(): Int {
        return images_urls.size
    }

    override fun onBindViewHolder(imageHolder: ImageViewHolder, pos: Int) {

        var uri= images_urls.get(pos).uri

        //imageHolder.imageView.setImageURI(uri)

       // imageHolder.imageView.setBackgroundResource(R.color.background_material_dark)

        Glide.with(context).load(uri).into(imageHolder.imageView);
    }





    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView= itemView.findViewById<ImageView>(R.id.photo_images)

    }

}






//The views in the list are represented by view holder objects.
// These objects are instances of a class you define by extending RecyclerView.ViewHolder. Each view holder is
// in charge of displaying a single item with a view. For example, if your list shows music collection, each view holder might represent a single album.


// solution  for supertype initialization is impossible without primary constructor
//The super constructor rules complicate the matter to some degree.
//
//If you define a secondary constructor in the derived class without defining the primary constructor
// (no parentheses near the class declaration), then the secondary constructor itself should call the super constructor, and no super constructor arguments should be specified in the class declaration:
//
//class test2 : test { // no arguments for `test` here
//    constructor(a: Int) : super() { /* ... */ }
//}
//Another option is define the primary constructor and call it from the secondary one:
//
//class test2() : test() {
//    constructor(a: Int) : this() { /* ... */ }
//}



