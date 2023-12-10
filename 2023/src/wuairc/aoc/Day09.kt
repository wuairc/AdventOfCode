package wuairc.aoc

fun main() {
    Day09().solve()
}

class Day09 : Template(9) {
    override fun part1(input: List<String>): Long {
        val historyList = parseInput(input)
        return historyList.sumOf { getNextNumber(it) }
    }

    override fun part2(input: List<String>): Long {
        val historyList = parseInput(input)
        return historyList.sumOf { getPreviousNumber(it) }
    }

    private fun getNextNumber(history: History): Long {
        var list = history.numbers
        val diff = mutableListOf<Long>()
        while (!isAllZeros(list)) {
            diff.add(list.last())
            list = list.windowed(2, 1).map { it[1] - it[0] }
        }
        return diff.sum()
    }

    private fun getPreviousNumber(history: History): Long {
        var list = history.numbers
        val diff = mutableListOf<Long>()
        while (!isAllZeros(list)) {
            diff.add(list.first())
            list = list.windowed(2, 1).map { it[1] - it[0] }
        }
        diff.add(0)
        return diff.reduceRight { value, acc -> value - acc }
    }

    private fun isAllZeros(sequence: List<Long>): Boolean {
        return sequence.all { it == 0L }
    }
}

private fun parseInput(input: List<String>): List<History> {
    return input.map { line ->
        History(line.split(' ').map { it.trim().toLong() })
    }
}

data class History(val numbers: List<Long>)
