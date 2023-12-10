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
}