import net.panayotov.util.Lines

const val modulus = 16777216L

fun mix(lhs: Long, rhs: Long) = lhs xor rhs

fun prune(value: Long) = value % modulus

fun nextSecret(secret: Long): Long {
    var curr = secret
    curr = prune(mix(curr, curr * 64L))
    curr = prune(mix(curr, curr / 32L))
    curr = prune(mix(curr, curr * 2048L))

    return curr
}

fun solve(secret: Long): Long {
    var curr = secret

    for (i in 1..2000) {
        curr = nextSecret(curr)
    }

    return curr
}

fun main() {
    val secrets = mutableListOf<Long>()
    for (line in Lines) {
        secrets.add(line.toLong())
    }

    val scores = secrets.map(::solve)
    val score = scores.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
