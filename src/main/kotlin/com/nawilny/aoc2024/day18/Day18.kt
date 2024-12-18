package com.nawilny.aoc2024.day18

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day18", "input.txt")
        .map { it.split(",") }
        .map { Position(it[0].toInt(), it[1].toInt()) }
//    val size = Position(7, 7)
    val size = Position(71, 71)
//    val droppedBytes = input.take(12).toSet()
    val droppedBytes = input.take(1024).toSet()

    findShortestPath(droppedBytes, size).println()

    val cutoffByte = findByteThatCutsThePathOff(input, size)
    "${cutoffByte.x},${cutoffByte.y}".println()
}

private fun findByteThatCutsThePathOff(bytes: List<Position>, size: Position): Position {
    val droppedBytes = mutableSetOf<Position>()
    bytes.forEach {
        droppedBytes.add(it)
        if (findShortestPath(droppedBytes, size) == 0) {
            return it
        }
    }
    return Position(0, 0)
}

private fun findShortestPath(droppedBytes: Set<Position>, size: Position): Int {
    val start = Position(0, 0)
    val end = Position(size.x - 1, size.y - 1)

    val visitedPositions = mutableMapOf(start to 0)
    val positionsToCheck = mutableMapOf(start to 0)
    while (positionsToCheck.isNotEmpty()) {
        val currentPosition = positionsToCheck.minBy { it.value }
        if (currentPosition.key == end) {
            return currentPosition.value
        }
        val distance = currentPosition.value + 1
        currentPosition.key.getNeighbours()
            .filter { isWithin(it, size) }
            .filter { !droppedBytes.contains(it) }
            .forEach {
                if (!visitedPositions.containsKey(it) || visitedPositions[it]!! > distance) {
                    visitedPositions[it] = distance
                    positionsToCheck[it] = distance
                }
            }
        positionsToCheck.remove(currentPosition.key)

    }
    return 0
}

private fun isWithin(p: Position, size: Position) = p.x >= 0 && p.y >= 0 && p.x < size.x && p.y < size.y

private data class Position(val x: Int, val y: Int) {
    fun getNeighbours() = setOf(
        Position(x, y - 1),
        Position(x, y + 1),
        Position(x - 1, y),
        Position(x + 1, y)
    )
}
