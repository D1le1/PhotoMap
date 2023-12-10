package by.korsakovegor.photomap.mainactivity.photos.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoIn
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PhotosViewModel : ViewModel() {
    private val _images = MutableLiveData<ArrayList<ImageDtoOut>>()
    val images: LiveData<ArrayList<ImageDtoOut>> get() = _images

    private val _comments = MutableLiveData<ArrayList<CommentDtoOut>>()
    val comments: LiveData<ArrayList<CommentDtoOut>> get() = _comments

    private val _comment = MutableLiveData<CommentDtoOut>()
    val comment: LiveData<CommentDtoOut> get() = _comment

    private val _deletedItem = MutableLiveData<Int>()
    val deletedItem: LiveData<Int> get() = _deletedItem

    fun getImages() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest("https://junior.balinasoft.com/api/image?page=0")
            val resImages = JsonParser.jsonToImageList(response)
            if (resImages != null)
                _images.postValue(resImages)
        }
    }

    fun getComments(imageId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                sendGetRequest("https://junior.balinasoft.com/api/image/$imageId/comment?page=0")
            Log.d("D1le", response)
            val resComments = JsonParser.jsonToCommentList(response)
            if (resComments != null)
                _comments.postValue(resComments)
        }
    }

    fun sendComment(comment: CommentDtoIn, imageId: Int?) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment"
        val accept = "*/*"
        val contentType = "application/json"
        val token = "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP"
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                sendPostRequest(url, JsonParser.commentToJson(comment), accept, contentType, token)
            Log.d("D1le", response)
            val resComment = JsonParser.jsonToComment(response)
            if (resComment != null)
                _comment.postValue(resComment)
        }
    }

    fun deleteComment(comment: CommentDtoOut, imageId: Int?, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/$imageId/comment/${comment.id}"
        val token = "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, token)
            Log.d("D1le", response)
            if (JsonParser.jsonCheckDelete(response))
                _deletedItem.postValue(pos)
        }
    }

    fun deleteImage(image: ImageDtoOut, pos: Int) {
        val url = "https://junior.balinasoft.com/api/image/${image.id}"
        val token = "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP"
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendDeleteRequest(url, token)
            if (JsonParser.jsonCheckDelete(response))
                _deletedItem.postValue(pos)
        }
    }

    private fun sendGetRequest(url: String): String {
        val client = OkHttpClient()

        val request =
            Request.Builder().url(url).addHeader("accept", "*/*")
                .addHeader(
                    "Access-Token",
                    "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP"
                ).build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    private fun sendPostRequest(
        url: String,
        jsonBody: String,
        accept: String,
        contentType: String,
        accessToken: String
    ): String {
        val client = OkHttpClient()

        val requestBody = jsonBody.toRequestBody()

        val request =
            Request.Builder().url(url).post(requestBody).addHeader("accept", accept)
                .addHeader("Content-Type", contentType)
                .addHeader("Access-Token", accessToken)
                .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    private fun sendDeleteRequest(url: String, accessToken: String): String {
        val client = OkHttpClient()

        val request =
            Request.Builder().url(url).delete().addHeader("accept", "*/*")
                .addHeader("Access-Token", accessToken)
                .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}