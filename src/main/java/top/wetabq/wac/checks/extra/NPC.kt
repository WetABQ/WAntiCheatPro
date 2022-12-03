package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.entity.data.EntityMetadata
import co.aikar.timings.Timing
import java.util.Optional

/**
 * Created by funcraft on 2015/11/10.
 */
interface NPC {

    open val ecnpcTiming: Timing?
        get() = null

    //需要其他类继承并重写此过程
    fun onTouch(player: Player)

    fun onNPCUpdate(tick: Int)

    fun startTiming() {
        Optional.ofNullable(ecnpcTiming).ifPresent{ it.startTiming()}
    }

    fun stopTiming() {
        Optional.ofNullable(ecnpcTiming).ifPresent{ it.stopTiming() }
    }

    companion object {

        fun cloneEntityMetadata(metadata: EntityMetadata): EntityMetadata {
            val map = metadata.map
            val re = EntityMetadata()
            map.forEach { i, data -> re.put(data) }
            return re
        }
    }
}
