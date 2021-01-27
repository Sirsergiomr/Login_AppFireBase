package com.example.loginappfirebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle: Bundle? = intent.extras
        val email : String? = bundle?.getString("email")
        val provider: String ? = bundle?.getString("provider")

        setup(email?:"",provider?:"")
    }

    private fun setup(email:String , provider: String){
        textViewEmail.text = email
        textViewProvider.text = provider
    }
}