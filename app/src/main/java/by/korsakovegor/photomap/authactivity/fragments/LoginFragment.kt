package by.korsakovegor.photomap.authactivity.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.authactivity.AuthViewModel
import by.korsakovegor.photomap.databinding.FragmentLoginLayoutBinding
import by.korsakovegor.photomap.models.SignUserDtoIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginLayoutBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (SignUserDtoIn.validateLogin(login))
                when (SignUserDtoIn.validatePassword(password)) {
                    0 -> CoroutineScope(Dispatchers.IO).launch {
                        viewModel.loginUser(SignUserDtoIn(login, password))
                    }
                    1 -> Log.d("D1le", "Password must match [a-z0-9_\\-.@]+")
                    2 -> Log.d("D1le", "Password size must be between 8 and 500")
                }
            else {
                binding.loginEditText.error = "Login size must be between 4 and 32"
                Log.d("D1le", "Login size must be between 4 and 32")
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            binding.error.visibility = View.VISIBLE
            binding.error.text = it
        }
    }


//    private fun sendPostRequest(url: String, jsonBody: String): String {
//    val urlObj = URL(url)
//    val connection = urlObj.openConnection() as HttpURLConnection
//
//    // Устанавливаем метод запроса на POST
//    connection.requestMethod = "POST"
//
//    // Устанавливаем заголовки
//    connection.setRequestProperty("accept", "application/json")
//    connection.setRequestProperty("Content-Type", "application/json")
//
//    // Включаем режим вывода данных в запрос
//    connection.doOutput = true
//
//    // Получаем поток вывода для записи данных в запрос
//    val outputStream = DataOutputStream(connection.outputStream)
//    outputStream.writeBytes(jsonBody)
//    outputStream.flush()
//    outputStream.close()
//
//    // Получаем код ответа от сервера
//    val responseCode = connection.responseCode
//
//    // Читаем ответ от сервера
//    val response = StringBuilder()
//    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//    var line: String?
//    while (reader.readLine().also { line = it } != null) {
//        response.append(line)
//    }
//    reader.close()
//
//    // Закрываем соединение
//    connection.disconnect()
//
//    return response.toString()
//}
}
