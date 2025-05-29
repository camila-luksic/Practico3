package com.example.practico3.models

enum class TetrisShape(val coordinates: Array<Pair<Int, Int>>) {
    I(arrayOf(0 to 0, 0 to 1, 0 to 2, 0 to 3)),
    L(arrayOf(0 to 0, 1 to 0, 2 to 0, 2 to 1)),
    J(arrayOf(0 to 1, 1 to 1, 2 to 1, 2 to 0)),
    T(arrayOf(1 to 0, 0 to 1, 1 to 1, 2 to 1)),
    S(arrayOf(1 to 0, 2 to 0, 0 to 1, 1 to 1)),
    Z(arrayOf(0 to 0, 1 to 0, 1 to 1, 2 to 1)),
    O(arrayOf(0 to 0, 1 to 0, 0 to 1, 1 to 1))
}