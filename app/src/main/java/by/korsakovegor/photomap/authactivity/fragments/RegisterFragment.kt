package by.korsakovegor.photomap.authactivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.authactivity.viewmodels.AuthViewModel
import by.korsakovegor.photomap.databinding.FragmentRegisterLayoutBinding
import by.korsakovegor.photomap.models.SignUserDtoIn
import by.korsakovegor.photomap.utils.Utils

class RegisterFragment() : Fragment() {
    private lateinit var binding: FragmentRegisterLayoutBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.registerButton.setOnClickListener {
            if(Utils.isInternetAvailable(requireContext())) {
                val login = binding.loginEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                val repeatPassword = binding.repeatPasswordEditText.text.toString()

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
                if (repeatPassword != password) {
                    binding.repeatPasswordEditText.error = "Passwords must match"
                    isValid = false
                }

                if (isValid) {
                    binding.swipeRefreshLayout.isRefreshing = true
                    viewModel.registerUser(user)
                }
            }else
                Utils.showConnectionAlertDialog(requireContext())
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.error.visibility = View.VISIBLE
            binding.error.text = it
            binding.success.visibility = View.GONE
        }

        viewModel.user.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.error.visibility = View.GONE
            binding.success.visibility = View.VISIBLE
            binding.success.text = "Registration successful"

            binding.loginEditText.text.clear()
            binding.passwordEditText.text.clear()
            binding.repeatPasswordEditText.text.clear()
        }
    }
}