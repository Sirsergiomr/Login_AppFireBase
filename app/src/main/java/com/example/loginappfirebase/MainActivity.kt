package com.example.loginappfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.acos

enum class ProviderType{
    GOOGLE
}

class MainActivity : AppCompatActivity() {
    private val Sign_in_google = 100
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val analytics : FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Entró")
        analytics.logEvent("InitScreen", bundle)

        setup()
        session()


        imageView2.setImageResource(R.mipmap.ic_launcher)


    }
    private fun setup(){
        buttonRegistar.setOnClickListener{
            if(editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextEmail.text.toString(),
                        editTextEmail.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        showOk();
                        persistencia()
                        session()
                    }
                    else{
                        showError()
                    }
                }
            }
        }
        buttonAcceder.setOnClickListener{
            if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.text.toString(),
                        editTextEmail.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        showOk();
                        persistencia()
                        session()
                    }
                    else{
                        showError()
                    }
                }
            }
        }

        buttonSalir.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            session()
        }
    buttonGoogle.setOnClickListener{
        val googleConf: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleConf)
        googleSignInClient.signOut()
        startActivityForResult(googleSignInClient.signInIntent,Sign_in_google)
    }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Sign_in_google){
            val task : Task<GoogleSignInAccount?> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null){

                    val credential : AuthCredential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            showOk();
                            persistenciaGoogle(account.email?:"")
                            session()
                            showHome(account.email?:"",ProviderType.GOOGLE)
                        }

                    }

                }
            }catch (a : ApiException){
                showError()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        session()
    }

    private fun session(){
        val prefs : SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        if (email== null){
            buttonSalir.isEnabled = false
            buttonAcceder.isEnabled=true
            buttonRegistar.isEnabled= true
            buttonGoogle.isEnabled = true
        }else{
            buttonSalir.isEnabled = true
            buttonAcceder.isEnabled=false
            buttonRegistar.isEnabled= false
            buttonGoogle.isEnabled = false
        }
    }

    private fun persistencia(){
        val prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", editTextEmail.text.toString())
        prefs.apply()
    }
    private fun persistenciaGoogle(email: String){
        val prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }
    private fun showOk(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ok")
        builder.setMessage("Conectado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showError(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ok")
        builder.setMessage("Error al conectar")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent:Intent =  Intent(this,HomeActivity::class.java).apply{
            putExtra("email",email )
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }

}