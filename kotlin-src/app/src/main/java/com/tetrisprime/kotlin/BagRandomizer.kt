package com.tetrisprime.kotlin

import kotlin.random.Random

/**
 * 7-bag randomizer — ensures fair piece distribution.
 * Each batch of 7 contains all 7 piece types in random order.
 */
class BagRandomizer(private val rng: Random = Random.Default) {
    private val bag = mutableListOf<PieceType>()
    private var index = 0

    init {
        refill()
    }

    private fun refill() {
        bag.clear()
        bag.addAll(PieceType.values().toList())
        bag.shuffle(rng)
        index = 0
    }

    fun next(): PieceType {
        if (index >= bag.size) {
            refill()
        }
        return bag[index++]
    }

    /** Preview the next N pieces. */
    fun preview(n: Int): List<PieceType> {
        val result = mutableListOf<PieceType>()
        var i = index
        var tempBag = bag.toMutableList()
        while (result.size < n) {
            if (i >= tempBag.size) {
                tempBag = PieceType.values().toMutableList().also { it.shuffle(rng) }
                i = 0
            }
            result.add(tempBag[i++])
        }
        return result
    }
}
