package top.wetabq.wac.checks.extra.ecnpc

import cn.nukkit.Player
import cn.nukkit.network.protocol.DataPacket

/**
 * Created by root on 16-3-30.
 */
class SendDataPacketRunnable : Runnable {

    private var pk: DataPacket? = null
    private var player: Array<Player>? = null

    constructor(p: Player, pk0: DataPacket) {
        this.player = arrayOf(p)
        pk = pk0
    }

    constructor(p: Array<Player>, pk0: DataPacket) {
        player = p
        pk = pk0
    }

    override fun run() {
        for (player in this.player!!) {
            player.dataPacket(this.pk)
        }
    }

}
