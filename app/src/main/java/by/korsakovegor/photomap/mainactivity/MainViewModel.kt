package by.korsakovegor.photomap.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.CommentDtoOut
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainViewModel: ViewModel() {
    private val _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> get() = _currentFragment

    fun setFragment(fragment: Fragment){
        _currentFragment.postValue(fragment)
    }

    fun uploadImageToServer(base64Image: String, date: Long, lat: Double, lng: Double, token: String) {
        val jsonBody = JSONObject()
            .put("base64Image", base64Image)
            .put("date", date)
            .put("lat", lat)
            .put("lng", lng)
            .toString()

        val mediaType = "application/json;charset=UTF-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://junior.balinasoft.com/api/image")
            .post(requestBody)
            .addHeader("accept", "application/json;charset=UTF-8")
            .addHeader(
                "Access-Token",
                token
            )
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибок
                Log.d("D1le", e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("D1le", response.body?.string().toString())
            }
        })
    }


}