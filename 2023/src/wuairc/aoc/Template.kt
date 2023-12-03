package wuairc.aoc

abstract class Template(day: Int) {

    private val dayText = day.toString().padStart(2, '0')
    abstract fun part1(input: List<String>): Int

    abstract fun part2(input: List<String>): Int

    fun solve() {
        val (test1Output, test2Output) = readTestOutput()

        check(part1(readInput("test1")) == test1Output)

        val input = readInput("input")
        println("Part 1: ${part1(input)}")

        if (test2Output == null) {
            return
        }

        check(part2(readInput("test2")) == test2Output)

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
    private fun readTestOutput(): Pair<Int, Int?> {
        val list = readTestOutputString()
        assert(list.isNotEmpty())
        val first = list[0].toInt()
        val second = if (list.size == 2) list[1].toInt() else null
        return first to second
    }
}