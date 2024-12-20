package com.nawilny.aoc2024.day20

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println
import kotlin.math.abs

fun main() {
    val input = Input.readFileLinesNormalized("day20", "input.txt")
    val racetrack = parseInput(input)
    val path = racetrack.buildPath()
    findShortcuts(path, 2).count { it.save >= 100 }.println()
    findShortcuts(path, 20).count { it.save >= 100 }.println()
}

private fun findShortcuts(path: List<Position>, cheatTime: Int): List<Shortcut> {
    val pathDistances = path.withIndex().associate { it.value to it.index }
    return path.flatMap { position ->
        path.filter { position.distanceTo(it) <= cheatTime }
            .filter { pathDistances.containsKey(it) }
            .map {
                val dFrom = pathDistances[position]!!
                val dTo = pathDistances[it]!!
                val dist = position.distanceTo(it)
                Shortcut(position, it, dTo - dFrom - dist)
            }
            .filter { it.save > 0 }
    }
}

private data class Shortcut(val from: Position, val to: Position, val save: Int)

private data class Position(val x: Int, val y: Int) {

    fun getNeighbours() = listOf(
        Position(x - 1, y),
        Position(x + 1, y),
        Position(x, y - 1),
        Position(x, y + 1),
    )

    fun distanceTo(p: Position) = abs(p.x - x) + abs(p.y - y)
}

private data class Racetrack(val walls: Set<Position>, val start: Position, val end: Position) {

    fun buildPath() = buildPath(listOf(start))

    private fun buildPath(pathSoFar: List<Position>): List<Position> {
        val p = pathSoFar.last()
        if (p == end) {
            return pathSoFar
        }
        val prev = if (pathSoFar.size > 1) pathSoFar[pathSoFar.size - 2] else null
        val next = p.getNeighbours().first { !walls.contains(it) && it != prev }
        return buildPath(pathSoFar.plus(next))
    }

}

private fun parseInput(input: List<String>): Racetrack {
    val walls = mutableSetOf<Position>()
    var start: Position? = null
    var end: Position? = null
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach {
            val p = Position(it.index, line.index)
            when (it.value) {
                '#' -> walls.add(p)
                'S' -> start = p
                'E' -> end = p
            }
        }
    }
    return Racetrack(walls, start!!, end!!)
}