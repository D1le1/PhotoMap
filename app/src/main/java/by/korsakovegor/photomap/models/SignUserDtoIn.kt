package by.korsakovegor.photomap.models

import java.util.regex.Pattern
import kotlin.math.log

data class SignUserDtoIn(
    val login: String,
    val password: String
) {
    companion object {

        fun validateLogin(login: String): Int {
            if (!Pattern.matches("[a-z0-9_\\-.@]+", login))
                return 1
            if (login.length !in 4..32)
                return 2
            return 0
        }

        fun validatePassword(password: String): Boolean {
            if (password.length in 8..500)
                return true
            return false
        }
    }

    fun validateUser(): Array<Int>{
        val results = arrayOf(0, 0, 0)
        if (!Pattern.matches("[a-z0-9_\\-.@]+", login))
            results[0] = 1
        if (login.length !in 4..32)
            results[1] = 1
        if (password.length !in 8..500)
            results[2] = 1
        return results
    }
}

