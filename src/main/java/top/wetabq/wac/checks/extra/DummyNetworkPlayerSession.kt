package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.network.CompressionProvider
import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.session.NetworkPlayerSession

/**
 * 2022/12/3<br></br>
 * WAntiCheatPro<br></br>
 *
 * @author huanmeng_qwq
 */
class DummyNetworkPlayerSession(
    private val player: Player
) : NetworkPlayerSession {
    private var compressionProvider: CompressionProvider = CompressionProvider.NONE

    override fun sendPacket(dataPacket: DataPacket) {}
    override fun sendImmediatePacket(dataPacket: DataPacket, runnable: Runnable) {}
    override fun disconnect(s: String) {}
    override fun getPlayer(): Player {
        return player
    }


    override fun setCompression(compressionProvider: CompressionProvider) {
        this.compressionProvider = compressionProvider
    }

    override fun getCompression(): CompressionProvider {
        return compressionProvider
    }
}