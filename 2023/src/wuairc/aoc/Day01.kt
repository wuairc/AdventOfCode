package wuairc.aoc

import wuairc.aoc.util.Template

fun main() {
    Day01().solve()
}

class Day01 : Template(1) {
    override fun part1(input: List<String>): Int {
        return input.sumOf {
            with(it.toCharArray()) {
                first(Char::isDigit).digitToInt() * 10 + last(Char::isDigit).digitToInt()
            }
        }
    }

    override fun part2(input: List<String>): Int {
        val digitMap: Map<String, Int> = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9
        )

        val maxKeyLength = digitMap.keys.maxOf { it.length }

        val digitFinder: (String) -> Sequence<Int> = { line ->
            line.windowedSequence(maxKeyLength, 1, true).mapNotNull { slice ->
                for (entry in digitMap.entries) {
                    if (slice.startsWith(entry.key)) {
                        return@mapNotNull entry.value
                    }
                }
                return@mapNotNull null
            }
        }
        return input.sumOf {
            val firstDigit = digitFinder(it).first()
            val secondDigit = digitFinder(it).last()
            firstDigit * 10 + secondDigit
        }
    }

    override fun expectedTestOutput(): Pair<Int, Int> = 142 to 281
}