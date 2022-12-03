package top.wetabq.wac.checks

import cn.nukkit.Player
import cn.nukkit.event.Event
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.logging.module.DebugLog
import top.wetabq.wac.module.DefaultModuleName
import top.wetabq.wac.module.WACModule
import top.wetabq.wac.module.exception.ModuleNotRegisterException
import top.wetabq.wac.module.exception.RegModuleException
import top.wetabq.wac.module.group.RegGroupContext
import top.wetabq.wac.module.group.RegGroupModule
import java.lang.reflect.ParameterizedType



/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class Check<out D : CheckData> : WACModule<RegGroupModule,RegGroupContext>() {

    companion object {
        val playerCheckDataMap = HashMap<String,HashMap<Check<*>,CheckData>>()

        @JvmStatic
        fun getPlayerCheatData(checkType: CheckType,player: Player): CheckData? {
            var checkData: CheckData? = null
            playerCheckDataMap[player.name]?.let {
                for ((k, v) in it) {
                    if (k.getCheckType() == checkType) checkData = v
                }
            }
            return checkData
        }

    }

    override fun getRegisterInstance(): RegGroupModule {
        return RegGroupModule(this)
    }

   //abstract fun getPlayerCheckData(player: Player) : D

    fun createPlayerCheckData(player:Player) : D {
        return createPlayerCheckData(player,WAntiCheatPro.df)
    }

    //abstract fun createPlayerCheckData(player: Player,df: DefaultConfig) : D

    open fun getPlayerCheckData(player: Player): CheckData {
        return (Check.playerCheckDataMap[player.name]?: let{
            it.createPlayerCheckData(player)
            Check.playerCheckDataMap[player.name]!!
        })[this]?:let {
            it.createPlayerCheckData(player)
            Check.playerCheckDataMap[player.name]!![this]!!
        }
    }

    open fun createPlayerCheckData(player: Player,df: DefaultConfig): D {
        val genType = javaClass.genericSuperclass
        val params = (genType as ParameterizedType).actualTypeArguments
        val entityClass = params[0] as Class<D>
        val cons = entityClass.getConstructor(Player::class.java, Check::class.java,DefaultConfig::class.java)
        val obj = cons.newInstance(player,this,df)
        val a =  Check.playerCheckDataMap[player.name]?: let{
            Check.playerCheckDataMap[player.name] = hashMapOf()
            Check.playerCheckDataMap[player.name]!!
        }
        a[this] = obj
        return obj
    }

    fun checkCheat(player: Player,checkData: CheckData,event: Event?) {
        if (checkData.doCheck(getCheckType())) {
            checkCheat(player, checkData, event, WAntiCheatPro.df)
        }
    }

    open fun checkCheat(player: Player,checkData: CheckData,event: Event?,df : DefaultConfig): Boolean {
        if (!df.defaultConfig[ConfigPaths.CHECKS + this.javaClass.name.toLowerCase() + ConfigPaths.CHECKS_SWITCH].toString().toBoolean()) return false
        return true
    }

    fun checkDebug(player: Player,message: String) {
        val debugLog = getWACInstance().moduleManager.getModule(DefaultModuleName.DEBUGLOG) as DebugLog?
        if (debugLog is DebugLog) {
            debugLog.debug(player,message)
        } else throw ModuleNotRegisterException("DebugLog module did not register")
    }

    abstract fun getCheckType(): CheckType

    override fun <T> register(registry: T) {
        if (registry is RegGroupModule) else throw RegModuleException(this,"registry isn't RegGroupModule")
    }

    override fun context(registerContext: RegGroupContext) {
        this.context = registerContext
    }

}