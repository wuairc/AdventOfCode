package wuairc.aoc

abstract class Template(day: Int) {

    private val dayText = day.toString().padStart(2, '0')
    abstract fun part1(input: List<String>): Long

    abstract fun part2(input: List<String>): Long

    fun solve() {
        val (test1Output, test2Output) = readTestOutput()

        val calculatedTestOutput = part1(readInput("test1"))
        check(calculatedTestOutput == test1Output) { "expected: $test1Output, actual: $calculatedTestOutput" }

        val input = readInput("input")
        println("Part 1: ${part1(input)}")

        if (test2Output == null) {
            return
        }

        val part2TestOutput = part2(readInput("test2"))
        check(part2TestOutput == test2Output) { "expected: $test2Output, actual: $part2TestOutput" }

        println("Part 2: ${part2(input)}")
    }

    private fun readInput(name: String): List<String> {
        return Template::class.java.getResourceAsStream("/input/$dayText/$name.txt").use {
            it!!.bufferedReader().readLines()
        }
    }

    protected fun readTestOutputString(): List<String> = readInput("testOutput")

    /**
     * @return test1 output, test2 output, test2 output is optional
     */
    private fun readTestOutput(): Pair<Long, Long?> {
        val list = readTestOutputString()
        assert(list.isNotEmpty())
        val first = list[0].toLong()
        val second = if (list.size == 2) list[1].toLong() else null
        return first to second
    }
}