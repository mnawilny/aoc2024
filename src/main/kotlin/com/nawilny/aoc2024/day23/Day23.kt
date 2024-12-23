package com.nawilny.aoc2024.day23

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val connections = Input.readFileLinesNormalized("day23", "input.txt")
        .map { it.split("-") }.map { setOf(it[0], it[1]) }.toSet()

    val computers = connections.flatten().toSet().toList()

    findISetsOfThree(computers, connections).filter { s -> s.any { it.startsWith("t") } }.size.println()
    findInterconnectedSets(computers, connections).maxBy { it.size }.sorted().joinToString(",").println()
}

private fun findISetsOfThree(computers: List<String>, connections: Set<Set<String>>): Set<Set<String>> {
    val result = mutableSetOf<Set<String>>()
    (computers.indices).forEach { i1 ->
        val c1 = computers[i1]
        (i1 + 1..<computers.size).forEach { i2 ->
            val c2 = computers[i2]
            if (connections.contains(setOf(c1, c2))) {
                (i2 + 1..<computers.size).forEach { i3 ->
                    val c3 = computers[i3]
                    if (connections.contains(setOf(c1, c3)) && connections.contains(setOf(c2, c3))) {
                        result.add(setOf(c1, c2, c3))
                    }
                }
            }
        }
    }
    return result
}

private fun findInterconnectedSets(computers: List<String>, connections: Set<Set<String>>): List<Set<String>> {
    return (computers.indices).flatMap { i1 ->
        findInterconnectedSets(setOf(computers[i1]), i1, computers, connections)
    }
}

private fun findInterconnectedSets(
    s: Set<String>,
    index: Int,
    computers: List<String>,
    connections: Set<Set<String>>
): Set<Set<String>> {
    val result = mutableSetOf<Set<String>>()
    (index + 1..<computers.size).forEach { i ->
        val c = computers[i]
        if (s.all { connections.contains(setOf(it, c)) }) {
            result.addAll(findInterconnectedSets(s.plus(c), i, computers, connections))
        }
    }
    if (result.isEmpty()) {
        result.add(s)
    }
    return result
}

