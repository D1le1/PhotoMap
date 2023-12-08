package by.korsakovegor.photomap.models

import java.util.regex.Pattern
import kotlin.math.log

data class SignUserDtoIn(
    val login: String,
    val password: String
) {
    companion object{

        fun validateLogin(login: String): Boolean{
            if(login.length in 4..32)
                return true
            return false
        }

        fun validatePassword(password: String): Int{
            if (!Pattern.matches("[a-z0-9_\\-.@]+", password))
                return 1
            if (password.length !in 8..500)
                return 2
            return 0
        }
    }
}

