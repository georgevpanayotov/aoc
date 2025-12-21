import java.util.Scanner
import net.panayotov.util.BoundedShape
import net.panayotov.util.Direction
import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.findMinMaxPoint
import net.panayotov.util.times

data class Instruction(val direction: Direction, val length: Int) {
    fun totalVector() = length.toLong() * direction.vector

    override fun toString() = "$direction $length"
}

fun parseDirection(dirChr: Char) =
    when (dirChr) {
        '3' -> Direction.NORTH
        '0' -> Direction.EAST
        '1' -> Direction.SOUTH
        '2' -> Direction.WEST
        else -> error("Unknown direction $dirChr")
    }

fun hexChar(hexChr: Char) =
    when (hexChr.lowercaseChar()) {
        'a' -> 10
        'b' -> 11
        'c' -> 12
        'd' -> 13
        'e' -> 14
        'f' -> 15
        else ->
            if (hexChr >= '0' && hexChr <= '9') {
                (hexChr - '0').toInt()
            } else {
                error("Unknown hex digit $hexChr")
            }
    }

fun parseHex(hexStr: String): Int {
    var place = 1
    var hex = 0

    for (i in hexStr.length - 1 downTo 0) {
        hex += hexChar(hexStr[i]) * place
        place *= 16
    }

    return hex
}

fun enumerateBoundary(instructions: List<Instruction>): List<Point> {
    var point = Point(0L, 0L)
    val points = mutableListOf<Point>()

    for (instruction in instructions) {
        point = point + instruction.totalVector()
        points.add(point)
    }

    return points.toList()
}

fun parseP1(line: Scanner): Instruction {
    val dirStr = line.next()
    val direction =
        when (dirStr) {
            "U" -> Direction.NORTH
            "R" -> Direction.EAST
            "D" -> Direction.SOUTH
            "L" -> Direction.WEST
            else -> error("Unknown direction $dirStr")
        }
    val length = line.next().toInt()
    val colorLine = line.next()
    val color = colorLine.substring(2, colorLine.length - 1)

    return Instruction(direction, length)
}

fun parseP2(line: Scanner): Instruction {
    line.next()
    line.next()
    val colorLine = line.next()
    val color = colorLine.substring(2, colorLine.length - 1)
    val direction = parseDirection(color[color.length - 1])
    val length = parseHex(color.substring(0, color.length - 1))
    return Instruction(direction, length)
}

fun main() {
    val instructions = mutableListOf<Instruction>()

    for (line in Scanners) {
        instructions.add(parseP2(line))
    }

    val boundary = enumerateBoundary(instructions)
    val (min, max) = findMinMaxPoint(boundary)!!

    val score = BoundedShape(boundary).area()
    println("Answer = $score")
}
