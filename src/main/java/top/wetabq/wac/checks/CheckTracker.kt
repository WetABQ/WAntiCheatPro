package top.wetabq.wac.checks

import cn.nukkit.Player
import top.wetabq.wac.checks.access.ICheckTracker
import java.util.LinkedList



/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
abstract class CheckTracker<T>(protected val player: Player,protected val size: Int) : ICheckTracker<T> {

    private val trackerQueue = LinkedList<T>()

    override fun addT(t: T) {
        if (trackerQueue.size >= size) getTrackerQueue().poll()
        getTrackerQueue().add(t)
    }

    fun getTrackerQueue() : LinkedList<T> {
        return trackerQueue
    }

    override fun getTrackerSize() : Int {
        return size
    }

    override fun getLast(): T{
        return trackerQueue.peek()
    }

    override fun isFull(): Boolean {
        return trackerQueue.size >= size
    }

    override fun clearAll() {
        trackerQueue.clear()
    }

}