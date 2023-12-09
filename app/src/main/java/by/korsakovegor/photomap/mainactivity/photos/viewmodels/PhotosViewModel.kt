package by.korsakovegor.photomap.mainactivity.photos.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoOut
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class PhotosViewModel: ViewModel() {
    private val _images = MutableLiveData<ArrayList<ImageDtoOut>>()
    val images: LiveData<ArrayList<ImageDtoOut>> get() = _images

    private val _comments = MutableLiveData<ArrayList<CommentDtoOut>>()
    val comments: LiveData<ArrayList<CommentDtoOut>> get() = _comments

    fun getImages(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest("https://junior.balinasoft.com/api/image?page=0")
            val resImages = JsonParser.jsonToImageList(response)
            if(resImages != null)
                _images.postValue(resImages)
        }
    }

    fun getComments(imageId: Int?){
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest("https://junior.balinasoft.com/api/image/$imageId/comment?page=0")
            val resComments = JsonParser.jsonToCommentList(response)
            if (resComments != null)
                _comments.postValue(resComments)
        }
    }

    private fun sendGetRequest(url: String):String {
        val client = OkHttpClient()

        val request =
            Request.Builder().url(url).addHeader("accept", "*/*")
                .addHeader("Access-Token", "6U5f0Hvae8OgyKpfWdnnW7l3Euvdw0TlFrlztYubaVZ59J4tKIfjd23MaAbWseXP").build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}