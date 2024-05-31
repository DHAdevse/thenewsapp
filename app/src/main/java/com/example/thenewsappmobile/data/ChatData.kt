package com.ahmedapps.thenewsapp.data
import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
Person Responsible for this class : DINH HUU QUANG ANH - 21110752
*/
object ChatData {

    val val_api_key = "AIzaSyDdLeVmeebtRIX0vYdUyoqdOIK7z1P5lAQ"
    //The getResponse function is a suspend function that takes a string prompt to start and returns a Chat object.
    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = val_api_key
        )
        try {
            // Convert execution context to I/O context
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            // user model to return the response
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }
    //The getResponseWithImage function is a suspend function that takes Image prompt to start and returns a Chat object.
    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = val_api_key
        )

        try {
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}




















