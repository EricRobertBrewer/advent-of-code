@file:Import("../AocUtil.kts")


data class UnitQuantity(val unit: String, val quantity: Int) {

    companion object {

        operator fun invoke(unitQuantity: String): UnitQuantity {
            val uq = unitQuantity.split(" ")
            return UnitQuantity(uq[1], uq[0].toInt())
        }
    }

    override fun toString(): String {
        return "$quantity $unit"
    }
}

solve(14, fun(lines: List<String>, part: Int): Long {
    val unitToQuantity = mutableMapOf<String, Int>() // Chemical -> number produced
    val unitToInputUnits = mutableMapOf<String, List<String>>() // Chemical -> chemicals required
    val unitToInputQuantities = mutableMapOf<String, List<Int>>() // Chemical -> number of each chemical required
    for (line in lines) {
        val sides = line.split(" => ")
        val output = UnitQuantity(sides[1])
        val inputs = sides[0].split(", ").map { UnitQuantity(it) }
        if (unitToQuantity.containsKey(output.unit)) {
            throw RuntimeException("Unit `${output.unit}` already has a reaction")
        }
        unitToQuantity[output.unit] = output.quantity
        unitToInputUnits[output.unit] = inputs.map { it.unit }
        unitToInputQuantities[output.unit] = inputs.map { it.quantity }
    }

    val pile = mutableMapOf<String, Int>() // Leftover from reactions.
    var oreConsumed = 0L
    var fuelProduced = 0L
    while (true) {
        // Do one FUEL reaction.
        val required = mutableMapOf("FUEL" to 1)
        while (required.keys.count() > 1 || !required.containsKey("ORE")) {
            for (unit in required.keys) {
                if (unit == "ORE") continue
                if (!pile.containsKey(unit)) {
                    pile[unit] = 0
                }
                // Produce chemical.
                if (pile[unit]!! < required[unit]!!) {
                    val quantity = unitToQuantity[unit]!!
                    val deficit = required[unit]!! - pile[unit]!!
                    val times = deficit / quantity + if (deficit % quantity != 0) 1 else 0
                    for (i in unitToInputUnits[unit]!!.indices) {
                        val inputUnit = unitToInputUnits[unit]!![i]
                        if (!required.containsKey(inputUnit)) {
                            required[inputUnit] = 0
                        }
                        required[inputUnit] = required[inputUnit]!! + times * unitToInputQuantities[unit]!![i]
                    }
                    pile[unit] = pile[unit]!! + times * unitToQuantity[unit]!!
                }
                // Remove produced chemical.
                pile[unit] = pile[unit]!! - required[unit]!!
                required.remove(unit)
                break
            }
        }
        val oreRequired = required["ORE"]!!.toLong()
        if (part == 1) {
            return oreRequired
        }
        oreConsumed += oreRequired
        if (oreConsumed > 1000000000000L) {
            break
        }
        fuelProduced += unitToQuantity["FUEL"]!!
    }
    return fuelProduced // TODO: ~54 s; Detect a pile-to-fuel-produced cycle?
})
