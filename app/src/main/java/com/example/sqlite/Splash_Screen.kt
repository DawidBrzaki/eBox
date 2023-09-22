package com.example.sqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.sqlite.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        supportActionBar?.hide()

        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1800)

        val animation = AnimationUtils.loadAnimation(this,R.anim.rotation_animation)

        val imageView =
            findViewById<ImageView>(R.id.logo)// Replace with your ImageView reference

        val numRotations = 5 // Number of times you want to rotate

        for (i in 1..numRotations) {
            imageView.startAnimation(animation)
            // Wait for the animation to finish before starting the next rotation

        }
    }
}