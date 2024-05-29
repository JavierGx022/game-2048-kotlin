package com.componentes.game_2048.Logica

data class Partida (
      val idPartida:String="",
      val board: List<List<Int>>,
      val score:Int=0
){
      // Constructor sin argumentos requerido por Firebase
      constructor() : this("", emptyList(), 0)
}