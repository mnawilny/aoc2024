package com.nawilny.aoc2024.day08

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day08", "input.txt")
    val antennas = input.withIndex().flatMap { line ->
        line.value.withIndex().filter { it.value != '.' }.map { it.value to Position(it.index, line.index) }
    }.fold(mutableMapOf<Char, List<Position>>()) { acc, pair ->
        acc.compute(pair.first) { _, p ->
            p?.plus(pair.second) ?: listOf(pair.second)
        }
        acc
    }
    if (antennas.any { it.value.size < 2 }) error("Unsupported case")
    val max = Position(input.first().length - 1, input.size - 1)

    antennas.flatMap { antennaFrequency ->
        val pairs = antennaFrequency.value.toPairs()
        pairs.flatMap { findAntinodes(it.first, it.second) }
    }.filter { withinLimit(it, max) }.toSet().count().println()

    antennas.flatMap { antennaFrequency ->
        val pairs = antennaFrequency.value.toPairs()
        pairs.flatMap { findAntinodesWithHarmonics(it.first, it.second, max) }
    }.toSet().count().println()
}

private data class Position(val x: Int, val y: Int) {
    fun plus(x: Int, y: Int) = Position(this.x + x, this.y + y)
    fun minus(x: Int, y: Int) = Position(this.x - x, this.y - y)
}

private fun <T> List<T>.toPairs(): List<Pair<T, T>> {
    return this.withIndex().flatMap { el1 ->
        this.subList(el1.index + 1, this.size).map { el1.value to it }
    }
}

private fun findAntinodes(p1: Position, p2: Position): List<Position> {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return listOf(p1.plus(dx, dy), p2.minus(dx, dy))
}

private fun findAntinodesWithHarmonics(p1: Position, p2: Position, max: Position): List<Position> {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    var p = p1
    val result = mutableListOf<Position>()
    while (withinLimit(p, max)) {
        result.add(p)
        p = p.plus(dx, dy)
    }
    p = p1.minus(dx, dy)
    while (withinLimit(p, max)) {
        result.add(p)
        p = p.minus(dx, dy)
    }
    return result
}

private fun withinLimit(p: Position, max: Position) = p.x >= 0 && p.y >= 0 && p.x <= max.x && p.y <= max.y
