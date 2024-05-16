@file:Import(
    "../AocUtil.kts",
    "../Intcode.kts",
)

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


solve(23, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    val n = 50 // Number of computers.
    val queues = Array(n) { PacketQueue() }
    queues.forEachIndexed { i, q -> q.pushInput(i.toLong()) } // Network address.
    val intcodes = Array(n) { Intcode(program, queues[it]) }

    val doRun = AtomicBoolean(true)
    val xNat = AtomicLong()
    val yNat = AtomicLong()
    val threads = intcodes.map { intcode ->
        Thread {
            while (doRun.get()) {
                val index = intcode.run().toInt() // Waiting for input.
                if (0 <= index && index < n) {
                    val x = intcode.run()
                    val y = intcode.run()
                    queues[index].pushPacket(x, y)
                } else if (index == 255) {
                    val x = intcode.run()
                    val y = intcode.run()
                    if (part == 1) {
                        println("255: $x $y")
                        doRun.set(false)
                    }
                    xNat.set(x)
                    yNat.set(y)
                }
            }
        }
    }
    threads.forEach { it.start() }

    // For part 2 only.
    // This maybe shouldn't be a thread - it should be a callable method
    // that is triggered when the number of idle threads is atomically
    // incremented to `n`.
    var threadNat: Thread? = null
    val yNatDeliveredSet = ConcurrentHashMap<Long, Boolean>()
    if (part != 1) {
        threadNat = Thread {
            while (doRun.get()) {
                Thread.sleep(250L)
                // TODO: Not guaranteed to be deterministic. It probably depends on the sleep time.
                val idle = queues.count { it.size == 0 }
                if (idle < n) {
                    continue
                }
                val x = xNat.get()
                val y = yNat.get()
                if (yNatDeliveredSet.containsKey(y)) {
                    println("y repeated: $y")
                    doRun.set(false)
                } else {
                    yNatDeliveredSet[y] = true
                    queues[0].pushPacket(x, y)
                }
            }
        }
        threadNat.start()
    }

    while (doRun.get()) {
        Thread.sleep(100L)
    }
    threads.forEach { it.join(100L) }
    threadNat?.join(100L)
    return -1 // TODO: Terminate.
})

class PacketQueue: IntcodeInputProvider {

    private val queue = ConcurrentLinkedQueue<Long>()

    override fun getInput(): Long {
        return queue.poll() ?: -1L
    }

    fun pushPacket(x: Long, y:Long) {
        synchronized(this) {
            queue.add(x)
            queue.add(y)
        }
    }

    fun pushInput(x: Long) {
        queue.add(x)
    }

    val size get() = queue.size
}
