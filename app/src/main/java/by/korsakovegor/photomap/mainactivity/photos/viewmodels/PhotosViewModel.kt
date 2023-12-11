package by.korsakovegor.photomap.mainactivity.photos.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoIn
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PhotosViewModel : ViewModel() {

    private val _images = MutableLiveData<ArrayList<ImageDtoOut>?>()
    val images: LiveData<ArrayList<ImageDtoOut>?> get() = _images

    private val _image = MutableLiveData<ImageDtoOut?>()
    val image: LiveData<ImageDtoOut?> get() = _image

    private val _comments = MutableLiveData<ArrayList<CommentDtoOut>?>()
    val comments: LiveData<ArrayList<CommentDtoOut>?> get() = _comments

    private val _comment = MutableLiveData<CommentDtoOut?>()
    val comment: LiveData<CommentDtoOut?> get() = _comment

    private val _deletedItem = MutableLiveData<Int>()
    val deletedItem: LiveData<Int> get() = _deletedItem

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val userToken = MutableLiveData<String>()

    fun setUserToken(token: String) {
        userToken.value = token
    }

    fun getImages(page: Int) {
        val url = "https://junior.balinasoft.com/api/image?page=$page"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest(url, userToken.value.toString())
            Log.d("D1le", "PAGE: $page - $response")
            if (response.isNotEmpty()) {
                val resImages = JsonParser.jsonToImageList(response)
                if (resImages != null) _images.postValue(resImages)
            } else
                _error.postValue("")
        }
    }

    fun sendImage(image: ImageDtoIn, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonBody = JsonParser.imageToJson(image)

            val url = "https://junior.balinasoft.com/api/image"
            val accept = "application/json;charset=UTF-8"
            val contentType = "application/json;charset=UTF-8"

            val response = sendPostRequest(url, jsonBody, accept, contentType, token)
            if (response.isNotEmpty()) {
                val resImage = JsonParser.jsonToImage(response)
                if (resImage != null) _image.postValue(resImage)
                else _error.postValue("Some problems with image tra again later")
            }else
                _error.postValue("")
        }

    }

    fun getComments(imageId: Int?) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment?page=0"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest(url, userToken.value.toString())
            if (response.isNotEmpty()) {
                Log.d("D1le", response)
                val resComments = JsonParser.jsonToCommentList(response)
                if (resComments != null) _comments.postValue(resComments)
            } else
                _error.postValue("")
        }
    }

    fun sendComment(comment: CommentDtoIn, imageId: Int?) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment"
        val accept = "*/*"
        val contentType = "application/json"
        val jsonBody = JsonParser.commentToJson(comment)
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                sendPostRequest(url, jsonBody, accept, contentType, userToken.value.toString())
            if (response.isNotEmpty()) {
                Log.d("D1le", response)
                val resComment = JsonParser.jsonToComment(response)
                if (resComment != null) _comment.postValue(resComment)
            } else
                _error.postValue("")
        }
    }

    fun deleteComment(comment: CommentDtoOut, imageId: Int?, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment/${comment.id}"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, userToken.value.toString())
            if (response.isNotEmpty()) {
                Log.d("D1le", response)
                if (JsonParser.jsonCheckDelete(response))
                    _deletedItem.postValue(pos)
                else
                    _error.postValue("Something went wrong try it later")
            } else {
                _error.postValue("")
            }
        }
    }

    fun deleteImage(image: ImageDtoOut, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/${image.id}"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, userToken.value.toString())
            if (response.isNotEmpty()) {
                Log.d("D1le", response)
                if (JsonParser.jsonCheckDelete(response)) _deletedItem.postValue(pos)
                else _error.postValue("Can't delete image with comments")
            } else
                _error.postValue("")
        }
    }

    private fun sendGetRequest(url: String, token: String): String {
        return try {
            val client = OkHttpClient()

            val request = Request.Builder().url(url).addHeader("accept", "*/*").addHeader(
                "Access-Token", token
            ).build()

            val response = client.newCall(request).execute()
            return response.body?.string() ?: ""
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