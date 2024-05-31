package com.ahmedapps.thenewsapp

import android.graphics.Bitmap
/*
Person Responsible for this class : DINH HUU QUANG ANH - 21110752
*/

sealed class ChatUiEvent {
    //The purpose of this class is likely to represent an event where the prompt in the UI needs to be updated with a new value.
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    //The prompt with an image (bitmap), needs to be sent or processed.
    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?
    ) : ChatUiEvent()
}
