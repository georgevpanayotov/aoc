import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun hash(step: String): Int {
    var hash = 0
    for (ch in step) {
        hash += ch.code
        hash *= 17
        hash %= 256
    }

    return hash
}

data class Lens(val label: String, val focal: Int)

fun createBoxes(): List<MutableList<Lens>> {
    val boxes = mutableListOf<MutableList<Lens>>()
    for (i in 0..255) {
        boxes.add(mutableListOf<Lens>())
    }

    return boxes.toList()
}

fun remove(label: String, boxes: List<MutableList<Lens>>) {
    val box = boxes[hash(label)]

    var removed = false

    for (i in 0..<box.size) {
        if (box[i].label == label) {
            removed = true

        }
        if (removed) {
            if (i < box.size - 1) {
                box[i] = box[i + 1]
            }
        }
    }

    if (removed) {
        box.removeAt(box.size - 1)
    }
}

fun place(lens: Lens, boxes: List<MutableList<Lens>>) {
    val box = boxes[hash(lens.label)]

    var found = false

    for (i in 0..<box.size) {
        if (box[i].label == lens.label) {
            box[i] = lens
            found = true
            break
        }
    }

    if (!found) {
        box.add(lens)
    }
}

fun getScore(boxes: List<MutableList<Lens>>): Int {
    var score = 0
    for (i in 0..<boxes.size) {
        for (j in 0..<boxes[i].size) {
            score += (i + 1) * (j + 1) * boxes[i][j].focal
        }
    }

    return score
}

fun main() {
    val steps = Lines.next().split(",")

    val boxes = createBoxes()

    for (step in steps) {
        if (step.indexOf('-') >= 0) {
            val label = step.substring(0, step.length - 1)

            remove(label, boxes)
        } else {
            val parts = step.split("=")
            val lens = Lens(parts[0], parts[1].toInt())

            place(lens, boxes)
        }
    }

    println("Answer = ${getScore(boxes)}")
}
