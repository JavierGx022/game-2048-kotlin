package com.componentes.game_2048.Logica

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class DataGame{
    private val auth = FirebaseAuth.getInstance()
    private val firebaseDatabase = Firebase.database

    fun saveMatch(partida:Partida, id:String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val personalInformationRef = firebaseDatabase.getReference("$currentUserId/Partidas/partida$id")
        personalInformationRef.setValue(partida)
    }

    fun updateMatchInFirebase(partida: Partida) {
        val currentUserId = auth.currentUser?.uid ?: return

        // Obtén el ID de la partida
        val partidaId = partida.idPartida

        // Verifica que el ID de la partida no esté vacío
        if (partidaId.isEmpty()) {
            return
        }

        // Referencia a la partida específica en la base de datos
        val partidaRef = firebaseDatabase.getReference("$currentUserId/Partidas/partida$partidaId")

        // Actualiza los datos de la partida en la base de datos
        partidaRef.setValue(partida)
            .addOnSuccessListener {
                Log.e("TAG", "Datos de la partida actualizados exitosamente en la base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error al actualizar los datos de la partida en la base de datos: $e")
            }
    }


    fun getAllMatchesForUser(onSuccess: (List<Partida>) -> Unit, onFailure: (String) -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return

        // Referencia a la colección de partidas del usuario en la base de datos
        val userMatchesRef = firebaseDatabase.getReference("$currentUserId/Partidas")

        // Escuchar los cambios en la colección de partidas del usuario
        userMatchesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val partidasList = mutableListOf<Partida>()

                // Iterar sobre cada partida y agregarla a la lista
                for (partidaSnapshot in dataSnapshot.children) {
                    val partida = partidaSnapshot.getValue(Partida::class.java)
                    partida?.let {
                        partidasList.add(it)
                    }
                }

                // Llamar a la función onSuccess con la lista de partidas
                onSuccess(partidasList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Llamar a la función onFailure con el mensaje de error
                onFailure(databaseError.message)
            }
        })
    }

}