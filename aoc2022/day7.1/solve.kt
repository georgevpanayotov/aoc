sealed interface Node {
    fun size(): Int
}

class Dir(val name: String) : Node {
    val nodes = mutableListOf<Node>()
    var size: Int = 0

    override fun size(): Int {
        var total = 0
        for (node in nodes) {
            total += node.size()
        }
        return total
    }

    fun addNode(node: Node) {
        nodes.add(node)
    }
}

class File(val name: String, val size: Int) : Node {
    override fun size(): Int {
        return size
    }
}

const val SIZE_LIMIT = 100000

fun main() {
    var line = readLine()
    val stack = kotlin.collections.ArrayDeque<Dir>()
    var root: Dir? = null

    while (line != null) {
        val scanner = java.util.Scanner(line)

        val firstToken = scanner.next()
        if (firstToken == "$") {
            val command = scanner.next()
            if (command == "cd") {
                val path = scanner.next()
                if (path == "..") {
                    stack.removeLast()
                } else {
                    val newDir = Dir(path)
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
            stack.last().addNode(File(scanner.next(), firstToken.toInt()))
        }

        line = readLine()
    }

    val queue = kotlin.collections.ArrayDeque<Dir>()
    queue.addLast(root!!)

    val matchingDirs = mutableSetOf<Dir>()

    while (!queue.isEmpty()) {
        val dir = queue.removeFirst()
        val size = dir.size()
        if (size <= SIZE_LIMIT) {
            matchingDirs.add(dir)
        }

        for (subDir in dir.nodes) {
            if (subDir is Dir) {
                queue.add(subDir)
            }
        }
    }

    var score = 0
    for (dir in matchingDirs) {
        score += dir.size()
    }

    print("${score}\n")
}
