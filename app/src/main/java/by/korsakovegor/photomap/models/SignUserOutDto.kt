package by.korsakovegor.photomap.models

data class SignUserOutDto(
    private val userId: Int,
    private val login: String,
    private val token: String
) {

}