package top.wetabq.wac.checks.utils

import cn.nukkit.Player
import cn.nukkit.block.BlockLadder
import cn.nukkit.block.BlockStairs
import cn.nukkit.block.BlockVine

/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.07
 * @version 1.0
 */
object PlayerUtils {

    @JvmStatic
    fun playerInAir(p: Player): Boolean {
        val level = p.getLevel()
        for (y in p.floorY - 1..p.floorY) {
            for (x in p.floorX - 1..p.floorX + 1) {
                for (z in p.floorZ - 1..p.floorZ + 1) {
                    if (!level.getBlock(x,y,z).canPassThrough()) return false
                }
            }
        }
        for (block in p.getBlocksAround()) {
            if (block.id != 0) {
                return false
            }
        }
        for (block in p.getCollisionBlocks()) {
            if (block.id != 0) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun playerInWater(p: Player): Boolean {
        val level = p.getLevel()
        for (y in p.floorY - 1..p.floorY) {
            for (x in p.floorX - 1..p.floorX + 1) {
                for (z in p.floorZ - 1..p.floorZ + 1) {
                    if (level.getBlock(x,y,z).id != 8 && level.getBlock(x,y,z).id != 111 && level.getBlock(x,y,z).id != 9) return false
                }
            }
        }
        for (block in p.getBlocksAround()) {
            if (block.id != 8 && block.id != 111 && block.id != 9) {
                return false
            }
        }
        for (block in p.getCollisionBlocks()) {
            if (block.id != 8 && block.id != 111 && block.id != 9) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun playerInLadder(p: Player): Boolean {
        /*val level = p.getLevel()
        for (y in p.floorY - 1..p.floorY) {
            for (x in p.floorX - 1..p.floorX + 1) {
                for (z in p.floorZ - 1..p.floorZ + 1) {
                    val block = level.getBlock(x,y,z)
                    if (block.canBeClimbed() || block is BlockLadder || block is BlockVine) {
                        return true
                    }
                }
            }
        }*/
        for (block in p.getBlocksAround()) {
            if (block is BlockLadder || block is BlockVine) {
                return true
            }
        }
        for (block in p.getCollisionBlocks()) {
            if (block is BlockLadder || block is BlockVine) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun playerInS(p: Player): Boolean {
        val level = p.getLevel()
        for (y in p.floorY - 1..p.floorY) {
            for (x in p.floorX - 1..p.floorX + 1) {
                for (z in p.floorZ - 1..p.floorZ + 1) {
                    val block = level.getBlock(x,y,z)
                    if (block is BlockStairs) {
                        return true
                    }
                }
            }
        }
        for (block in p.getBlocksAround()) {
            if (block is BlockStairs) {
                return true
            }
        }
        for (block in p.getCollisionBlocks()) {
            if (block is BlockStairs) {
                return true
            }
        }
        return false
    }

}