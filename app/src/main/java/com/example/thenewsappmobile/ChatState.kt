package com.ahmedapps.thenewsapp
/*
Person Responsible for this class : DINH HUU QUANG ANH - 21110752
*/
import android.graphics.Bitmap
import com.ahmedapps.thenewsapp.data.Chat

// Data class named 'ChatState' which will hold the state of the chat.
data class ChatState (
    // A mutable list of 'Chat' objects, initialized as an empty mutable list.
    val chatList: MutableList<Chat> = mutableListOf(),
    // A string to hold the prompt, initialized as an empty string.
    val prompt: String = "",
    // A nullable Bitmap object, which can be used to store an image. Initially set to null.
    val bitmap: Bitmap? = null
)
