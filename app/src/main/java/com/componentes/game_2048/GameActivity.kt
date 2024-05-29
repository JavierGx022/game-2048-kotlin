package com.componentes.game_2048

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.componentes.game_2048.Logica.DataGame
import com.componentes.game_2048.Logica.Game2048
import com.componentes.game_2048.Logica.Partida
import com.componentes.game_2048.databinding.ActivityGameBinding
import com.componentes.recyclerviewexample.AdapterRecycler.PartidasAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class GameActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityGameBinding
    private lateinit var gestureDetector: GestureDetector
    private val game2048 = Game2048()
    private val dataGame = DataGame()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gestureDetector = GestureDetector(this, this)
        var cargada= intent.getStringExtra("cargada").toString()
        val tablero: List<List<Int>>? = intent.getSerializableExtra("tablero") as? List<List<Int>>


        if(cargada.equals("partidaCargada")){
            if(tablero!=null){
                game2048.initializeGameFB(tablero)
            }else{
                game2048.initializeGame()
            }

            val scoreP = intent.getIntExtra("score", 0)
            game2048.setScore(scoreP)
        }else{
            game2048.initializeGame()
        }

        updateUI()

        binding.restartGame.setOnClickListener{
            game2048.resetGame()
            updateUI()
        }



    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (game2048.hasMovesAvailable()) {
            val deltaX = e2.x - (e1?.x ?: 0f)
            val deltaY = e2.y - (e1?.y ?: 0f)

            // Si el desplazamiento horizontal es mayor que el desplazamiento vertical
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                // Si el desplazamiento es hacia la izquierda
                if (deltaX < 0) {
                    game2048.moveLeft()
                } else { // Si el desplazamiento es hacia la derecha
                    game2048.moveRight()
                }
            } else { // Si el desplazamiento vertical es mayor que el desplazamiento horizontal
                // Si el desplazamiento es hacia arriba
                if (deltaY < 0) {
                    game2048.moveUp()
                } else { // Si el desplazamiento es hacia abajo
                    game2048.moveDown()
                }
            }

            updateUI()

        } else {
            // No hay movimientos disponibles, mostrar mensaje o realizar otra acción
            showGameOverDialog()
        }

        return true
    }


    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    private fun updateUI() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val value = game2048.getValueAt(i, j)
                val textViewId = resources.getIdentifier("textView_$i$j", "id", packageName)
                val textView = findViewById<TextView>(textViewId)
                textView.text = if (value != 0) value.toString() else ""
                // Cambiar el fondo de las casillas según el valor del número
                when (value) {
                    2 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_2)
                    4 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_4)
                    8 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_8)
                    16 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_16)
                    32 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_32)
                    64 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_64)
                    128 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_128)
                    256 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_256)
                    512 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_512)
                    1024 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_1024)
                    2048 -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.color_2048)
                    // Agregar más casos para otros valores si es necesario
                    else -> textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_cell)
                }
            }
        }

        // Actualizar el puntaje
        updateScore()

        datos()

        // Verificar si el jugador ha ganado
        if (game2048.hasWon()) {
            showGameWonDialog()
        }
    }

    fun datos(){
        val boardState = game2048.getBoardState()
        val id= intent.getStringExtra("id").toString()
        val partida = Partida(
            idPartida = id, // Puedes usar un ID único para la partida
            board = boardState,
            score = game2048.getScore() // Supongamos que tienes un método getScore en tu Game2048
        )

        dataGame.updateMatchInFirebase(partida)



    }


    private fun updateScore() {
        val score = game2048.getScore() // Reemplaza esto con el método adecuado para calcular el puntaje
        binding.scoreGame.text = "$score"
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun showGameOverDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Game Over")
            setMessage("¡Has perdido la partida!")
            setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
                // Aquí puedes agregar cualquier acción adicional que desees realizar al hacer clic en "Aceptar"
                // Por ejemplo, puedes redirigir al usuario a la actividad de inicio
                val intent = Intent(this@GameActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra la actividad actual para evitar volver atrás
            })
            setCancelable(false) // Impide que el diálogo se cierre al tocar fuera de él
        }

        // Mostrar el diálogo de alerta
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showGameWonDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Felicidades!")
            .setMessage("¡Has ganado la partida!")
            .setPositiveButton("Aceptar") { dialog, _ ->
                // Puedes agregar aquí la lógica para reiniciar el juego o ir a otra actividad
                val intent = Intent(this@GameActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            .setCancelable(false) // Impide que el diálogo se cierre al tocar fuera de él
            .show()
    }




}
