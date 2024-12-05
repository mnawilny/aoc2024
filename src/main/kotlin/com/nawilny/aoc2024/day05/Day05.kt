package com.nawilny.aoc2024.day05

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLines("day05", "input.txt")
    val newLineIndex = input.withIndex().first { it.value.isEmpty() }.index
    val rules = input.slice(0..<newLineIndex)
        .map { line -> line.split("|").map { it.toInt() } }
        .map { it[0] to it[1] }
    val updatePages = input.slice(newLineIndex + 1..<(input.size))
        .map { line -> line.split(",").map { it.toInt() } }

    updatePages.filter { passesRules(it, rules) }
        .sumOf { it[it.size / 2] }.println()
    updatePages.filter { !passesRules(it, rules) }
        .map { orderCorrectly(it, rules) }
        .sumOf { it[it.size / 2] }.println()
}


private fun passesRules(pages: List<Int>, rules: List<Pair<Int, Int>>): Boolean {
    rules.forEach {
        val i1 = pages.indexOf(it.first)
        val i2 = pages.indexOf(it.second)
        if (i1 >= 0 && i2 >= 0 && i1 > i2) {
            return false
        }
    }
    return true
}

private fun orderCorrectly(pages: List<Int>, rules: List<Pair<Int, Int>>): List<Int> {
    val remainingPages = pages.toMutableSet()
    val result = mutableListOf<Int>()
    while (remainingPages.isNotEmpty()) {
        val next = remainingPages.filter { page ->
            rules.none { it.second == page && remainingPages.contains(it.first) }
        }
        if (next.size != 1) {
            error("Ups...")
        }
        result.add(next.first())
        remainingPages.remove(next.first())
    }
    return result
}
