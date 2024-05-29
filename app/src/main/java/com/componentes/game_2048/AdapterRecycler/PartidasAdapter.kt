package com.componentes.recyclerviewexample.AdapterRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.componentes.game_2048.Logica.Partida
import com.componentes.game_2048.R


class PartidasAdapter(private val partidasList:List<Partida>, private val onClickListener: (Partida)->Unit): RecyclerView.Adapter<PartidasViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidasViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        return PartidasViewHolder(layoutInflater.inflate(R.layout.item_row, parent, false))
    }

    override fun getItemCount(): Int {
        return partidasList.size
    }

    override fun onBindViewHolder(holder: PartidasViewHolder, position: Int) {
        val item= partidasList[position]
        val c= itemCount
        holder.render(item,c, onClickListener)


    }



}