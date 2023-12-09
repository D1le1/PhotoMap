package by.korsakovegor.photomap.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CommentDtoOut(
    val id: Int,
    val date: Long,
    val text: String
) {
    val time: String = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(Date(date * 1000))
}