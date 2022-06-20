package com.tallercmovil.mshop1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.tallercmovil.mshop1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Para firebaseAuth

    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instanciando firebaseAuth

        firebaseAuth = FirebaseAuth.getInstance()

        //Obteniendo los datos del usuario loggeado

        binding.tvUsuario.text = firebaseAuth.currentUser?.displayName
        binding.tvMail.text = firebaseAuth.currentUser?.email

        //Botón para cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            //cerramos sesión en firebase
            firebaseAuth.signOut()

            //Desasociamos la app con la cuenta Google
            GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnSuccessListener {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this, "No se pudo cerrar sesión en Google", Toast.LENGTH_SHORT).show()
                }


        }


    }
}