package com.ahmedapps.thenewsapp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedapps.thenewsapp.data.Chat
import com.ahmedapps.thenewsapp.data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/*
Person Responsible for this class : DINH HUU QUANG ANH - 21110752
*/

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    // Function to handle events from the UI
    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                // If the prompt is not empty, add it to the chat list
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt, event.bitmap)
                    // If the bitmap is not null, get a response with the image
                    if (event.bitmap != null) {
                        // Call getResponseWithImage function
                        getResponseWithImage(event.prompt, event.bitmap)
                    } else {
                        getResponse(event.prompt)
                    }
                }
            }

            // If the event is to update the prompt, update the prompt in the chat state
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    // Private function to add a prompt to the chat list
    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }

    // Private function to get a response for the given prompt
    private fun getResponse(prompt: String) {
        // Launch a coroutine to get a response for the given prompt
        viewModelScope.launch {
            // Get response from ChatData
            val chat = ChatData.getResponse(prompt)
            // Update the chat state with the new response
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }
    // Private function to get a response for the given prompt with an image
    private fun getResponseWithImage(prompt: String, bitmap: Bitmap) {
        viewModelScope.launch {
            //Get response with image from ChatData
            val chat = ChatData.getResponseWithImage(prompt, bitmap)

            // Update the chat state with the new response
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }
}


















