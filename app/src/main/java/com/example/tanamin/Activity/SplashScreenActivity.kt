package com.example.tanamin.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.tanamin.Authentication.AuthenticationActivity
import com.example.tanamin.Home.HomeActivity
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var fAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fAuth = FirebaseAuth.getInstance()
        val user = fAuth.currentUser

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            if(user != null) {
                val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(this@SplashScreenActivity, AuthenticationActivity::class.java)
                startActivity(intent)
            }
//            startActivity(Intent(applicationContext, StoryActivity::class.java))
        }, 3000)
    }
}