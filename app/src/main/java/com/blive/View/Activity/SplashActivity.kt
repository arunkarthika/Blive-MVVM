package com.blive.View.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.blive.R
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val Splash_Iv: ImageView = findViewById(R.id.splash_iv)

        Glide
            .with(this)
            .load(R.drawable.splash_logo)
            .into(Splash_Iv)

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this, Testactivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 1000)
    }
}
