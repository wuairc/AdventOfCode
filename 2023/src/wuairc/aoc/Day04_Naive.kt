package wuairc.aoc

fun main() {
    Day04_Naive().solve()
}

class Day04_Naive : Template(4) {
    override fun part1(input: List<String>): Int {
        val cardList = parseInput(input)
        return cardList.sumOf { card -> card.getPoints() }
    }

    override fun part2(input: List<String>): Int {
        val cardList = parseInput(input)
        val cardNumbers = cardList.map(Card::getWinningNumberCount).toIntArray()
        cardNumbers.forEachIndexed { index, number ->
            val remaining = (cardNumbers.size - index - 1).coerceAtMost(number)
            repeat(remaining) {
                cardNumbers[remaining + index + 1] += number
            }
        }
        return cardNumbers.sum()
    }

    private fun parseInput(input: List<String>): List<Card> {
        val spaceRegex = Regex("""\s+""")
        return input.map { line ->
            val (cardLine, numberLine) = line.split(':', limit = 2)
            val id = cardLine.trim().split(' ', limit = 2)[1].trim()
            val (winningLine, haveLine) = numberLine.trim().split('|', limit = 2)
            val winning = winningLine.trim().split(spaceRegex).toSet()
            val have = haveLine.trim().split(spaceRegex).toSet()
            Card(id, winning, have)
        }
    }

    data class Card(val id: String, val winningNumbers: Set<String>, val have: Set<String>) {
        fun getWinningNumberCount(): Int {
            return (winningNumbers intersect have).size
        }

        fun getPoints(): Int {
            val size = getWinningNumberCount()
            return if (size == 0) 0 else 1 shl (size - 1)
        }
    }
}