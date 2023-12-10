package by.korsakovegor.photomap.models

import java.io.Serializable

data class SignUserOutDto(
    val userId: Int,
    val login: String,
    val token: String
) : Serializable