package by.korsakovegor.photomap.authactivity.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.authactivity.viewmodels.AuthViewModel
import by.korsakovegor.photomap.databinding.FragmentLoginLayoutBinding
import by.korsakovegor.photomap.mainactivity.MainActivity
import by.korsakovegor.photomap.models.SignUserDtoIn

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginLayoutBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

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

            val user = SignUserDtoIn(login, password)
            val results = user.validateUser()

            var isValid = true

            if (results[1] == 1) {
                binding.loginEditText.error = "Login must be between 4 and 32"
                isValid = false
            }
            if (results[0] == 1) {
                binding.loginEditText.error = "Login must match [a-z0-9_\\-.@]+"
                isValid = false
            }
            if (results[2] == 1) {
                binding.passwordEditText.error = "Password must be between 8 and 500"
                isValid = false
            }

            if (isValid)
                viewModel.loginUser(user)

        }

        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("user", it)
                resultLauncher.launch(intent)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.error.visibility = View.VISIBLE
            binding.error.text = it
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                viewModel.clearUser()
            }
    }
}
