package wuairc.aoc

fun main() {
    Day07().solve()
}

class Day07 : Template(7) {
    override fun part1(input: List<String>): Int {
        return calculateWinning(input, Part1Rule)
    }

    override fun part2(input: List<String>): Int {
        return calculateWinning(input, Part2Rule)
    }

    private fun calculateWinning(input: List<String>, rule: Rule): Int {
        val handBids = parseInput(input, rule)
        val value = handBids.sortedWith(rule.getHandBidComparator()).withIndex().sumOf {
            println("${it.value.bid} * ${it.index + 1}")
            it.value.bid * (it.index + 1)
        }
        if (value < Int.MAX_VALUE) {
            return value.toInt()
        } else {
            throw AssertionError(value)
        }
    }

    private fun parseInput(input: List<String>, order: Rule): List<HandBid> {
        return input.map { line ->
            val (cardsLine, bidLine) = line.split(' ', limit = 2)
            val cards = cardsLine.trim().toCharArray()
            val bid = bidLine.trim().toLong()
            val kind = order.getKind(cards)
            val hand = Hand(cards, kind)
            HandBid(hand, bid)
        }
    }

    interface Rule {
        fun getKind(cards: CharArray): Kind

        fun getHandBidComparator(): Comparator<HandBid>

        fun getCardComparator(): Comparator<Char>
    }

    object Part1Rule : Rule {
        private val cardOrderMap: Map<Char, Int> =
            arrayOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
                .reversedArray()
                .withIndex()
                .associateBy({ it.value }, { it.index })

        private object CardComparator : Comparator<Char> {
            override fun compare(left: Char, right: Char): Int {
                if (left == right) {
                    return 0
                }
                return cardOrderMap[left]!!.compareTo(cardOrderMap[right]!!)
            }
        }

        private object HandComparator : Comparator<Hand> {
            override fun compare(left: Hand, right: Hand): Int {
                if (left === right) {
                    return 0
                }
                val kindOrder = left.kind.compareTo(right.kind)
                if (kindOrder != 0) {
                    return kindOrder
                }
                for (pair in left.cards.zip(right.cards)) {
                    val cardOrder = CardComparator.compare(pair.first, pair.second)
                    if (cardOrder != 0) {
                        return cardOrder
                    }
                }
                return 0
            }
        }

        private object HandBidComparator : Comparator<HandBid> {
            override fun compare(o1: HandBid, o2: HandBid): Int {
                return HandComparator.compare(o1.hand, o2.hand)
            }
        }

        override fun getKind(cards: CharArray): Kind {
            val counter = mutableMapOf<Char, IntArray>()
            for (card in cards) {
                counter.computeIfAbsent(card) { IntArray(1) }[0]++
            }
            val entries = counter.entries.sortedByDescending { it.value[0] }.map { it.key to it.value[0] }
            return when (entries.size) {
                1 -> Kind.Five
                2 -> if (entries[0].second == 4) Kind.Four else Kind.FullHouse
                3 -> if (entries[0].second == 3) Kind.Three else Kind.TwoPair
                4 -> Kind.OnePair
                5 -> Kind.HighCard
                else -> throw AssertionError()
            }
        }

        override fun getHandBidComparator(): Comparator<HandBid> = HandBidComparator
        override fun getCardComparator(): Comparator<Char> = CardComparator
    }

    object Part2Rule : Rule {
        private val cardOrderMap: Map<Char, Int> =
            arrayOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
                .reversedArray()
                .withIndex()
                .associateBy({ it.value }, { it.index })

        private object CardComparator : Comparator<Char> {
            override fun compare(left: Char, right: Char): Int {
                if (left == right) {
                    return 0
                }
                return cardOrderMap[left]!!.compareTo(cardOrderMap[right]!!)
            }
        }

        private object HandComparator : Comparator<Hand> {
            override fun compare(left: Hand, right: Hand): Int {
                if (left === right) {
                    return 0
                }
                val kindOrder = left.kind.compareTo(right.kind)
                if (kindOrder != 0) {
                    return kindOrder
                }
                for (pair in left.cards.zip(right.cards)) {
                    val cardOrder = CardComparator.compare(pair.first, pair.second)
                    if (cardOrder != 0) {
                        return cardOrder
                    }
                }
                return 0
            }
        }

        private object HandBidComparator : Comparator<HandBid> {
            override fun compare(o1: HandBid, o2: HandBid): Int {
                return HandComparator.compare(o1.hand, o2.hand)
            }
        }

        override fun getKind(cards: CharArray): Kind {
            val counter = mutableMapOf<Char, IntArray>()
            for (card in cards) {
                counter.computeIfAbsent(card) { IntArray(1) }[0]++
            }
            val jokerCount = counter.remove('J')?.let { it[0] } ?: 0
            val entries = counter.entries.sortedByDescending { it.value[0] }.also {
                if (it.isNotEmpty()) {
                    it[0].value[0] += jokerCount
                }
            }.map { it.key to it.value[0] }
            return when (entries.size) {
                0, 1 -> Kind.Five
                2 -> if (entries[0].second == 4) Kind.Four else Kind.FullHouse
                3 -> if (entries[0].second == 3) Kind.Three else Kind.TwoPair
                4 -> Kind.OnePair
                5 -> Kind.HighCard
                else -> throw AssertionError()
            }
        }

        override fun getHandBidComparator(): Comparator<HandBid> = HandBidComparator
        override fun getCardComparator(): Comparator<Char> = CardComparator
    }

    enum class Kind : Comparable<Kind> {
        // ABCDE
        HighCard,

        // AABCD,
        OnePair,

        // AABBC
        TwoPair,

        // AAABC
        Three,

        // AAABB
        FullHouse,

        // AAAAB
        Four,

        // AAAAA
        Five
    }

    data class Hand(val cards: CharArray, val kind: Kind) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Hand

            if (!cards.contentEquals(other.cards)) return false
            if (kind != other.kind) return false

            return true
        }

        override fun hashCode(): Int {
            var result = cards.contentHashCode()
            result = 31 * result + kind.hashCode()
            return result
        }
    }

    data class HandBid(val hand: Hand, val bid: Long)
}