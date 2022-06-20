package com.tallercmovil.mshop1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tallercmovil.mshop1.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //Para entrar a Google
    lateinit var gso: GoogleSignInOptions
    lateinit var signInClient: GoogleSignInClient

    //Para el servicio de autenticación con Firebase
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Ponemos lo que vamos a requerir de su cuenta Google.
        //En esta ocasión solamente su correo
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("293229955338-m32pkfen4osp023sfqp6dk5qu1d18d7u.apps.googleusercontent.com")
            .requestEmail()
            .build()

        signInClient = GoogleSignIn.getClient(this, gso)


        //Para verificar si ya ha accedido previamente
        val signInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        //Si la variable no está en nulo, quiere decir que ya había ingresado
        //También podemos checar si en firebaseAuth está loggeado.

        if(signInAccount!=null || firebaseAuth.currentUser!=null){
            Toast.makeText(this, "Usuario ya registrado", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //para procesar el sign In Intent
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                //En un task buscamos obtener los datos proceso de signin
                val signInTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try{
                    //Establecemos un elemento GoogleSignInAccount para obtener finalmente los datos

                    val signInAccount = signInTask.getResult(ApiException::class.java)

                    //Agregamos los datos de ingreso a FirebaseAuth
                    //Paso 1. Obtenemos la credencial de la cuenta de Google para usarla en Firebase

                    val authCredential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)

                    //Paso 2. Usamos la credencial para loggearnos en FirebaseAuth
                    firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener {
                        Toast.makeText(this, "Tu cuenta Google ha sido asociada a la aplicación", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "No se pudo completar el registro", Toast.LENGTH_SHORT).show()
                    }

                }catch(e: ApiException){
                    //Manejo de error en el try
                }

            }
        }

        binding.btnEntrarGoogle.setOnClickListener {
            val signIntent = signInClient.signInIntent
            resultLauncher.launch(signIntent)
        }


    }
}