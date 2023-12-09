package by.korsakovegor.photomap.utils

import android.util.Log
import by.korsakovegor.photomap.models.ImageDtoOut
import by.korsakovegor.photomap.models.SignUserDtoIn
import by.korsakovegor.photomap.models.SignUserOutDto
import org.json.JSONObject

class JsonParser {
    companion object {
        fun jsonToUser(jsonResponse: String): SignUserOutDto? {
            val jsonObject = JSONObject(jsonResponse)
            Log.d("D1le", jsonResponse)
            val status = jsonObject.getInt("status")
            if (status == 200) {
                val dataObject = jsonObject.getJSONObject("data")
                val userId = dataObject.getInt("userId")
                val login = dataObject.getString("login")
                val token = dataObject.getString("token")

                return SignUserOutDto(userId, login, token)
            }

            return null
        }

        fun userToJson(user: SignUserDtoIn): String {
            val jsonObject = JSONObject()
            jsonObject.put("login", user.login)
            jsonObject.put("password", user.password)

            return jsonObject.toString()
        }

        fun jsonToImageList(jsonResponse: String): ArrayList<ImageDtoOut>? {
            val jsonObject = JSONObject(jsonResponse)
            val status = jsonObject.getInt("status")
            if (status == 200) {
                val imagesList = ArrayList<ImageDtoOut>()
                val jsonArray = jsonObject.getJSONArray("data")
                for (i in 0 until jsonArray.length()) {
                    val arrayJsonObject = jsonArray.getJSONObject(i)
                    val id = arrayJsonObject.getInt("id")
                    val url = arrayJsonObject.getString("url")
                    val date = arrayJsonObject.getLong("date")
                    val lat = arrayJsonObject.getDouble("lat")
                    val lng = arrayJsonObject.getDouble("lng")
                    imagesList.add(ImageDtoOut(id, url, date, lat, lng))
                }
                return imagesList
            }
            return null
        }
    }
}