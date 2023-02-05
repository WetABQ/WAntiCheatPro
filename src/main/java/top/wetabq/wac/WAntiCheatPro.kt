package top.wetabq.wac

import cn.nukkit.entity.data.Skin
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.TextFormat
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.WACModuleManager
import top.wetabq.wac.module.exception.ModuleNotRegisterException
import top.wetabq.wac.utils.MetricsLite
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
        lateinit var protocolType: ProtocolType

        @JvmStatic
        fun translateMessage(str: String) : String {
            return TextFormat.colorize(str.replace("{WACTitle}",TITLE).replace("{enter}","\n"))
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
        this.verifyProtocolType()
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

    private fun verifyProtocolType() {
        protocolType = try {
            val clazz = Class.forName("cn.nukkit.Nukkit")
            clazz.getField("NUKKIT_PM1E")
            if (this.server.getPropertyBoolean("server-authoritative-block-breaking") ||
                this.server.getPropertyString("server-authoritative-movement") == "server-auth")
                ProtocolType.SERVER_AUTH else ProtocolType.CLIENT_AUTH
        } catch (exception : NoSuchFileException) {
            ProtocolType.SERVER_AUTH
        }
    }

    enum class ProtocolType {
        CLIENT_AUTH, SERVER_AUTH
    }

}