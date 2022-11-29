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
import com.example.tanamin.databinding.FragmentRegisterBinding
import com.example.tanamin.lib.Google
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Google(), View.OnClickListener  {
    private lateinit var binding: FragmentRegisterBinding

    private lateinit var fAuth: FirebaseAuth

    private lateinit var fstore: FirebaseFirestore
    private lateinit var myRef: DocumentReference

    private var firebaseUserID: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        fAuth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        binding.buttonNavLogin.setOnClickListener(this)
        binding.txtSudahMendaftar.setOnClickListener(this)
        binding.buttonManualRegister.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_nav_login -> {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            R.id.txt_sudah_mendaftar -> {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            R.id.button_manual_register -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val nama = binding.etNama.text.toString()
        val password = binding.etPassword.text.toString()
        val konfirmasiPassword = binding.etKonfirmasiPassword.text.toString()

        if (email == "") {
            Toast.makeText(this.context, "Please fill out all fields", Toast.LENGTH_LONG).show()
        }else if(nama == "") {
            Toast.makeText(this.context, "Please fill out all fields", Toast.LENGTH_LONG).show()
        }else if(password == "") {
            Toast.makeText(this.context, "Please fill out all fields", Toast.LENGTH_LONG).show()
        }else {
            fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = fAuth.currentUser!!.uid
                        myRef = fstore.collection("Users").document(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["nama"] = nama
                        userHashMap["email"] = email

                        myRef.set(userHashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("RegisterFragment", "SignUp:success")
                                val intent = Intent(this.context, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                Log.w("RegisterFragment", "SignUp:failure", task.exception)
                                Toast.makeText(
                                    requireContext(), "SignUp failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Log.w("RegisterFragment", "SignUp:failure", task.exception)
                        Toast.makeText(
                            requireContext(), "SignUp failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}