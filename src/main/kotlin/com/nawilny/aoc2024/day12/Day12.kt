package com.nawilny.aoc2024.day12

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println


fun main() {
    val input = Input.readFileLinesNormalized("day12", "input.txt")
    val regions = getRegions(input)
    regions.sumOf { getPerimeter(it.second) * it.second.size }.println()
    regions.sumOf { getSides(it.second) * it.second.size }.println()
}

private fun getRegions(input: List<String>): Set<Pair<Char, Set<Position>>> {
    val regions = mutableSetOf<Pair<Char, Set<Position>>>()
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach { c ->
            val p = Position(c.index, line.index)
            if (regions.none { it.second.contains(p) }) {
                regions.add(c.value to getRegion(p, input, setOf(p)).plus(p))
            }
        }
    }
    return regions
}

private fun getRegion(position: Position, input: List<String>, region: Set<Position>): Set<Position> {
    val newPositions = position.getNeighbours().filter { !region.contains(it) }
        .filter { isWithin(it, input) }
        .filter { input[position.y][position.x] == input[it.y][it.x] }.toSet()
    var total = region.plus(newPositions)
    return newPositions.fold(newPositions) { set, p ->
        val new = getRegion(p, input, total)
        total = total.plus(new)
        set.plus(new)
    }
}

private fun getPerimeter(region: Set<Position>) = getEdges(region).size

private fun getSides(region: Set<Position>): Int {
    val edges = getEdges(region)
    val combinedEdges = getEdges(region).toMutableSet()
    edges.forEach { edge ->
        if (combinedEdges.contains(edge)) {
            if (edge.orientation == Edge.Orientation.H_FROM_BOTTOM || edge.orientation == Edge.Orientation.H_FROM_TOP) {
                combineSidesInDirection(edge, combinedEdges) { it.left() }
                combineSidesInDirection(edge, combinedEdges) { it.right() }
            } else {
                combineSidesInDirection(edge, combinedEdges) { it.up() }
                combineSidesInDirection(edge, combinedEdges) { it.down() }
            }
        }
    }
    return combinedEdges.size
}

private fun combineSidesInDirection(edge: Edge, combinedEdges: MutableSet<Edge>, operation: (Position) -> Position) {
    var e = Edge(operation(edge.p), edge.orientation)
    while (combinedEdges.contains(e)) {
        combinedEdges.remove(e)
        e = Edge(operation(e.p), edge.orientation)
    }
}

private fun getEdges(region: Set<Position>): Set<Edge> {
    return region.fold(setOf()) { edges, p ->
        val newEdges = mutableSetOf<Edge>()
        if (!region.contains(p.up())) {
            newEdges.add(Edge(p, Edge.Orientation.H_FROM_BOTTOM))
        }
        if (!region.contains(p.down())) {
            newEdges.add(Edge(p.down(), Edge.Orientation.H_FROM_TOP))
        }
        if (!region.contains(p.left())) {
            newEdges.add(Edge(p, Edge.Orientation.V_FROM_RIGHT))
        }
        if (!region.contains(p.right())) {
            newEdges.add(Edge(p.right(), Edge.Orientation.V_FROM_LEFT))
        }
        edges.plus(newEdges)
    }
}


private fun isWithin(p: Position, input: List<String>): Boolean {
    return p.x >= 0 && p.y >= 0 && p.x < input.first().length && p.y < input.size
}

private data class Edge(val p: Position, val orientation: Orientation) {
    enum class Orientation { H_FROM_BOTTOM, H_FROM_TOP, V_FROM_LEFT, V_FROM_RIGHT }
}

private data class Position(val x: Int, val y: Int) {
    fun getNeighbours() = listOf(up(), right(), down(), left())

    fun up() = Position(x, y - 1)
    fun down() = Position(x, y + 1)
    fun left() = Position(x - 1, y)
    fun right() = Position(x + 1, y)
}
