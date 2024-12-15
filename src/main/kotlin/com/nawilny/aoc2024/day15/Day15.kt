package com.nawilny.aoc2024.day15

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day15", "input.txt")
    val warehouseInput = input.filter { it.startsWith('#') }
    val warehouse1 = Warehouse(warehouseInput, false)
    val warehouse2 = Warehouse(warehouseInput, true)
    val directions = parseDirections(input.filter { !it.startsWith('#') })

    directions.forEach { warehouse1.moveRobot(it) }
    warehouse1.printWarehouse()
    warehouse1.getBoxesCoordinates().println()

    directions.forEach { warehouse2.moveRobot(it) }
    warehouse2.printWarehouse()
    warehouse2.getBoxesCoordinates().println()
}

private data class Position(val x: Int, val y: Int)

private enum class Direction {
    UP {
        override fun move(p: Position) = Position(p.x, p.y - 1)
    },
    RIGHT {
        override fun move(p: Position) = Position(p.x + 1, p.y)
    },
    DOWN {
        override fun move(p: Position) = Position(p.x, p.y + 1)
    },
    LEFT {
        override fun move(p: Position) = Position(p.x - 1, p.y)
    };

    abstract fun move(robot: Position): Position
}

private data class Box(val p1: Position, val p2: Position)

private class Warehouse(input: List<String>, val scale: Boolean) {
    val walls: Set<Position>
    val boxes: MutableMap<Position, Box>
    var robot: Position

    init {
        val walls = mutableSetOf<Position>()
        boxes = mutableMapOf()
        var robot: Position? = null
        input.withIndex().forEach { line ->
            line.value.withIndex().forEach {
                if (scale) {
                    val p1 = Position(it.index * 2, line.index)
                    val p2 = Position((it.index * 2) + 1, line.index)
                    when (it.value) {
                        '#' -> {
                            walls.add(p1)
                            walls.add(p2)
                        }

                        'O' -> {
                            val box = Box(p1, p2)
                            boxes[p1] = box
                            boxes[p2] = box
                        }

                        '@' -> {
                            robot = p1
                        }
                    }
                } else {
                    val p = Position(it.index, line.index)
                    when (it.value) {
                        '#' -> walls.add(p)
                        'O' -> boxes[p] = Box(p, p)
                        '@' -> robot = p
                    }
                }
            }
        }
        this.walls = walls
        this.robot = robot!!
    }

    fun moveRobot(d: Direction) {
        val newRobotPosition = d.move(robot)
        if (walls.contains(newRobotPosition)) {
            return
        }
        if (boxes.contains(newRobotPosition)) {
            val result = canMoveBox(newRobotPosition, d)
            if (!result) {
                return
            }
            moveBox(newRobotPosition, d)
        }
        robot = newRobotPosition
    }

    private fun canMoveBox(boxPosition: Position, d: Direction): Boolean {
        val box = boxes[boxPosition]!!
        if (scale) {
            val nextPosition1 = d.move(box.p1)
            val nextPosition2 = d.move(box.p2)
            if (walls.contains(nextPosition1) || walls.contains(nextPosition2)) {
                return false
            }
            if (boxes.contains(nextPosition1) && boxes[nextPosition1] != box) {
                val result = canMoveBox(nextPosition1, d)
                if (!result) {
                    return false
                }
            }
            if (boxes.contains(nextPosition2) && boxes[nextPosition2] != box) {
                val result = canMoveBox(nextPosition2, d)
                if (!result) {
                    return false
                }
            }
        } else {
            val nextPosition = d.move(box.p1)
            if (walls.contains(nextPosition)) {
                return false
            }
            if (boxes.contains(nextPosition)) {
                val result = canMoveBox(nextPosition, d)
                if (!result) {
                    return false
                }
            }
        }
        return true
    }

    private fun moveBox(boxPosition: Position, d: Direction) {
        val box = boxes[boxPosition]!!
        if (scale) {
            val nextPosition1 = d.move(box.p1)
            val nextPosition2 = d.move(box.p2)
            if (boxes.contains(nextPosition1) && boxes[nextPosition1] != box) {
                moveBox(nextPosition1, d)
            }
            if (boxes.contains(nextPosition2) && boxes[nextPosition2] != box) {
                moveBox(nextPosition2, d)
            }
            val newBox = Box(nextPosition1, nextPosition2)
            boxes.remove(box.p1)
            boxes.remove(box.p2)
            boxes[nextPosition1] = newBox
            boxes[nextPosition2] = newBox
        } else {
            val nextPosition = d.move(box.p1)
            if (boxes.contains(nextPosition)) {
                moveBox(nextPosition, d)
            }
            val newBox = Box(nextPosition, nextPosition)
            boxes.remove(box.p1)
            boxes[nextPosition] = newBox
        }
    }

    fun getBoxesCoordinates() = boxes.values.toSet().sumOf { it.p1.x + (it.p1.y * 100) }

    fun printWarehouse() {
        val with = walls.maxOf { it.x }
        val height = walls.maxOf { it.y }
        (0..height).forEach { y ->
            (0..with).forEach {
                val p = Position(it, y)
                val c = when {
                    walls.contains(p) -> '#'
                    boxes.contains(p) -> {
                        val b = boxes[p]!!
                        if (scale) {
                            if (p == b.p1) '[' else ']'
                        } else {
                            'O'
                        }
                    }

                    robot == p -> '@'
                    else -> '.'
                }
                print(c)
            }
            print('\n')
        }
    }

}

private fun parseDirections(lines: List<String>): List<Direction> {
    return lines.fold("") { acc, s ->
        acc + s
    }.map {
        when (it) {
            '>' -> Direction.RIGHT
            '<' -> Direction.LEFT
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            else -> error("Invalid direction")
        }
    }
}