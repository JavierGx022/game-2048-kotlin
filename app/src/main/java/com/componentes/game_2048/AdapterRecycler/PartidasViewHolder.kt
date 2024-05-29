package com.componentes.recyclerviewexample.AdapterRecycler

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.componentes.game_2048.Logica.Partida
import com.componentes.game_2048.R
import com.componentes.game_2048.databinding.ItemRowBinding


class PartidasViewHolder(view:View): RecyclerView.ViewHolder(view) {
    val idPartida= view.findViewById<TextView>(R.id.tvIdPartida)
    val binding= ItemRowBinding.bind(view)

    fun render(partida:Partida, c:Int, onClickListener: (Partida)->Unit){
        val totalPartidas = c
        val posicion = totalPartidas - adapterPosition
        idPartida.text = "Partida ${posicion}"
        itemView.setOnClickListener { onClickListener(partida) }
    }
}