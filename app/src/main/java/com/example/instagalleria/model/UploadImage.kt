package com.example.instagalleria.model

import android.net.Uri
import java.io.Serializable

data class UploadImage(var filename:String, var uri: Uri)  : Serializable


// include later with like unliked loved - users