package com.nawilny.aoc2024.day06

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day06", "input.txt")
    val width = input.first().length
    val height = input.size
    val obstacles = mutableSetOf<Position>()
    var guardPosition = Position(-1, -1)
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach {
            if (it.value == '#') {
                obstacles.add(Position(it.index, line.index))
            } else if (it.value == '^') {
                guardPosition = Position(it.index, line.index)
            }
        }
    }
    val guard = Guard(guardPosition, Direction.UP)

    val room = Room(width = width, height = height, obstacles = obstacles, guard = guard)
    room.simulate()
    room.guardPositions.map { it.position }.toSet().count().println()

    val allPositions = (0..width).flatMap { x -> (0..height).map { Position(x, it) } }
    allPositions
        .filter { !obstacles.contains(it) }
        .map { Room(width = width, height = height, obstacles = obstacles.plus(it), guard = guard) }
        .map { it.simulate() }
        .count { it == Room.MoveResult.CYCLE }
        .println()
}

private class Room(val width: Int, val height: Int, val obstacles: Set<Position>, var guard: Guard) {

    val guardPositions = mutableSetOf(guard)
    var state = MoveResult.CONTINUE

    enum class MoveResult { CONTINUE, OUT, CYCLE }

    fun simulate(): MoveResult {
        while (state == MoveResult.CONTINUE) {
            move()
        }
        return state
    }

    private fun move() {
        val next = guard.next()
        guard = if (obstacles.contains(next)) {
            guard.rotate()
        } else {
            guard.move()
        }
        if (guard.position.x < 0 || guard.position.x >= width || guard.position.y < 0 || guard.position.y >= height) {
            state = MoveResult.OUT
        } else if (guardPositions.contains(guard)) {
            state = MoveResult.CYCLE
        } else {
            guardPositions.add(guard)
        }
    }
}

private data class Guard(val position: Position, val direction: Direction) {
    fun next() = direction.move(position)
    fun rotate() = Guard(position, direction.rotate())

    fun move() = Guard(direction.move(position), direction)
}

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    UP {
        override fun rotate() = RIGHT
        override fun move(p: Position) = Position(p.x, p.y - 1)
    },
    DOWN {
        override fun rotate() = LEFT
        override fun move(p: Position) = Position(p.x, p.y + 1)
    },
    LEFT {
        override fun rotate() = UP
        override fun move(p: Position) = Position(p.x - 1, p.y)
    },
    RIGHT {
        override fun rotate() = DOWN
        override fun move(p: Position) = Position(p.x + 1, p.y)
    };

    abstract fun rotate(): Direction
    abstract fun move(p: Position): Position
}
