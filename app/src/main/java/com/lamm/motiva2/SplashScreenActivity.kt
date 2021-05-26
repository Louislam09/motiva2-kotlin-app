package com.lamm.motiva2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.alpha

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        var iconImage = this.findViewById<ImageView>(R.id.iv_note)
        iconImage.alpha = 0f

        var title = this.findViewById<TextView>(R.id.title)
        title.alpha = 0f

        title.animate().setDuration(3000).alpha(1f).withEndAction {
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

        iconImage.animate().setDuration(1500).alpha(1f).withEndAction {
//            val i = Intent(this,MainActivity::class.java)
//            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
//            finish()
        }


    }
}