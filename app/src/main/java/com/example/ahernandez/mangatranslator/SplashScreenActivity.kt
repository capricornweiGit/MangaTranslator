package com.example.ahernandez.mangatranslator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide both status & navigation bars before setting view
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        setContentView(R.layout.activity_splash_screen)

        // Set and start animation
        val logoAnim = AnimationUtils.loadAnimation(baseContext, R.anim.logo_animation)
        var logoImg: ImageView = findViewById(R.id.imageViewLogo)

        logoImg.startAnimation(logoAnim)

        // Overwrite listener to close and send user to
        // main application after animation finishes
        logoAnim.setAnimationListener(object: Animation.AnimationListener {

            override fun onAnimationEnd(animation: Animation?) {

                // Close activity to backwards navigation
                finish()
                // Navigate to Main
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            } // END onAnimationEnd()


            // Unimplemented
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationRepeat(p0: Animation?) { }

        }) // END setAnimationListener()


    } // END onCreate()


} // END SplashScreenActivity class