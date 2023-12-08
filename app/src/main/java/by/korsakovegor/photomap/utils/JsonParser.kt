package by.korsakovegor.photomap.utils

import android.util.Log
import by.korsakovegor.photomap.models.SignUserDtoIn
import by.korsakovegor.photomap.models.SignUserOutDto
import okhttp3.Response
import org.json.JSONObject

class JsonParser {
    companion object {
        fun jsonToUser(jsonResponse: String): SignUserOutDto? {
            val jsonObject = JSONObject(jsonResponse)
            Log.d("D1le", jsonResponse)
            val status = jsonObject.getInt("status")
            if(status == 200){
                val dataObject = jsonObject.getJSONObject("data")
                val userId = dataObject.getInt("userId")
                val login = dataObject.getString("login")
                val token = dataObject.getString("token")

                return SignUserOutDto(userId, login, token)
            }

            return null
        }

        fun userToJson(user: SignUserDtoIn): String{
            val jsonObject = JSONObject()
            jsonObject.put("login", user.login)
            jsonObject.put("password", user.password)

            return jsonObject.toString()
        }
    }
}