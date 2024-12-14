package com.nawilny.aoc2024.day14

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day14", "input.txt")
//    val width = 11
    val width = 101
//    val height = 7
    val height = 103
    val robots = input.map { parseRobot(it) }

    var r = robots
    (1..100).forEach { _ ->
        r = simulateRobotsMove(r, width, height)
    }
    val q1 = r.filter { it.position.x < width / 2 && it.position.y < height / 2 }
    val q2 = r.filter { it.position.x > width / 2 && it.position.y < height / 2 }
    val q3 = r.filter { it.position.x < width / 2 && it.position.y > height / 2 }
    val q4 = r.filter { it.position.x > width / 2 && it.position.y > height / 2 }
    val result = q1.count() * q2.count() * q3.count() * q4.count()
    result.println()

    var r2 = robots
    (1..10000).forEach { i ->
        r2 = simulateRobotsMove(r2, width, height)
        if (canBeChristmasTree(r2)) {
            println("----------------- $i -----------------")
            printFloor(r2, width, height)
        }
    }
}

private data class Position(val x: Int, val y: Int)

private data class Robot(val position: Position, val v: Position) {

    fun move(width: Int, height: Int): Robot {
        var newX = position.x + v.x
        if (newX < 0) {
            newX += width
        }
        if (newX >= width) {
            newX -= width
        }
        var newY = position.y + v.y
        if (newY < 0) {
            newY += height
        }
        if (newY >= height) {
            newY -= height
        }
        return Robot(Position(newX, newY), v)
    }
}

private fun parseRobot(line: String): Robot {
    val lineParts = line.split(" ")
    val p1 = lineParts[0].split(",")
    val p2 = lineParts[1].split(",")
    val posx = p1[0].substring(2).toInt()
    val posy = p1[1].toInt()
    val vx = p2[0].substring(2).toInt()
    val vy = p2[1].toInt()
    return Robot(Position(posx, posy), Position(vx, vy))
}

private fun simulateRobotsMove(robots: List<Robot>, width: Int, height: Int): List<Robot> {
    return robots.map { it.move(width, height) }
}

private fun printFloor(robots: List<Robot>, width: Int, height: Int) {
    val s = robots.map { it.position }.toSet()
    (0..<height).forEach { y ->
        (0..<width).forEach { x ->
            if (s.contains(Position(x, y))) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
}

private fun canBeChristmasTree(robots: List<Robot>): Boolean {
    val s = robots.map { it.position }.toSet()
    return s.any {
        s.contains(Position(it.x + 1, it.y))
                && s.contains(Position(it.x + 2, it.y))
                && s.contains(Position(it.x + 3, it.y))
                && s.contains(Position(it.x + 4, it.y))
                && s.contains(Position(it.x + 5, it.y))
                && s.contains(Position(it.x + 6, it.y))
    }
}



