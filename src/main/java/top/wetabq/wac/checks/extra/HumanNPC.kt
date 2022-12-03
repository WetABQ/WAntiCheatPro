package top.wetabq.wac.checks.extra

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.entity.Entity
import cn.nukkit.entity.EntityHuman
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.entity.data.Skin
import cn.nukkit.event.entity.EntityDamageByBlockEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.inventory.InventoryHolder
import cn.nukkit.item.Item
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.level.format.FullChunk
import cn.nukkit.nbt.tag.CompoundTag
import cn.nukkit.nbt.tag.DoubleTag
import cn.nukkit.nbt.tag.FloatTag
import cn.nukkit.nbt.tag.ListTag
import cn.nukkit.network.protocol.AddPlayerPacket
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket
import cn.nukkit.network.protocol.PlayerListPacket
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.extra.ecnpc.SendDataPacketRunnable

import java.util.*

/**
 * Created by funcraft on 2015/11/13.
 */
open class HumanNPC : EntityHuman, InventoryHolder, NPC {

    protected var nametg = ""

    var middle = true
    private val cache = HashMap<Locale, String>()

    val isLevelChange: Boolean
        get() = false

    val isOnline: Boolean
        get() = true

    val isSpectator: Boolean
        get() = false

    init {
        this.isNameTagVisible = true
        this.isNameTagAlwaysVisible = true
        this.getInventory().heldItemSlot = 0
    }

    constructor(chunk: FullChunk, nbt: CompoundTag, name: String,skin: Skin) : super(chunk, nbt) {
        this.isNameTagVisible = true
        this.isNameTagAlwaysVisible = true
        this.getInventory().heldItemSlot = 0
        this.skin = skin
    }


    constructor(pos: Location, name: String,skin: Skin) : this(pos.getLevel().getChunk(pos.floorX shr 4, pos.floorZ shr 4),
            CompoundTag()
                    .putList(ListTag<DoubleTag>("Pos")
                            .add(DoubleTag("", pos.x))
                            .add(DoubleTag("", pos.y))
                            .add(DoubleTag("", pos.z)))
                    .putList(ListTag<DoubleTag>("Motion")
                            .add(DoubleTag("", 0.0))
                            .add(DoubleTag("", 0.0))
                            .add(DoubleTag("", 0.0)))
                    .putList(ListTag<FloatTag>("Rotation")
                            .add(FloatTag("", pos.yaw.toFloat()))
                            .add(FloatTag("", pos.pitch.toFloat()))
                    ),name,skin) {
        this.saveNBT()
    }


    override fun attack(source: EntityDamageEvent): Boolean {
        return if (source !is EntityDamageByBlockEvent) super.attack(source) else false
    }

    override fun spawnTo(player: Player) {
        if (!this.skin.isValid) {
            throw IllegalStateException(this.javaClass.simpleName + " must have a valid skin set")
        }

        this.sendSkin(player.getPlayer())

        val pk = AddPlayerPacket()
        pk.uuid = this.uniqueId
        pk.username = name
        pk.entityUniqueId = this.getId()
        pk.entityRuntimeId = this.getId()
        pk.x = this.x.toFloat()
        pk.y = this.y.toFloat()
        pk.z = this.z.toFloat()
        pk.speedX = this.motionX.toFloat()
        pk.speedY = this.motionY.toFloat()
        pk.speedZ = this.motionZ.toFloat()
        pk.yaw = this.yaw.toFloat()
        pk.pitch = this.pitch.toFloat()
        pk.item = this.getInventory().itemInHand
        pk.metadata = NPC.cloneEntityMetadata(this.dataProperties).putString(Entity.DATA_NAMETAG, this.nameTag)
        player.dataPacket(pk)
        this.inventory.sendArmorContents(player.getPlayer())
        //System.out.println("Spawned a NPC to " + player.getName());
        this.getInventory().sendHeldItem(player.getPlayer())
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.getPlayer().usedChunks.containsKey(Level.chunkHash(this.chunk.x, this.chunk.z))) {
            this.hasSpawned[player.getLoaderId()] = player.getPlayer()
        }

    }

    override fun onNPCUpdate(tick: Int) {

    }


    fun sendSkin(player: Player) {
        /*
        PlayerSkinPacket pk = new PlayerSkinPacket();
        pk.uuid = this.uuid;
        pk.skin = this.skin;
        pk.oldSkinName = "";
        pk.newSkinName = "";
        pk.premium = false;

        player.dataPacket(pk);*/

        val pk1 = PlayerListPacket()
        pk1.type = PlayerListPacket.TYPE_ADD
        val entry = PlayerListPacket.Entry(this.uuid, this.getId(), "", this.skin)
        pk1.entries = arrayOf(entry)
        player.dataPacket(pk1)
        val pk2 = PlayerListPacket()
        pk2.type = PlayerListPacket.TYPE_REMOVE
        pk2.entries = arrayOf(PlayerListPacket.Entry(this.uuid))
        this.getServer().scheduler.scheduleDelayedTask(SendDataPacketRunnable(player, pk2), 10)
    }

    override fun getSkin(): Skin {
        return if (super.getSkin() == null) WAntiCheatPro.skin else super.getSkin()
    }

    override fun setNameTag(name: String) {
        val same = name.equals(this.nametg)
        this.cache.clear()
        this.nametg = name
        if (!same) this.sendNameTag()
    }

    fun sendNameTag() {
        for (player in ArrayList(this.hasSpawned.values)) {
            this.sendNameTag(player as Player)
        }
    }

    fun sendNameTag(player: Player) {
        val data = EntityMetadata()
                .putString(Entity.DATA_NAMETAG, this.nameTag)

        this.sendData(player.getPlayer(), data)
    }

    fun resendPosition() {
        val pk = MoveEntityAbsolutePacket()
        pk.eid = this.getId()
        pk.x = x
        pk.y = y + this.baseOffset
        pk.z = z
        pk.yaw = yaw
        pk.headYaw = yaw
        pk.pitch = pitch
        Server.broadcastPacket(this.viewers.values, pk)
        //this.getLevel().addEntityMovement(this.chunk.getX(), this.chunk.getZ(), this.getId(), this.x, this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.yaw);
    }

    override fun onInteract(player: Player?, item: Item?): Boolean {
        if (player is Player) this.onTouch(player)
        return super.onInteract(player, item)
    }

    override fun onTouch(player: Player) {

    }

    override fun close() {
        super.close()
        this.getServer().logger.debug("closed " + this.javaClass.name)
    }

    override fun checkBlockCollision() {
        //深度优化，减少无用计算量
    }

}
