package com.nawilny.aoc2024.day01

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println
import kotlin.math.abs

fun main() {
    val list = Input.readFileLinesNormalized("day01", "input.txt")
        .map { it.split("   ") }
    val left = list.map {it[0].toInt()}.sorted()
    val right = list.map {it[1].toInt()}.sorted()

    // part 1
    left.withIndex().sumOf { abs(it.value - right[it.index]) }.println()

    // part 2
    val rightCounter = right.groupingBy { it }.eachCount()
    left.sumOf { it * rightCounter.getOrDefault(it, 0) }.println()
}