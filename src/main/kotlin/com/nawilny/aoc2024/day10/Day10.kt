package com.nawilny.aoc2024.day10

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day10", "input.txt")
        .map { line -> line.map { it.digitToInt() } }
    val map = Map(input)
    getStartPositions(map).sumOf { getTrailheadPaths(it, map).distinct().size }.println()
    getStartPositions(map).sumOf { getTrailheadPaths(it, map).size }.println()
}

private fun getTrailheadPaths(startPosition: Position, map: Map): List<Position> {
    val startHeight = map.getHeight(startPosition)
    if (startHeight == 9) {
        return listOf(startPosition)
    }
    return startPosition.getNeighbours()
        .filter { map.contains(it) }
        .filter { map.getHeight(it) == startHeight + 1 }
        .flatMap { getTrailheadPaths(it, map) }
}

private fun getStartPositions(map: Map): List<Position> {
    return map.heights.withIndex().flatMap { line ->
        line.value.withIndex().filter { it.value == 0 }.map { Position(it.index, line.index) }
    }
}

private data class Position(val x: Int, val y: Int) {
    fun getNeighbours() = listOf(
        Position(x - 1, y), Position(x + 1, y), Position(x, y - 1), Position(x, y + 1)
    )

}

private data class Map(val heights: List<List<Int>>) {
    fun getHeight(p: Position) = heights[p.y][p.x]
    fun contains(p: Position) = p.x >= 0 && p.y >= 0 && p.x < heights.first().size && p.y < heights.size
}
