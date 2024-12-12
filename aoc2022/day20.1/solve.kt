import net.panayotov.util.Lines

class Node(val number: Int, val next: Node?)

fun main() {
    val numbers = Lines.asSequence().map(String::toInt).toList()

    var head: Node? = null
    val nodes = mutableListOf<Node>()
    var zeroNode: Node? = null

    for (i in numbers.size - 1 downTo 0) {
        head = Node(numbers[i], head)
        nodes.add(0, head)
        if (head.number == 0) {
            zeroNode = head
        }
    }

    if (zeroNode == null) {
        error("Must have a zero node.")
    }

    var curr: Node? = head
    while (curr != null) {
        val index = nodes.indexOf(curr)

        nodes.removeAt(index)

        val newIndex = ((index + curr.number) % nodes.size + nodes.size) % nodes.size
        nodes.add(newIndex, curr)

        curr = curr.next
    }

    val zeroIndex = nodes.indexOf(zeroNode)
    val score =
        listOf(1000, 2000, 3000)
            .map {
                val index = (zeroIndex + it) % nodes.size
                nodes[index].number
            }
            .reduce { acc, value -> acc + value }

    println(score)
}
