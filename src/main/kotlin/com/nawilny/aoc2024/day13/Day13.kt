package com.nawilny.aoc2024.day13

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day13", "input.txt")
    val machines = parseInput(input)
    machines.mapNotNull { it.solve() }.sum().println()
    machines.map { it.movePrice(10000000000000) }.mapNotNull { it.solve() }.sum().println()
}

private data class Position(val x: Long, val y: Long)

private data class ClawMachine(val a: Position, val b: Position, val price: Position) {

    fun solve(): Long? {
        val bP1 = (price.y * a.x) - (price.x * a.y)
        val bP2 = (b.y * a.x) - (b.x * a.y)
        if (bP1 % bP2 != 0L) {
            return null
        }
        val bTimes = bP1 / bP2
        val aP1 = price.x - (bTimes * b.x)
        if (aP1 % a.x != 0L) {
            return null
        }
        val aTimes = aP1 / a.x
        return aTimes * 3 + bTimes
    }

    fun movePrice(dPrice: Long) = ClawMachine(a, b, Position(price.x + dPrice, price.y + dPrice))
}

private fun parseInput(input: List<String>): List<ClawMachine> {
    var a = Position(0, 0)
    var b = Position(0, 0)
    val list = mutableListOf<ClawMachine>()
    input.withIndex().forEach {
        when (it.index % 3) {
            0 -> a = parseButtonInput(it.value)
            1 -> b = parseButtonInput(it.value)
            else -> list.add(ClawMachine(a, b, parsePrizeInput(it.value)))
        }
    }
    return list
}

private fun parseButtonInput(s: String): Position {
    val parts = s.split(", Y+")
    return Position(parts[0].substring("Button A: X+".length).toLong(), parts[1].toLong())
}

private fun parsePrizeInput(s: String): Position {
    val parts = s.split(", Y=")
    return Position(parts[0].substring("Prize: X=".length).toLong(), parts[1].toLong())
}

