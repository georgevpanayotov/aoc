sealed interface Node {
    fun size(): Int
    fun depth(): Int
    fun name(): String
}

class Dir(val name: String, val depth: Int) : Node {
    val nodes = mutableListOf<Node>()
    var size: Int = 0

    override fun size(): Int {
        var total = 0
        for (node in nodes) {
            total += node.size()
        }
        return total
    }

    override fun depth(): Int {
        return depth
    }
    override fun name(): String = name

    fun addNode(node: Node) {
        nodes.add(node)
    }
}

class File(val name: String, val size: Int, val depth: Int) : Node {
    override fun size(): Int {
        return size
    }
    override fun depth(): Int {
        return depth
    }
    override fun name(): String = name
}

const val SIZE_LIMIT = 100000
const val TOTAL_SIZE_AVAIL = 70000000
const val UNUSED_NEEDED = 30000000

fun main() {
    var line = readLine()
    val stack = kotlin.collections.ArrayDeque<Dir>()
    var root: Dir? = null
    var depth = 0

    while (line != null) {
        val scanner = java.util.Scanner(line)

        val firstToken = scanner.next()
        if (firstToken == "$") {
            val command = scanner.next()
            if (command == "cd") {
                val path = scanner.next()
                if (path == "..") {
                    stack.removeLast()
                    depth--
                } else {
                    val newDir = Dir(path, depth)
                    depth++
                    if (!stack.isEmpty()) {
                        stack.last().addNode(newDir)
                    }
                    if (path == "/") {
                        root = newDir
                    }
                    stack.addLast(newDir)
                }
            } else if (command == "ls") {
            }
        } else if (firstToken.toIntOrNull() != null) {
            stack.last().addNode(File(scanner.next(), firstToken.toInt(), depth))
        }

        line = readLine()
    }

    val queue = kotlin.collections.ArrayDeque<Node>()
    queue.addLast(root!!)

    while (!queue.isEmpty()) {
        val node = queue.removeLast()
        for (i in 1..node.depth()) {
            print("  ")
        }

        val isDir = node is Dir
        val trailing = if (isDir && node.name() != "/") "/" else ""
        print("${node.name()}$trailing - ${node.size()}\n")

        if (isDir) {
            val dir = node as Dir

            for (subDir in dir.nodes.asReversed()) {
                queue.add(subDir)
            }
        }
    }
}
