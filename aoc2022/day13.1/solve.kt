sealed class PacketData

class ListPacketData(val items: List<PacketData>) : PacketData() {
    override fun toString(): String { 
        var str = "["
        for (i in 0..items.size - 2) {
            str += items[i].toString()
            str += ","
        }
        if (items.size > 0) {
            str += items[items.size - 1]
        }
        str += "]"
        return str
    }
}

class CorePacketData(val item: Int) : PacketData() {
    override fun toString(): String = "$item"
}

class Cursor(var pos: Int)

fun parsePacket(line: String, cursor: Cursor): PacketData? {
    if (cursor.pos >= line.length) {
        return null
    }

    if (line[cursor.pos] == '[') {
        cursor.pos++
        val items = arrayListOf<PacketData>()
        var done = false

        while (!done) {
            if (line[cursor.pos] == ']') {
                cursor.pos++
                done = true
            } else {
                val item = parsePacket(line, cursor)
                if (item == null) {
                    error("")
                }

                items.add(item)
                if (line[cursor.pos] == ',') {
                    cursor.pos++
                } else if (line[cursor.pos] == ']') {
                    cursor.pos++
                    done = true
                }
            }
        }

        return ListPacketData(items)
    } else if (line[cursor.pos].isDigit()) {
        var digits = ""
        while (line[cursor.pos].isDigit()) {
            digits += "${line[cursor.pos]}"
            cursor.pos++
        }

        return CorePacketData(digits.toInt())
    } else {
        print("BAD: ${line[cursor.pos]}\n")
        return null
    }
}

fun main() {
    var line = readLine()
    var score = 0
    while (line != null) {
        val leftPacket = parsePacket(line, Cursor(0))
        print("$leftPacket\n")
        line = readLine()

        val rightPacket = parsePacket(line!!, Cursor(0))
        print("$rightPacket\n\n")
        readLine()

        line = readLine()
    }
    print(score)
}
