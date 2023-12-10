package by.korsakovegor.photomap.models

import java.util.Base64
import java.util.Date

data class ImageDtoIn(
    val base64: String,
    val systemDate: Date,
    val lat: Double,
    val lng: Double
){
    val date = systemDate.time/1000
}