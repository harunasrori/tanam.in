package com.example.tanamin.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.databinding.ActivityAuthenticationBinding
import com.example.tanamin.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}