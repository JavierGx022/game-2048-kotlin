package com.componentes.game_2048

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.componentes.game_2048.Logica.DataGame
import com.componentes.game_2048.Logica.Game2048
import com.componentes.game_2048.Logica.Partida
import com.componentes.game_2048.databinding.ActivityMainBinding
import com.componentes.recyclerviewexample.AdapterRecycler.PartidasAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.Serializable

import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val dataGame = DataGame()
    private val game2048 = Game2048()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showListMatchs()

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        initRecycler()

        val boardState = game2048.getBoardState()


       binding.btnStart.setOnClickListener {
           val partidaId = UUID.randomUUID().toString()
           if((intent.getStringExtra("tPerfil")).equals("invitado")){
               var intent= Intent(this,GameActivity::class.java)
               startActivity(intent)
           }else{
               val partida = Partida(
                   idPartida = partidaId,
                   board = boardState,
                   score = game2048.getScore()
               )
               dataGame.saveMatch(partida, partidaId)
               var intent= Intent(this,GameActivity::class.java)
               intent.putExtra("id", partidaId)
               startActivity(intent)
           }
       }
        binding.btnSalir.setOnClickListener {
            signOutAndStartSignInActivity()
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showListMatchs(){
        var tPerfil= intent.getStringExtra("tPerfil")
        if(tPerfil.equals("invitado")){
            binding.containerMatchs.visibility= View.GONE
        }
    }

    private fun initRecycler(){
        binding.rvPartidas.layoutManager= LinearLayoutManager(this)
        dataGame.getAllMatchesForUser(
            onSuccess = { partidasList ->
                binding.rvPartidas.adapter= PartidasAdapter(partidasList){p->
                    onItemSelected(
                        p
                    )
                }
            },
            onFailure = { errorMessage ->

            }
        )
    }

    private fun onItemSelected(partida:Partida){
        val intent= Intent(this, GameActivity::class.java)
        intent.putExtra("score", partida.score)
        intent.putExtra("id", partida.idPartida)
        intent.putExtra("cargada", "partidaCargada")
        intent.putExtra("tablero", partida.board as Serializable)
        startActivity(intent)
    }
}