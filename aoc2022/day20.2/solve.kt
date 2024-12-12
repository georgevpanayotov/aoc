import net.panayotov.util.Lines

const val DECRYPTION_KEY = 811589153L

class Node(val number: Long, val next: Node?)

fun mix(head: Node, nodes: MutableList<Node>) {
    var curr: Node? = head

    while (curr != null) {
        val index = nodes.indexOf(curr)

        nodes.removeAt(index)

        val size = nodes.size.toLong()
        val newIndex = ((index + curr.number) % size + size) % size

        nodes.add(newIndex.toInt(), curr)

        curr = curr.next
    }
}

fun getScore(zeroNode: Node, nodes: MutableList<Node>): Long {
    val zeroIndex = nodes.indexOf(zeroNode)

    return listOf(1000, 2000, 3000)
        .map {
            val index = (zeroIndex + it) % nodes.size
            nodes[index].number
        }
        .reduce { acc, value -> acc + value }
}

fun main() {
    val numbers = Lines.asSequence().map { it.toLong() * DECRYPTION_KEY }.toList()

    var head: Node? = null
    val nodes = mutableListOf<Node>()
    var zeroNode: Node? = null

    for (i in numbers.size - 1 downTo 0) {
        head = Node(numbers[i], head)
        nodes.add(0, head)
        if (head.number == 0L) {
            zeroNode = head
        }
    }

    if (zeroNode == null) {
        error("Must have a zero node.")
    }

    if (head == null) {
        error("Must be at least 1 node.")
    }

    for (i in 1..10) {
        mix(head, nodes)
    }

    val score = getScore(zeroNode, nodes)

    println("Answer = $score")
}
