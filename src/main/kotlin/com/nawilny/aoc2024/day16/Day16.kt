package com.nawilny.aoc2024.day16

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day16", "input.txt")
    val maze = parseMaze(input)
    val paths = findShortestPath(maze)

    val endElements = Direction.values().map { ReindeerPosition(maze.end, it) }.filter { paths.containsKey(it) }
    endElements.map { paths[it] }.first()!!.score.println()

    // Assuming endElements is size of 1
    getAllPathPositions(endElements.first(), paths).map { it.p }.toSet().size.println()
}

private fun getAllPathPositions(
    end: ReindeerPosition,
    paths: Map<ReindeerPosition, PathElement>
): Set<ReindeerPosition> {
    val element = paths[end]!!
    return if (element.howToGetThere.isEmpty()) {
        setOf(end)
    } else if (element.howToGetThere.size == 1) {
        getAllPathPositions(element.howToGetThere.first(), paths).plus(end)
    } else {
        element.howToGetThere.fold(setOf<ReindeerPosition>()) { acc, position ->
            acc.plus(getAllPathPositions(position, paths))
        }.plus(end)
    }
}

private fun findShortestPath(maze: Maze): Map<ReindeerPosition, PathElement> {
    val visitedPositions = mutableMapOf<ReindeerPosition, PathElement>()
    visitedPositions[maze.start] = PathElement(0, setOf())
    val positionsToCheck = mutableMapOf<ReindeerPosition, Int>()
    positionsToCheck[maze.start] = 0

    var minScore = Int.MAX_VALUE

    while (positionsToCheck.isNotEmpty()) {
        val currentPosition = positionsToCheck.minByOrNull { it.value }!!.key
        val pathElement = visitedPositions[currentPosition]!!
        if (currentPosition.p == maze.end) {
            minScore = pathElement.score
        }
        if (pathElement.score < minScore) {
            currentPosition.nextPositions().forEach {
                if (!maze.walls.contains(it.key.p)) {
                    val newScore = pathElement.score + it.value
                    val existingElement = visitedPositions[it.key]
                    if (existingElement == null) {
                        visitedPositions[it.key] = PathElement(newScore, setOf(currentPosition))
                        positionsToCheck[it.key] = newScore
                    } else if (existingElement.score > newScore) {
                        visitedPositions[it.key] = PathElement(newScore, setOf(currentPosition))
                    } else if (existingElement.score == newScore) {
                        visitedPositions[it.key] =
                            PathElement(newScore, existingElement.howToGetThere.plus(currentPosition))
                    }
                }
            }
        }
        positionsToCheck.remove(currentPosition)
    }

    return visitedPositions
}

private data class PathElement(val score: Int, val howToGetThere: Set<ReindeerPosition>)

private data class ReindeerPosition(val p: Position, val d: Direction) {
    fun nextPositions() = mapOf(
        ReindeerPosition(d.straight(p), d) to 1,
        ReindeerPosition(d.turnLeft().straight(p), d.turnLeft()) to 1001,
        ReindeerPosition(d.turnRight().straight(p), d.turnRight()) to 1001
    )
}

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    NORTH {
        override fun straight(p: Position) = Position(p.x, p.y - 1)
        override fun turnLeft() = WEST
        override fun turnRight() = EAST
    },
    SOUTH {
        override fun straight(p: Position) = Position(p.x, p.y + 1)
        override fun turnLeft() = EAST
        override fun turnRight() = WEST
    },
    WEST {
        override fun straight(p: Position) = Position(p.x - 1, p.y)
        override fun turnLeft() = SOUTH
        override fun turnRight() = NORTH
    },
    EAST {
        override fun straight(p: Position) = Position(p.x + 1, p.y)
        override fun turnLeft() = NORTH
        override fun turnRight() = SOUTH
    };

    abstract fun straight(p: Position): Position
    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
}

private data class Maze(val start: ReindeerPosition, val end: Position, val walls: Set<Position>)

private fun parseMaze(input: List<String>): Maze {
    var start: ReindeerPosition? = null
    var end: Position? = null
    val walls = mutableSetOf<Position>()
    input.withIndex().forEach { line ->
        line.value.withIndex().forEach {
            val p = Position(it.index, line.index)
            when (it.value) {
                '#' -> walls.add(p)
                'S' -> start = ReindeerPosition(p, Direction.EAST)
                'E' -> end = p
            }
        }
    }
    return Maze(start!!, end!!, walls)
}