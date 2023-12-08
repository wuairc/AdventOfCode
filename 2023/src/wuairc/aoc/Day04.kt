package wuairc.aoc

fun main() {
    Day04().solve()
}

class Day04 : Template(4) {
    override fun part1(input: List<String>): Long {
        val cardList = parseInput(input)
        return cardList.sumOf { card -> card.getPoints().toLong() }
    }

    override fun part2(input: List<String>): Long {
        val cardList = parseInput(input)
        val state = State()
        var totalCardNumber = 0L
        cardList.map(Card::getWinningNumberCount).forEachIndexed { index, winningCount ->
            val cardNumber = state.getNextCardNumber()
            totalCardNumber += cardNumber
            val remaining = cardList.size - index - 1
            state.addCopies(cardNumber, winningCount.coerceAtMost(remaining))
        }
        return totalCardNumber
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

    // ugly but good performance
    class State {
        private var startIndex = 0
        private var size = 1
        private var cardCopies = IntArray(20)

        fun getNextCardNumber(): Int {
            if (size == 0) {
                return 1
            }
            return cardCopies[startIndex] + 1
        }

        fun addCopies(cardNumber: Int, winningCount: Int) {
            if (size > 0) {
                startIndex++
                size--
            }
            resize(winningCount)

            repeat(winningCount) { index ->
                cardCopies[startIndex + index] += cardNumber
            }
        }

        override fun toString(): String {
            return cardCopies.slice(startIndex until startIndex + size).joinToString(" ")
        }

        private fun resize(winningCount: Int) {
            if (winningCount > cardCopies.size) {
                val newCard = IntArray(winningCount)
                cardCopies.copyInto(newCard, 0, startIndex, startIndex + size)
                cardCopies = newCard
                startIndex = 0
            } else if (startIndex + winningCount > cardCopies.size) {
                cardCopies.copyInto(cardCopies, 0, startIndex, startIndex + size)
                cardCopies.fill(0, size)
                startIndex = 0
            }
            size = size.coerceAtLeast(winningCount)
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