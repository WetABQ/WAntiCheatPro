package top.wetabq.wac.checks.block.blockbreak.checks

import cn.nukkit.Player
import cn.nukkit.blockentity.BlockEntitySpawnable
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerChunkRequestEvent
import cn.nukkit.level.format.anvil.Chunk
import cn.nukkit.nbt.NBTIO
import cn.nukkit.nbt.tag.CompoundTag
import cn.nukkit.utils.BinaryStream
import cn.nukkit.utils.ThreadCache
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.Check
import top.wetabq.wac.checks.CheckData
import top.wetabq.wac.checks.CheckType
import top.wetabq.wac.checks.block.blockbreak.BlockBreakData
import top.wetabq.wac.checks.exception.CheckCheatException
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.group.RegGroupModule
import java.io.IOException
import java.nio.ByteOrder
import java.util.ArrayList

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
class HiddenMine : Check<BlockBreakData>() {

    companion object {
        var switch = false
    }

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry.addConfigPath(ConfigPaths.CHECKS_BREAK_HIDDENMINE_ORES,object : ArrayList<Int>() {
                init {
                    add(14)
                    add(15)
                    add(16)
                    add(21)
                    add(56)
                    add(73)
                    add(74)
                    add(129)
                }
            })
                    .addConfigPath(ConfigPaths.CHECKS_BREAK_HIDDENMINE_FILTER,object : ArrayList<Int>() {
                        init {
                            add(0)
                            add(8)
                            add(9)
                            add(10)
                            add(11)
                            add(20)
                            add(26)
                            add(27)
                            add(30)
                            add(31)
                            add(32)
                            add(37)
                            add(38)
                            add(39)
                            add(40)
                            add(44)
                            add(50)
                            add(63)
                            add(64)
                            add(65)
                            add(66)
                            add(68)
                            add(71)
                            add(81)
                            add(83)
                            add(85)
                            add(96)
                            add(101)
                            add(102)
                            add(104)
                            add(105)
                            add(106)
                            add(107)
                            add(126)
                            add(141)
                            add(142)
                        }
                    })
                    .addConfigPath(ConfigPaths.CHECKS_BREAK_HIDDENMINE_PROTECTWORLD, arrayListOf<String>())
                    .addConfigPath(ConfigPaths.CHECKS_BREAK_HIDDENMINE_SCANHEIGHT,16.0)
                    .addConfigPath(ConfigPaths.CHECKS_BREAK_HIDDENMINE_DYNAMICSCAN,true)
                    .addCommand(object: WSubCommand("addWorldProtect") {
                        override fun getAliases(): Array<String> {
                            return arrayOf("addWP","awp")
                        }

                        override fun getArguments(): Array<CommandArgument> {
                            return arrayOf(CommandArgument("worldName", "string",false))
                        }

                        override fun getDescription(): String {
                            return "Add a world that doesn't need HiddenMine"
                        }

                        override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                            if(sender.isOp) {
                                val list = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_PROTECTWORLD] as ArrayList<String>?
                                list?.add(args[1])
                                sender.sendMessage(WAntiCheatPro.TITLE + "Successfully added world [${args[1]}]")
                            }
                            return true
                        }

                    })
                    .addCommand(object: WSubCommand("removeWorldProtect") {
                        override fun getAliases(): Array<String> {
                            return arrayOf("removeWP","rmwp")
                        }

                        override fun getArguments(): Array<CommandArgument> {
                            return arrayOf(CommandArgument("worldName", "string",false))
                        }

                        override fun getDescription(): String {
                            return "Remove a world that doesn't need HiddenMine"
                        }

                        override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                            if(sender.isOp) {
                                val list = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_PROTECTWORLD] as ArrayList<String>?
                                list?.remove(args[1])
                                sender.sendMessage(WAntiCheatPro.TITLE + "Successfully removed world [${args[1]}]")
                            }
                            return true
                        }

                    })
                    .registerCommands()
                    .setModuleName(DefaultModuleName.HIDDENMINE)
                    .setModuleAuthor("FENGBerd https://github.com/fengberd/FHiddenMine-Nukkit")
                    .context()
        }
    }

    override fun getCheckType(): CheckType {
        return CheckType.BLOCKBREAK_HIDDENMINE
    }

    override fun checkCheat(player: Player, checkData: CheckData, event: Event?, df: DefaultConfig):Boolean {
        if (!super.checkCheat(player, checkData, event, df)) return true
        if (event is PlayerChunkRequestEvent && checkData is BlockBreakData) {
            switch = true
            val level = player.getLevel()
            val protectWorld = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_PROTECTWORLD] as ArrayList<String>
            if (protectWorld.contains(level.folderName)) return true
            event.setCancelled()
            val ores = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_ORES] as ArrayList<Int>
            val filter = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_FILTER] as ArrayList<Int>
            var scanHeight = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_SCANHEIGHT].toString().toDouble()
            val dynamicScan = df.defaultConfig[ConfigPaths.CHECKS_BREAK_HIDDENMINE_DYNAMICSCAN].toString().toBoolean()
            val chunk = level.getChunk(event.chunkX, event.chunkZ, false) as Chunk
            var blockEntities = ByteArray(0)

            if (!chunk.blockEntities.isEmpty()) {
                val tagList = ArrayList<CompoundTag>()

                for (blockEntity in chunk.blockEntities.values) {
                    if (blockEntity is BlockEntitySpawnable) {
                        tagList.add(blockEntity.spawnCompound)
                    }
                }

                try {
                    blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            val extra = chunk.blockExtraDataArray
            val extraData: BinaryStream?
            if (!extra.isEmpty()) {
                extraData = BinaryStream()
                extraData.putVarInt(extra.size)
                for ((key, value) in extra) {
                    extraData.putVarInt(key)
                    extraData.putLShort(value)
                }
            } else {
                extraData = null
            }

            val stream = ThreadCache.binaryStream.get()
            stream.reset()
            var count = 0
            val sections = chunk.sections
            for (i in sections.indices.reversed()) {
                if (!sections[i].isEmpty) {
                    count = i + 1
                    break
                }
            }
            if (dynamicScan && player.onGround) scanHeight = player.y
            stream.putByte(count.toByte())
            for (i in 0 until count) {
                val section = sections[i]
                var fillIndex = 0
                val blocks = ByteArray(4096)
                val data = ByteArray(2048)
                for (x in 0..15) {
                    for (z in 0..15) {
                        val index = x shl 7 or (z shl 3)
                        var y = 0
                        while (y < 16) {
                            var b1 = 0
                            var b2 = 0
                            var tmpId: Int
                            val x_ = chunk.x * 16
                            var y_ = section.y * 16
                            val z_ = chunk.z * 16
                            val bIndex = (x shl 8) + (z shl 4) + y
                            if (y_ < scanHeight && !filter.contains(level.getBlockIdAt(x_ + x + 1, y_ + y, z_ + z)) && !filter.contains(level.getBlockIdAt(x_ + x - 1, y_ + y, z_ + z)) &&
                                    !filter.contains(level.getBlockIdAt(x_ + x, y_ + y + 1, z_ + z)) && !filter.contains(level.getBlockIdAt(x_ + x, y_ + y - 1, z_ + z)) &&
                                    !filter.contains(level.getBlockIdAt(x_ + x, y_ + y, z_ + z + 1)) && !filter.contains(level.getBlockIdAt(x_ + x, y_ + y, z_ + z - 1))) {
                                tmpId = section.getBlockId(x, y, z)
                                if (tmpId == 1) {
                                    blocks[bIndex] = (ores.get(++fillIndex % ores.size)).toByte()
                                } else {
                                    blocks[bIndex] = tmpId.toByte()
                                    b1 = section.getBlockData(x, y, z)
                                }
                            } else {
                                blocks[bIndex] = section.getBlockId(x, y, z).toByte()
                                b1 = section.getBlockData(x, y, z)
                            }
                            ++y_
                            if (y_ < scanHeight && !filter.contains(level.getBlockIdAt(x_ + x + 1, y_ + y, z_ + z)) && !filter.contains(level.getBlockIdAt(x_ + x - 1, y_ + y, z_ + z)) &&
                                    !filter.contains(level.getBlockIdAt(x_ + x, y_ + y + 1, z_ + z)) && !filter.contains(level.getBlockIdAt(x_ + x, y_ + y - 1, z_ + z)) &&
                                    !filter.contains(level.getBlockIdAt(x_ + x, y_ + y, z_ + z + 1)) && !filter.contains(level.getBlockIdAt(x_ + x, y_ + y, z_ + z - 1))) {
                                tmpId = section.getBlockId(x, y, z)
                                if (tmpId == 1) {
                                    blocks[bIndex + 1] = (ores.get(++fillIndex % ores.size)).toByte()
                                } else {
                                    blocks[bIndex + 1] = tmpId.toByte()
                                    b2 = section.getBlockData(x, y + 1, z)
                                }
                            } else {
                                blocks[bIndex + 1] = section.getBlockId(x, y + 1, z).toByte()
                                b1 = section.getBlockData(x, y + 1, z)
                            }
                            data[index or (y shr 1)] = (b2 shl 4 or b1).toByte()
                            y += 2
                        }
                    }
                }
                stream.putByte(0.toByte())
                stream.put(blocks)
                stream.put(data)
            }
            for (height in chunk.heightMapArray) {
                stream.putByte(height)
            }
            stream.put(ByteArray(256))
            stream.put(chunk.biomeIdArray)
            stream.putByte(0.toByte())
            if (extraData != null) {
                stream.put(extraData.buffer)
            } else {
                stream.putVarInt(0)
            }
            stream.put(blockEntities)
            for (i in sections.indices.reversed()) {
                if (!sections[i].isEmpty) {
                    count = i + 1
                    break
                }
            }
            stream.putByte(count.toByte())
            player.sendChunk(chunk.x, chunk.z, count,stream.buffer)
        } else throw CheckCheatException(this,"Incoming parameter error")
        return true
    }

}