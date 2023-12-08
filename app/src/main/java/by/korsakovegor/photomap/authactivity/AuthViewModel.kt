package by.korsakovegor.photomap.authactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.korsakovegor.photomap.models.SignUserDtoIn
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthViewModel : ViewModel() {
    private val _user = MutableLiveData<SignUserOutDto>()
    val user: LiveData<SignUserOutDto> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loginUser(user: SignUserDtoIn) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = sendPostRequest(
                "https://junior.balinasoft.com/api/account/signin",
                "{\"login\": \"${user.login}\", \"password\": \"${user.password}\"}"
            )
            val user = JsonParser.jsonToUser(response)
            if(user == null)
                _error.postValue("Invalid user, check your login and password")
            else
                _user.postValue(user)
        }
    }

    private fun sendPostRequest(url: String, jsonBody: String): String {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request =
            Request.Builder().url(url).post(requestBody).addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json").build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}