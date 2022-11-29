package com.example.tanamin.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.tanamin.Home.HomeActivity
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentLoginBinding
import com.example.tanamin.lib.Google
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Google(), View.OnClickListener {
    private lateinit var binding: FragmentLoginBinding

    //    private lateinit var mCallbackManager: CallbackManager
    private lateinit var fAuth: FirebaseAuth
    private val TAG: String = LoginFragment::class.java.getSimpleName()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        fAuth = FirebaseAuth.getInstance()

        binding.txtBelumMendaftar.setOnClickListener(this)
        binding.buttonNavDaftar.setOnClickListener(this)
        binding.buttonManualSignIn.setOnClickListener(this)
        binding.buttonGoogleSignIn.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_nav_daftar -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            R.id.txt_belum_mendaftar -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            R.id.button_manual_sign_in -> {
                manualSignIn()
            }
            R.id.button_google_sign_in -> {
                signIn()
            }
        }
    }

    private fun manualSignIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email == "" && password == "") {
            Toast.makeText(
                requireContext(),
                "Please fill Email and Password",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginFragment", "signInWithEmail:success")
                        Toast.makeText(
                            requireContext(), "Authentication succed.",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this.context, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            requireContext(), "Authentication failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}