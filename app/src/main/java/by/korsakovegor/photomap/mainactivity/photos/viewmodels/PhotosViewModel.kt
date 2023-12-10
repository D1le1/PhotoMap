package by.korsakovegor.photomap.mainactivity.photos.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PhotosViewModel(private val user: SignUserOutDto) : ViewModel() {

    private val _images = MutableLiveData<ArrayList<ImageDtoOut>>()
    val images: LiveData<ArrayList<ImageDtoOut>> get() = _images

    private val _comments = MutableLiveData<ArrayList<CommentDtoOut>>()
    val comments: LiveData<ArrayList<CommentDtoOut>> get() = _comments

    private val _comment = MutableLiveData<CommentDtoOut>()
    val comment: LiveData<CommentDtoOut> get() = _comment

    private val _deletedItem = MutableLiveData<Int>()
    val deletedItem: LiveData<Int> get() = _deletedItem

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    fun getImages() {
        val url = "https://junior.balinasoft.com/api/image?page=0"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest(url, user.token)
            if (response!!.isNotEmpty()) {
                val resImages = JsonParser.jsonToImageList(response)
                if (resImages != null) _images.postValue(resImages)
            } else
                _error.postValue(true)
        }
    }

    fun getComments(imageId: Int?) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment?page=0"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest(url, user.token)
            Log.d("D1le", response)
            val resComments = JsonParser.jsonToCommentList(response)
            if (resComments != null) _comments.postValue(resComments)
        }
    }

    fun sendComment(comment: CommentDtoIn, imageId: Int?) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment"
        val accept = "*/*"
        val contentType = "application/json"
        val jsonBody = JsonParser.commentToJson(comment)
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                sendPostRequest(url, jsonBody, accept, contentType, user.token)
            Log.d("D1le", response)
            val resComment = JsonParser.jsonToComment(response)
            if (resComment != null) _comment.postValue(resComment)
        }
    }

    fun deleteComment(comment: CommentDtoOut, imageId: Int?, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment/${comment.id}"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, user.token)
            Log.d("D1le", response)
            if (JsonParser.jsonCheckDelete(response)) _deletedItem.postValue(pos)
        }
    }

    fun deleteImage(image: ImageDtoOut, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/${image.id}"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, user.token)
            if(response.isNotEmpty()) {
                Log.d("D1le", response)
                if (JsonParser.jsonCheckDelete(response)) _deletedItem.postValue(pos)
            }else
                _error.postValue(true)
        }
    }

    private fun sendGetRequest(url: String, token: String): String {
        return try {
            val client = OkHttpClient()

            val request = Request.Builder().url(url).addHeader("accept", "*/*").addHeader(
                "Access-Token", token
            ).build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun sendPostRequest(
        url: String, jsonBody: String, accept: String, contentType: String, accessToken: String
    ): String {
        return try {
            val client = OkHttpClient()

            val requestBody = jsonBody.toRequestBody()

            val request = Request.Builder().url(url).post(requestBody).addHeader("accept", accept)
                .addHeader("Content-Type", contentType).addHeader("Access-Token", accessToken)
                .build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun sendDeleteRequest(url: String, accessToken: String): String {
        return try {
            val client = OkHttpClient()

            val request = Request.Builder().url(url).delete().addHeader("accept", "*/*")
                .addHeader("Access-Token", accessToken).build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}