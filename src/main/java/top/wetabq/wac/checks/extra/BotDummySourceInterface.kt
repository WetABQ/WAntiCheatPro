package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.network.SourceInterface
import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.session.NetworkPlayerSession
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class BotDummySourceInterface : SourceInterface {

    private val players = HashMap<String, FakePlayerBot>()
    private val identifiers = ConcurrentHashMap<Int, String>()
    val player2Address = HashMap<InetSocketAddress, Player>()

    fun getPlayers(): Map<String, FakePlayerBot> {
        return players
    }

    override fun getNetworkLatency(player: Player): Int {
        return 0
    }

    override fun process(): Boolean {
        return true
    }

    override fun putPacket(player: Player, dataPacket: DataPacket): Int? {
        return 0
    }

    override fun putPacket(player: Player, dataPacket: DataPacket, b: Boolean): Int? {
        return 0
    }

    override fun putPacket(player: Player, dataPacket: DataPacket, b: Boolean, b1: Boolean): Int? {
        return 0
    }

    override fun getSession(address: InetSocketAddress?): NetworkPlayerSession {
        return DummyNetworkPlayerSession(this, address!!)
    }

    fun open(identifier: String, address: InetAddress, port: Int, clientID: Long, targetPlayer: Player): FakePlayerBot {
        val player = FakePlayerBot(this, clientID, address, port, targetPlayer)
        this.players[identifier] = player
        this.identifiers[player.rawHashCode()] = identifier
        this.player2Address[InetSocketAddress(address, port)] = player
        Server.getInstance().addPlayer(InetSocketAddress(address, port), player)
        return player
    }

    override fun close(player: Player) {
        this.close(player, "unknown reason")
    }

    override fun close(player: Player, s: String) {
        if (this.identifiers.containsKey(player.rawHashCode())) {
            val id = this.identifiers[player.rawHashCode()]
            this.players.remove(id)
            this.identifiers.remove(player.rawHashCode())
        }
    }

    override fun emergencyShutdown() {

    }

    override fun setName(s: String) {

    }

    override fun shutdown() {

    }

}