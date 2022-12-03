package top.wetabq.wac

import cn.nukkit.entity.data.Skin
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.TextFormat
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.WACModuleManager
import top.wetabq.wac.module.exception.ModuleNotRegisterException
import top.wetabq.wac.utils.MetricsLite
import java.util.*
import javax.imageio.ImageIO

/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class WAntiCheatPro : PluginBase() {

    companion object {
        lateinit var instance : WAntiCheatPro
        var TITLE : String = TextFormat.colorize("&6&lWAC &r&c» ")
        const val VERSION = "v1.0.0"
        lateinit var df: DefaultConfig
        lateinit var skin: Skin

        @JvmStatic
        fun translateMessage(str: String) : String {
            return TextFormat.colorize(str.replace("{WACTitle}",WAntiCheatPro.TITLE).replace("{enter}","\n"))
        }

    }

    lateinit var moduleManager: WACModuleManager

    override fun onLoad() {
        val dirPath = "skin.png"
        MetricsLite(this)
        val skinStream = this.javaClass.classLoader.getResourceAsStream(dirPath)
        val dskin = Skin()
        dskin.setSkinData(ImageIO.read(skinStream))
        skin = dskin
        instance = this
    }

    override fun onEnable() {
        moduleManager = WACModuleManager()
        moduleManager.registerPriorityLoading()
        val def = moduleManager.getModule(DefaultModuleName.DEFAULTCONFIG) as DefaultConfig?
        if (def is DefaultConfig) df = def else throw ModuleNotRegisterException("DefaultConfig module did not register")
        moduleManager.registerAllDefaultModule()
    }

    override fun onDisable() {
        moduleManager.disableAllModule()
    }

}