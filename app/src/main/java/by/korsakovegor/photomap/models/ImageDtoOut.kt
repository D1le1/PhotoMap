package by.korsakovegor.photomap.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImageDtoOut(
    val id: Int, val url: String, val date: Long, val lat: Double, val lng: Double
) {
    val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        .format(Date(date * 1000))

}