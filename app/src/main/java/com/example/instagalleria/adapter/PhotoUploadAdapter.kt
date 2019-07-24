package com.example.instagalleria.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.instagalleria.R

class PhotoUploadAdapter() : BaseAdapter() {

    var image_list = ArrayList<Uri>()
    lateinit var contex: Context

    constructor(context: Context,image_list:ArrayList<Uri>) : this() {

        this.contex=context
        this.image_list=image_list
    }

    override fun getItem(position: Int): Any {
        return image_list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return image_list.size
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var layoutInflater = contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var view = layoutInflater.inflate(R.layout.listof_photos, parent, false)

        var imageView = view.findViewById(R.id.photo_images) as ImageView


        imageView.setImageURI(image_list.get(position))


//        uploadImage.uri
//        imageView.setImageBitmap()
         //Glide.with(contex).load(image_list.get(position)).into(imageView);
        return view
    }


}


//Here is the difference:
//
//BaseAdapter is a very generic adapter that allows you to do pretty much whatever you want. However, you have to do a bit more coding yourself to get it working.
//ArrayAdapter is a more complete implementation that works well for data in arrays or ArrayLists. Similarly, there is a related CursorAdapter that you should use if your data is in a Cursor. Both of these extend BaseAdapter.
//If your data is in a specialized collection of some sort or if you don't want the default behavior that ArrayAdapter provides, you will likely want to extend BaseAdapter to get the flexibility you need.
//
//The performance of each really depends on how you implement them or change their behavior. At their core, either one can be just as effective (especially considering that an ArrayAdapter is a BaseAdapter).
//
//You can do pretty much whatever you want with any adapter, but keep in mind that BaseAdapter is abstract, so you can't use it directly.

//In Kotlin val declares final, read only, reference - and that is exactly what compiler error is telling you with
//
//Val cannot be reassigned
//
//Once you assign value to val, it cannot be changed. If you want to be able to reassign it you have to declare it as var