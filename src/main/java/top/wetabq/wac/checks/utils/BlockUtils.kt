package top.wetabq.wac.checks.utils

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.item.Item
import cn.nukkit.item.ItemTool
import cn.nukkit.item.enchantment.Enchantment
import cn.nukkit.level.Position
import cn.nukkit.potion.Effect
import java.util.*
import kotlin.collections.ArrayList
import cn.nukkit.level.particle.DustParticle
import cn.nukkit.math.Vector3



/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
object BlockUtils {
    private fun toolBreakTimeBonus0(toolType: Int, toolTier: Int, isWoolBlock: Boolean, isCobweb: Boolean): Double {
        if (toolType == ItemTool.TYPE_SWORD) return if (isCobweb) 15.0 else 1.0
        if (toolType == ItemTool.TYPE_SHEARS) return if (isWoolBlock) 5.0 else 15.0
        if (toolType == ItemTool.TYPE_NONE) return 1.0
        when (toolTier) {
            ItemTool.TIER_WOODEN -> return 2.0
            ItemTool.TIER_STONE -> return 4.0
            ItemTool.TIER_IRON -> return 6.0
            ItemTool.TIER_DIAMOND -> return 8.0
            ItemTool.TIER_GOLD -> return 12.0
            else -> return 1.0
        }
    }

    private fun speedBonusByEfficiencyLore0(efficiencyLoreLevel: Int): Double {
        return if (efficiencyLoreLevel == 0) 0.0 else (efficiencyLoreLevel * efficiencyLoreLevel + 1).toDouble()
    }

    private fun speedRateByHasteLore0(hasteLoreLevel: Int): Double {
        return 1.0 + 0.2 * hasteLoreLevel
    }

    private fun toolType0(item: Item): Int {
        if (item.isSword) return ItemTool.TYPE_SWORD
        if (item.isShovel) return ItemTool.TYPE_SHOVEL
        if (item.isPickaxe) return ItemTool.TYPE_PICKAXE
        if (item.isAxe) return ItemTool.TYPE_AXE
        return if (item.isShears) ItemTool.TYPE_SHEARS else ItemTool.TYPE_NONE
    }

    private fun correctTool0(blockToolType: Int, item: Item): Boolean {
        return blockToolType == ItemTool.TYPE_SWORD && item.isSword ||
                blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel ||
                blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe ||
                blockToolType == ItemTool.TYPE_AXE && item.isAxe ||
                blockToolType == ItemTool.TYPE_SHEARS && item.isShears ||
                blockToolType == ItemTool.TYPE_NONE
    }

    //http://minecraft.gamepedia.com/Breaking
    private fun breakTime0(blockHardness: Double, correctTool: Boolean, canHarvestWithHand: Boolean,
                           blockId: Int, toolType: Int, toolTier: Int, efficiencyLoreLevel: Int, hasteEffectLevel: Int,
                           insideOfWaterWithoutAquaAffinity: Boolean, outOfWaterButNotOnGround: Boolean): Double {
        val baseTime = (if (correctTool || canHarvestWithHand) 1.5 else 5.0) * blockHardness
        var speed = 1.0 / baseTime
        val isWoolBlock = blockId == Block.WOOL
        val isCobweb = blockId == Block.COBWEB
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, isWoolBlock, isCobweb)
        speed += speedBonusByEfficiencyLore0(efficiencyLoreLevel)
        speed *= speedRateByHasteLore0(hasteEffectLevel)
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2
        if (outOfWaterButNotOnGround) speed *= 0.2
        return 1.0 / speed
    }

    @JvmStatic
    fun getBreakTime(item: Item, player: Player,block: Block): Double {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null")
        Objects.requireNonNull(player, "getBreakTime: Player can not be null")
        val blockHardness = block.hardness
        val correctTool = correctTool0(block.toolType, item)
        val canHarvestWithHand = block.canHarvestWithHand()
        val blockId = block.id
        val itemToolType = toolType0(item)
        val itemTier = item.tier
        val efficiencyLoreLevel = item.getEnchantment(Enchantment.ID_EFFICIENCY)?.level ?: 0
        val hasteEffectLevel = player.getEffect(Effect.HASTE)?.amplifier ?:0
        val insideOfWaterWithoutAquaAffinity = player.isInsideOfWater && (player.inventory.helmet.getEnchantment(Enchantment.ID_WATER_WORKER)?.level ?: 0 > 1)
        val outOfWaterButNotOnGround = !player.isInsideOfWater && !player.isOnGround
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, blockId, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround)
    }

    @JvmStatic
    fun getLineBlock(pos1: Position,pos2: Position) : ArrayList<Block> {
        val dis = pos1.distance(pos2)
        var t = 0.0
        val blockList = ArrayList<Block>()
        while (t <= 1) {
            val v3 = Vector3(pos1.x + (pos2.x - pos1.x) * t, pos1.y + (pos2.y - pos1.y) * t, pos1.z + (pos2.z - pos1.z) * t)
            if (pos1.getLevel().folderName.equals(pos2.getLevel().folderName)) {
                val block = pos1.level.getBlock(v3.floor())
                if (block.isSolid && !block.canPassThrough() && !block.canBeClimbed() && block.isNormalBlock) {
                    blockList.add(block)
                }
            }
            t += 1 / dis
        }
        return blockList
    }

}