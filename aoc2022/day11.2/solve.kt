import java.util.Scanner
import java.math.BigInteger

const val RELIEF_FACTOR = 1
const val ROUNDS = 10000

class Monkey(
    val items: MutableList<BigInteger>,
    val operation: (BigInteger) -> BigInteger,
    val divisible: Int,
    val trueMonkey: Int,
    val falseMonkey: Int
) : Comparable<Monkey> {
    var inspected: BigInteger = BigInteger.ZERO
    val bigDivisible = BigInteger.valueOf(divisible.toLong())

    override fun compareTo(other: Monkey): Int {
        return inspected.compareTo(other.inspected)
    }

    override fun toString(): String = "items($items), operation($operation), divisible($divisible), ifTrue($trueMonkey), ifFalse($falseMonkey)"
}

class MonkeyBuilder {
    // This would be simpler to just say `arrayListOf<>()` here but I just wanted to figure out the
    // pattern for lateinit.
    private lateinit var items: MutableList<BigInteger>
    private var operation: ((BigInteger) -> BigInteger)? = null
    private var divisible: Int? = null
    private var trueMonkey: Int? = null
    private var falseMonkey: Int? = null

    fun addItem(item: Int) {
        safeItems().add(BigInteger.valueOf(item.toLong()))
    }

    fun setOperation(operation: (BigInteger) -> BigInteger) {
        this.operation = operation
    }

    fun setTrueMonkey(monkey: Int) {
        trueMonkey = monkey
    }

    fun setDivisible(div: Int) {
        divisible = div
    }

    fun setFalseMonkey(monkey: Int) {
        falseMonkey = monkey
    }

    fun done(): Boolean = operation != null &&
        divisible != null &&
        trueMonkey != null &&
        falseMonkey != null

    fun started(): Boolean = operation != null ||
        divisible != null ||
        trueMonkey != null ||
        falseMonkey != null || !safeItems().isEmpty()

    fun build(): Monkey {
        if (!done()) {
            error("Not all fields set.")
        }

        return Monkey(
            safeItems(),
            operation!!,
            divisible!!,
            trueMonkey!!,
            falseMonkey!!
        )
    }

    private fun safeItems(): MutableList<BigInteger> {
        if (!this::items.isInitialized) {
            items = arrayListOf<BigInteger>()
        }

        return items
    }
}

fun oldOrNum(operand: String, old: BigInteger): BigInteger {
    if (operand == "old") {
        return old
    } else {
        return BigInteger(operand)
    }
}

fun parseMonkey(lines: Iterator<Scanner>): Monkey? {
    val builder = MonkeyBuilder()

    if (!lines.hasNext()) {
        // No more lines? We're done!
        return null
    }

    var line = lines.next()
    if (!line.hasNext()) {
        // Skip optional blank line at the start.
        line = lines.next()
    }

    if (!line.hasNext() || !(line.next() == "Monkey")) {
        error("No monkey")
    }

    if (!line.hasNext()) {
        error("No monkey number")
    }
    line.next()

    if (!lines.hasNext()) {
        error("No starting items line")
    }
    line = lines.next()

    line.next() // Starting
    line.next() // items:

    while (line.hasNext()) {
        var itemStr = line.next()

        if (itemStr[itemStr.length - 1] == ',') {
            itemStr = itemStr.substring(0, itemStr.length - 1)
        }

        builder.addItem(itemStr.toInt())
    }

    if (!lines.hasNext()) {
        error("No Operation line")
    }

    line = lines.next()
    line.next() // Operation:
    line.next() // new
    line.next() // =

    val lhs = line.next()
    val op = line.next()

    val rhs = line.next()

    builder.setOperation { old ->
        val lhsResolved = oldOrNum(lhs, old)
        val rhsResolved = oldOrNum(rhs, old)
        when (op) {
            "+" -> lhsResolved + rhsResolved
            "*" -> lhsResolved * rhsResolved
            else -> error("no op")
        }
    }

    if (!lines.hasNext()) {
        error("No test line")
    }

    line = lines.next()

    line.next() // Test:
    line.next() // divisible
    line.next() // by

    builder.setDivisible(line.nextInt())

    if (!lines.hasNext()) {
        error("No true line")
    }

    line = lines.next()
    for (i in 1..5) {
        // If true: throw to monkey
        line.next()
    }
    builder.setTrueMonkey(line.nextInt())

    if (!lines.hasNext()) {
        error("No false line")
    }

    line = lines.next()
    for (i in 1..5) {
        // If false: throw to monkey
        line.next()
    }
    builder.setFalseMonkey(line.nextInt())

    return if (builder.done()) {
        builder.build()
    } else {
        null
    }
}

object Lines : Iterator<Scanner> {
    private var line: Scanner? = null
    override fun next(): Scanner {
        ensureLine()
        val ret = line ?: error("No next line")
        line = null

        return ret
    }

    override fun hasNext(): Boolean {
        ensureLine()
        return line != null
    }

    private fun ensureLine() {
        if (line == null) {
            line = readLine()?.let(::Scanner)
        }
    }
}

fun lcm(nums: MutableList<BigInteger>): BigInteger {
    // We just happen to know that the input is all primes so we don't need to get fancy with LCM
    // computation.
    var lcm = BigInteger.ONE

    for (num in nums) {
        lcm *= num
    }
    return lcm
}

fun doRound(monkeys: List<Monkey>, modulus: BigInteger) {
    for (monkey in monkeys) {
        val it = monkey.items.listIterator()
        while (it.hasNext()) {
            var worry = it.next()
            it.remove()

            // Use a modulus to keep the `worry` values from getting so big we end up with OOM.
            // Modulus must be chosen as LCM of the `divisible` values so that the divisibility
            // doesn't change.
            worry = monkey.operation(worry) % modulus
            val tossTo = if (worry % monkey.bigDivisible == BigInteger.ZERO) {
                monkey.trueMonkey
            } else {
                monkey.falseMonkey
            }
            monkey.inspected++
            monkeys[tossTo].items.add(worry)
        }
    }
}

fun main() {
    val monkeys = arrayListOf<Monkey>()

    var monkey = parseMonkey(Lines)

    while (monkey != null) {
        monkeys.add(monkey)
        print("Monkey ${monkeys.size - 1} $monkey\n")

        monkey = parseMonkey(Lines)
    }

    val modulus = lcm(ArrayList(monkeys.map { m -> m.bigDivisible }))

    val roundsToCheck = setOf<Int>(1 ,20 ,1000 ,2000 ,3000 ,4000 ,5000 ,6000 ,7000 ,8000 ,9000 ,10000)
    for (i in 1..ROUNDS) {
        doRound(monkeys, modulus)
        if (roundsToCheck.contains(i)) {
            print("After round $i\n")
            for (j in 0..monkeys.size - 1) {
                print ("Monkey $j inspected items ${monkeys[j].inspected}\n")
            }

        }
    }

    monkeys.sort()
    val x: BigInteger = BigInteger.valueOf(monkeys[monkeys.size - 1].inspected.toLong())
    val y: BigInteger = BigInteger.valueOf(monkeys[monkeys.size - 2].inspected.toLong())

    print("$x * $y = ${x * y}\n")
}
