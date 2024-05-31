package com.example.thenewsappmobile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ahmedapps.thenewsapp.ChatUiEvent
import com.ahmedapps.thenewsapp.ChatViewModel
import com.ahmedapps.thenewsapp.ui.theme.GeminiChatBotTheme
import com.ahmedapps.thenewsapp.ui.theme.Green
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


class ChatBotMain: ComponentActivity() {
    private val uriState = MutableStateFlow("")


    // Register for image picker result
    private val imagePicker =
        // Use the ActivityResultContracts.PickVisualMedia() contract to pick an image
        registerForActivityResult<PickVisualMediaRequest, Uri?>(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            // Update the uri state with the selected image uri if it's not null
            uri?.let {
                uriState.update { uri.toString() }
            }
        }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiChatBotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary)
                                    .height(35.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopStart),
                                    text = stringResource(id = R.string.app_name),
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd),
                                    onClick = { navigateToMainActivity() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Back Home",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    ) {
                        ChatScreen(paddingValues = it)
                    }

                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Đóng ChatBotMain activity
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(paddingValues: PaddingValues) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .height(35.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopStart),
                        text = stringResource(id = R.string.app_name),
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        onClick = { navigateToMainActivity() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Back Home",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Get the ChatViewModel
            val chaViewModel = viewModel<ChatViewModel>()
            // Get the value of status from the ViewModel
            val chatState = chaViewModel.chatState.collectAsState().value

            val bitmap = getBitmap()

            // Column to display the chat messages
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    )
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.Bottom
            ) {
                // LazyColumn to display the chat messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    reverseLayout = true
                ) {
                    // Display the chat messages using itemsIndexed
                    itemsIndexed(chatState.chatList) { index, chat ->
                        if (chat.isFromUser) {
                            // If the chat is from the user, display the UserChatItem
                            UserChatItem(
                                prompt = chat.prompt, bitmap = chat.bitmap
                            )
                        } else {
                            // If the chat is from the model, display the ModelChatItem
                            ModelChatItem(response = chat.prompt)
                        }
                    }
                }
                // Row to display the input field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Column to display the image picker and send button
                    Column {
                        bitmap?.let {
                            Image(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(bottom = 2.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentDescription = "picked image",
                                contentScale = ContentScale.Crop,
                                bitmap = it.asImageBitmap()
                            )
                        }

                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest
                                            .Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build()
                                    )
                                },
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = "Add Image",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        modifier = Modifier
                            .weight(1f),
                        value = chatState.prompt,
                        onValueChange = {
                            chaViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                        },
                        placeholder = {
                            Text(text = "Enter your message")
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                chaViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                                uriState.update { "" }
                            },
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Send message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    @Composable
    fun UserChatItem(prompt: String, bitmap: Bitmap?) {
        //User prompt
        Column(
            modifier = Modifier.padding(start = 100.dp, bottom = 16.dp)
        ) {
            //User prompt image
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }
            // User prompt text
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                text = prompt,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }
    }

    @Composable
    fun ModelChatItem(response: String) {
        //Model response
        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
        ) {
            // Model response text
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Green)
                    .padding(16.dp),
                text = response,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }
    }

    //Get image from local storage and convert it to Bitmap
    @Composable
    private fun getBitmap(): Bitmap? {
        //Get the uri of the image by user
        val uri = uriState.collectAsState().value
        //Get the image state with the uri asynchrounously
        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state
        //If the image state is success, return bitmap
        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }
        return null
    }
}