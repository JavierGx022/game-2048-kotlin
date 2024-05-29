package com.componentes.game_2048.Logica

import android.util.Log
import kotlin.random.Random



class Game2048 {
    private val grid = Array(4) { IntArray(4) }
    private var score = 0
    private val dataGame= DataGame()

    fun getValueAt(row: Int, col: Int): Int {
        return grid[row][col]
    }

    fun setValueAt(row: Int, col: Int, value: Int) {
        grid[row][col] = value
    }

    fun initializeGame() {
        repeat(2) {
            addNewNumber()
        }
    }

    fun initializeGameFB(tablero: List<List<Int>>?) {
        if (tablero == null) {
            // Llamar a initializeGame si el tablero es nulo
            initializeGame()
            return
        }

        // Verificar que el tablero tenga el tamaño correcto
        if (tablero.size != grid.size || tablero.any { it.size != grid[0].size }) {
            throw IllegalArgumentException("El tamaño del tablero es incorrecto")
        }

        // Configurar el tablero con los datos proporcionados
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                grid[i][j] = tablero[i][j]
            }
        }
    }

    fun addNewNumber() {
        val rand = Random.Default
        val emptyCells = countEmptyCells()
        if (emptyCells == 0) {
            return
        }
        val index = rand.nextInt(emptyCells)
        var count = 0
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] == 0) {
                    if (count == index) {
                        grid[i][j] = if (rand.nextBoolean()) 2 else 2
                        
                        return // Salir del método después de agregar un nuevo número
                    }
                    count++
                }
            }
        }
    }

    fun getScore(): Int {
        return score
    }

    fun setScore(s:Int){
        score=s
    }



    fun resetGame() {
        // Reiniciar todas las celdas del tablero a cero
        for (row in grid) {
            for (i in row.indices) {
                row[i] = 0
            }
        }
        score=0

        // Después de reiniciar las celdas, agrega dos nuevos números al azar
        addNewNumber()
        addNewNumber()
    }


    fun countEmptyCells(): Int {
        var count = 0
        for (row in grid) {
            for (cell in row) {
                if (cell == 0) {
                    count++
                }
            }
        }
        return count
    }

    fun moveLeft() {
        var moved = false // Variable para verificar si se realizó algún movimiento en la cuadrícula

        for (row in grid) {
            var current = 0
            for (j in 1 until grid.size) {
                if (row[j] != 0) {
                    if (row[current] == 0) {
                        row[current] = row[j]
                        row[j] = 0
                        moved = true // Se realizó un movimiento
                    } else if (row[current] == row[j]) {
                        row[current] *= 2
                        score += row[current]
                        row[j] = 0
                        // Implementa la lógica para la puntuación aquí si es necesario
                        moved = true // Se realizó un movimiento
                    } else {
                        current++
                        if (current != j) {
                            row[current] = row[j]
                            row[j] = 0
                            moved = true // Se realizó un movimiento
                        }
                    }
                }
            }
        }

        // Después de completar todos los movimientos posibles, agregar un nuevo número si se realizó algún movimiento
        if (moved) {
            addNewNumber()

        }
    }


    fun moveRight() {
        var moved = false

        for (row in grid) {
            var current = grid.size - 1
            for (j in grid.size - 2 downTo 0) {
                if (row[j] != 0) {
                    if (row[current] == 0) {
                        row[current] = row[j]
                        row[j] = 0
                        moved = true
                    } else if (row[current] == row[j]) {
                        row[current] *= 2
                        score += row[current]
                        row[j] = 0

                        moved = true
                    } else {
                        current--
                        if (current != j) {
                            row[current] = row[j]
                            row[j] = 0
                            moved = true
                        }
                    }
                }
            }
        }

        if (moved) {
            addNewNumber()
        }
    }

    fun moveUp() {
        var moved = false

        for (col in 0 until grid.size) {
            var current = 0
            for (i in 1 until grid.size) {
                if (grid[i][col] != 0) {
                    if (grid[current][col] == 0) {
                        grid[current][col] = grid[i][col]
                        grid[i][col] = 0
                        moved = true
                    } else if (grid[current][col] == grid[i][col]) {
                        grid[current][col] *= 2
                        score += grid[current][col]
                        grid[i][col] = 0
                        moved = true
                    } else {
                        current++
                        if (current != i) {
                            grid[current][col] = grid[i][col]
                            grid[i][col] = 0
                            moved = true
                        }
                    }
                }
            }
        }

        if (moved) {
            addNewNumber()
        }
    }

    fun moveDown() {
        var moved = false

        for (col in 0 until grid.size) {
            var current = grid.size - 1
            for (i in grid.size - 2 downTo 0) {
                if (grid[i][col] != 0) {
                    if (grid[current][col] == 0) {
                        grid[current][col] = grid[i][col]
                        grid[i][col] = 0
                        moved = true
                    } else if (grid[current][col] == grid[i][col]) {
                        grid[current][col] *= 2
                        score += grid[current][col]
                        grid[i][col] = 0
                        moved = true
                    } else {
                        current--
                        if (current != i) {
                            grid[current][col] = grid[i][col]
                            grid[i][col] = 0
                            moved = true
                        }
                    }
                }
            }
        }

        if (moved) {
            addNewNumber()
        }
    }


    fun hasMovesAvailable(): Boolean {
        // Verificar si hay al menos una celda vacía en el tablero
        if (countEmptyCells() > 0) {
            return true
        }

        // Verificar si hay dos celdas adyacentes con el mismo valor en filas o columnas contiguas
        for (i in 0 until 4) {
            for (j in 0 until 3) {
                // Verificar movimientos horizontales
                if (grid[i][j] == grid[i][j + 1]) {
                    return true
                }
                // Verificar movimientos verticales
                if (grid[j][i] == grid[j + 1][i]) {
                    return true
                }
            }
        }

        // Si no se encontraron movimientos posibles, retorna falso
        return false
    }

    fun hasWon(): Boolean {
        for (row in grid) {
            for (value in row) {
                if (value == 2048) {
                    return true
                }
            }
        }
        return false
    }

    fun getBoardState(): List<List<Int>> {
        val boardState = mutableListOf<List<Int>>()
        for (row in grid) {
            boardState.add(row.toList()) // Convertir cada fila del tablero en una lista y agregarla a la lista de estado del tablero
        }
        return boardState
    }





}

