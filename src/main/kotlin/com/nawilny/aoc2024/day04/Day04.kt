package com.nawilny.aoc2024.day04

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

private const val SEARCHED_WORD = "XMAS"
fun main() {
    val grid = Grid(Input.readFileLinesNormalized("day04", "input.txt"))

    val startPositions = grid.lines.withIndex().flatMap { line ->
        line.value.indices.map { Position(it, line.index) }
    }
    startPositions.sumOf { p ->
        Direction.entries.count { matchesWorld(grid, p, it) }
    }.println()
    startPositions.count { matchesXWorld(grid, it) }.println()
}

private fun matchesWorld(grid: Grid, startPosition: Position, direction: Direction): Boolean {
    var position = startPosition
    SEARCHED_WORD.forEach { c ->
        if (grid.get(position) != c) {
            return false
        }
        position = direction.next(position)
    }
    return true
}

private fun matchesXWorld(grid: Grid, startPosition: Position): Boolean {
    return (grid.get(startPosition) == 'A')
            && isMSPair(grid, startPosition, Direction.LEFT_UP, Direction.RIGHT_DOWN)
            && isMSPair(grid, startPosition, Direction.LEFT_DOWN, Direction.RIGHT_UP)
}

private fun isMSPair(grid: Grid, p: Position, dir1: Direction, dir2: Direction): Boolean {
    val v1 = grid.get(dir1.next(p))
    val v2 = grid.get(dir2.next(p))
    return (v1 == 'M' && v2 == 'S') || (v1 == 'S' && v2 == 'M')
}

private data class Grid(val lines: List<String>) {
    fun get(p: Position): Char? {
        if (p.y < 0 || p.y >= lines.size) {
            return null
        }
        if (p.x < 0 || p.x >= lines[p.y].length) {
            return null
        }
        return lines[p.y][p.x]
    }
}

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    UP {
        override fun next(p: Position) = Position(p.x, p.y - 1)
    },
    RIGHT_UP {
        override fun next(p: Position) = Position(p.x + 1, p.y - 1)
    },
    RIGHT {
        override fun next(p: Position) = Position(p.x + 1, p.y)
    },
    RIGHT_DOWN {
        override fun next(p: Position) = Position(p.x + 1, p.y + 1)
    },
    DOWN {
        override fun next(p: Position) = Position(p.x, p.y + 1)
    },
    LEFT_DOWN {
        override fun next(p: Position) = Position(p.x - 1, p.y + 1)
    },
    LEFT {
        override fun next(p: Position) = Position(p.x - 1, p.y)
    },
    LEFT_UP {
        override fun next(p: Position) = Position(p.x - 1, p.y - 1)
    };

    abstract fun next(p: Position): Position
}
