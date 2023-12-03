package wuairc.aoc

fun main() {
    Day03().solve()
}

class Day03 : Template(3) {
    override fun part1(input: List<String>): Int {
        val schematic = parseInput(input)
        val body = schematic.windowed(3, 1).flatMap {
            it[1].getAdjacentNumbers(it)
        }.sumOf { it.value }
        val head = schematic.first().getAdjacentNumbers(schematic.subList(0, schematic.size.coerceAtMost(2)))
            .sumOf { it.value }
        val tail = if (schematic.size > 1) {
            schematic.last().getAdjacentNumbers(schematic.subList(schematic.size - 2, schematic.size))
                .sumOf { it.value }
        } else {
            0
        }
        return head + body + tail
    }

    override fun part2(input: List<String>): Int {
        val schematic = parseInput(input)
        val body = schematic.windowed(3, 1).flatMap {
            it[1].getGearRatio(it)
        }.sum()
        val head = schematic.first().getGearRatio(
            schematic.subList(0, schematic.size.coerceAtMost(2))
        ).sum()
        val tail = if (schematic.size > 1) {
            schematic.last().getGearRatio(schematic.subList(schematic.size - 2, schematic.size)).sum()
        } else {
            0
        }
        return head + body + tail
    }

    private fun parseInput(input: List<String>): List<SchematicLine> = input.map { line ->
        val numbers = mutableListOf<Number>()
        val symbols = mutableListOf<Symbol>()
        var index = 0
        while (index < line.length) {
            val ch = line[index]
            when {
                ch.isDigit() -> {
                    val startIndex = index
                    index++
                    while (index < line.length && line[index].isDigit()) {
                        index++
                    }
                    val range = startIndex until index
                    val number = line.substring(range).toInt()
                    numbers.add(Number(range, number))
                }

                ch == '.' -> {
                    index++
                }

                else -> {
                    symbols.add(Symbol(index, ch))
                    index++
                }
            }
        }
        SchematicLine(numbers, symbols)
    }
}

data class Number(val range: IntRange, val value: Int) {
    fun isAdjacentToSymbol(symbol: Symbol): Boolean {
        val symbolIndex = symbol.index
        return range.contains(symbolIndex) || range.first - symbolIndex == 1 || symbolIndex - range.last == 1
    }
}

data class Symbol(val index: Int, val value: Char)

data class SchematicLine(val numbers: List<Number>, val symbols: List<Symbol>) {
    /**
     * @param adjacentLines including this line
     */
    fun getAdjacentNumbers(adjacentLines: List<SchematicLine>): Iterable<Number> {
        return numbers.filter { number ->
            adjacentLines.any { line -> line.isAdjacentToSymbol(number) }
        }
    }

    /**
     * @param adjacentLines including this line
     */
    fun getGearRatio(adjacentLines: List<SchematicLine>): Iterable<Int> {
        return symbols.filter { it.value == '*' }.mapNotNull { symbol ->
            val adjacentNumbers = adjacentLines.flatMap { line ->
                line.numbers.filter { number ->
                    number.isAdjacentToSymbol(symbol)
                }
            }
            if (adjacentNumbers.size == 2) {
                return@mapNotNull adjacentNumbers[0].value * adjacentNumbers[1].value
            } else {
                null
            }
        }
    }

    private fun isAdjacentToSymbol(number: Number): Boolean {
        return this.symbols.any { symbol ->
            number.isAdjacentToSymbol(symbol)
        }
    }
}