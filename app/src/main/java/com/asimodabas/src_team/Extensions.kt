package com.asimodabas.src_team

import android.content.Context
import android.widget.Toast

fun Context.toastMessage(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}