package by.korsakovegor.photomap.mainactivity.photos.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class PhotosViewModel: ViewModel() {
    private val _images = MutableLiveData<ArrayList<ImageDtoOut>>()
    val images: LiveData<ArrayList<ImageDtoOut>> get() = _images

    fun getImages(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendGetRequest("https://junior.balinasoft.com/api/image?page=0")
            val resImgs = JsonParser.jsonToImageList(response)
            if(resImgs != null)
                _images.postValue(resImgs)
        }
    }

    private fun sendGetRequest(url: String):String {
        val client = OkHttpClient()

        val request =
            Request.Builder().url(url).addHeader("accept", "*/*")
                .addHeader("Access-Token", "9yiYh82kqJhTkD5Iq06iXMwGEl9N3xOBaa21LgI1OlutY5pfWyVe4ZQ9WZRL93LU").build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}