package wuairc.aoc.util

abstract class Template(day: Int) {

    private val dayText = day.toString().padStart(2, '0')
    abstract fun part1(input: List<String>): Int

    abstract fun part2(input: List<String>): Int

    /**
     * @return part1 test answer and part 2 test answer if available
     */
    abstract fun expectedTestOutput(): Pair<Int, Int?>

    fun solve() {
        val testInput1 = readInput("${dayText}_test1")
        val testOutput = expectedTestOutput()
        check(part1(testInput1) == testOutput.first)

        val input = readInput(dayText)
        println("Part 1: ${part1(input)}")

        if (testOutput.second == null) {
            return
        }

        val testInput2 = readInput("${dayText}_test2")
        check(part2(testInput2) == testOutput.second)

        println("Part 2: ${part2(input)}")
    }
}