package top.wetabq.wac.checks.utils

import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class TickTask : Runnable {

    /**
     * The Class ImprobableUpdateEntry.
     */
    protected class ImprobableUpdateEntry(var addLevel: Float)

    /** The Constant lagMaxTicks.  */
    val lagMaxTicks = 80

    /** Improbable entries to update.  */
    private var improbableUpdates: MutableMap<UUID, ImprobableUpdateEntry> = LinkedHashMap(50)

    /** The Constant improbableLock.  */
    private val improbableLock = ReentrantLock()

    /** Lock for delayedActions.  */
    private val actionLock = Any() // TODO: Use a ReentrantLock?

    /** Last n tick durations, measured from run to run. */
    private val tickDurations = LongArray(lagMaxTicks)

    /** Tick durations summed up in packs of n (nxn time covered).  */
    private val tickDurationsSq = LongArray(lagMaxTicks)

    /** Maximally covered time on ms for lag tracking, roughly.  */
    private val lagMaxCoveredMs = 50L * (1L + lagMaxTicks * (1L + lagMaxTicks))

    /** Task id of the running TickTask.  */
    private var taskId = -1

    /** The tick.  */
    private var tick = 0

    /** The time start.  */
    private var timeStart: Long = 0

    /** The time last.  */
    private var timeLast: Long = 0

    /** Lock flag set on disable.  */
    private var locked = true

    /**
     * Get the tasks tick count. It is increased with every server tick.<br></br>
     * NOTE: Can be called from other threads.
     * @return The current tick count.
     */
    fun getTick(): Int {
        return tick
    }

    /**
     * Get the time at which the task was started.
     *
     * @return the time start
     */
    fun getTimeStart(): Long {
        return timeStart
    }

    /**
     * Time when last time processing was finished.
     *
     * @return the time last
     */
    fun getTimeLast(): Long {
        return timeLast
    }

    /**
     * Get lag percentage for the last ms milliseconds.<br></br>
     * NOTE: Will not be synchronized, still can be called from other threads.
     * @param ms Past milliseconds to cover. A longer period of time may be used, up to two times if ms > lagMaxTicks * 50.
     * @return Lag factor (1.0 = 20 tps, 2.0 = 10 tps), excluding the current tick.
     */
    fun getLag(ms: Long): Float {
        return getLag(ms, false)
    }

    fun start(): Int {
        cancel()
        if (taskId != -1) {
            timeStart = System.currentTimeMillis()
        } else {
            timeStart = 0
        }
        return taskId
    }

    /**
     * Cancel.
     */
    fun cancel() {
        if (taskId == -1) {
            return
        }
        taskId = -1
    }

    /**
     * Control if new elements can be added to request queues.<br></br>
     * NOTE: This is just a flag, no sync is done here.
     *
     * @param locked
     * the new locked
     */
    fun setLocked(locked: Boolean) {
        // TODO: synchronize over lists !?
        this.locked = locked
    }

    /**
     * Reset tick and tick stats to 0 (!).
     */
    fun reset() {
        tick = 0
        timeLast = 0
        for (i in 0 until lagMaxTicks) {
            tickDurations[i] = 0
            tickDurationsSq[i] = 0
        }
    }


    /**
     * Get lag percentage for the last ms milliseconds, if the specified ms is bigger than the maximally covered duration, the percentage will refer to the maximally covered duration, not the given ms.<br></br>
     * NOTE: Using "exact = true" is meant for checks in the main thread. If called from another thread, exact should be set to false.
     * @param ms Past milliseconds to cover. A longer period of time may be used, up to two times if ms > lagMaxTicks * 50.
     * @param exact If to include the currently running tick, if possible. Should only be set to true, if called from the main thread (or while the main thread is blocked).
     * @return Lag factor (1.0 = 20 tps, 2.0 = 10 tps).
     */
    fun getLag(ms: Long, exact: Boolean): Float {
        if (ms < 0) {
            // Account for freezing (i.e. check timeLast, might be an extra method)!
            return getLag(0, exact)
        } else if (ms > lagMaxCoveredMs) {
            return getLag(lagMaxCoveredMs, exact)
        }
        val tick = tick
        if (tick == 0) {
            return 1f
        }
        val add = if (ms > 0 && ms % 50 == 0L) 0 else 1
        // TODO: Consider: Put "exact" block here, subtract a tick if appropriate?
        val totalTicks = Math.min(tick, add + (ms / 50).toInt())
        val maxTick = Math.min(lagMaxTicks, totalTicks)
        var sum = tickDurations[maxTick - 1]
        var covered = (maxTick * 50).toLong()

        // Only count fully covered:
        if (totalTicks > lagMaxTicks) {
            var maxTickSq = Math.min(lagMaxTicks, totalTicks / lagMaxTicks)
            if (lagMaxTicks * maxTickSq == totalTicks) {
                maxTickSq -= 1
            }
            sum += tickDurationsSq[maxTickSq - 1]
            covered += (lagMaxTicks * 50 * maxTickSq).toLong()
        }

        if (exact) {
            // Attempt to count in the current tick.
            val passed = System.currentTimeMillis() - timeLast
            if (passed > 50) {
                // Only count in in the case of "overtime".
                covered += 50
                sum += passed
            }
        }
        // TODO: Investigate on < 1f.
        return Math.max(1f, sum.toFloat() / covered.toFloat())
    }

    // Instance methods (meant private).


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    override fun run() {
        // Measure time after heavy stuff.
        val time = System.currentTimeMillis()
        val lastDur: Long

        // Time running backwards check (not only players can!).
        if (timeLast > time) {
            lastDur = 50
        } else if (tick > 0) {
            lastDur = time - timeLast
        } else {
            lastDur = 50
        }

        // Update sums of sums of tick durations.
        if (tick > 0 && tick % lagMaxTicks == 0) {
            val sum = tickDurations[lagMaxTicks - 1]
            for (i in 1 until lagMaxTicks) {
                tickDurationsSq[i] = tickDurationsSq[i - 1] + sum
            }
            tickDurationsSq[0] = sum
        }

        // Update tick duration sums.
        for (i in 1 until lagMaxTicks) {
            tickDurations[i] = tickDurations[i - 1] + lastDur
        }
        tickDurations[0] = lastDur
        // Finish.
        tick++
        timeLast = time
    }

}
