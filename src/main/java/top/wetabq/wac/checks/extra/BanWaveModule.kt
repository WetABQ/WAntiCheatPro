package top.wetabq.wac.checks.extra

import cn.nukkit.command.CommandSender
import cn.nukkit.scheduler.AsyncTask
import top.wetabq.wac.WAntiCheatPro
import top.wetabq.wac.checks.access.ViolationProcess
import top.wetabq.wac.command.CommandArgument
import top.wetabq.wac.command.WSubCommand
import top.wetabq.wac.config.ConfigPaths
import top.wetabq.wac.config.module.DefaultConfig
import top.wetabq.wac.module.group.GroupModule
import top.wetabq.wac.module.group.RegGroupModule


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
class BanWaveModule : GroupModule() {

    companion object {
        var banWave = HashMap<ViolationProcess<*>,Int>()
    }

    override fun <T> register(registry: T) {
        super.register(registry)
        if (registry is RegGroupModule) {
            registry
                    .addAsyncTask(object : AsyncTask() {
                        override fun onRun() {
                            while (true) {
                                synchronized(BanWaveModule.Companion::class) {
                                    for ((k,v) in (banWave.clone() as HashMap<*, *>)) {
                                        if (k is ViolationProcess<*> && v is Int) {
                                            if (v > 0) {
                                                banWave[k] = v - 1
                                            } else {
                                                k.executePunishment()
                                                banWave.remove(k)
                                            }
                                        }
                                    }
                                }
                                Thread.sleep(1000)
                            }
                        }
                    })
                    .addConfigPath(ConfigPaths.VIOLATION_DURATION_BANWAVE_MIN,60)
                    .addConfigPath(ConfigPaths.VIOLATION_DURATION_BANWAVE_MAX,60*5)
                    .addCommand(object : WSubCommand("startBanWave"){
                        override fun getAliases(): Array<String> {
                            return arrayOf("sbw","startbw","startBanWave")
                        }

                        override fun getArguments(): Array<CommandArgument>? {
                            return null
                        }

                        override fun getDescription(): String {
                            return "Start processing all BanWave players right away"
                        }

                        override fun execute(sender: CommandSender, label: String, args: Array<out String>, df: DefaultConfig): Boolean {
                            if(sender.isOp) {
                                banWave.keys.forEach { vp -> vp.executePunishment() }
                                banWave.clear()
                                banWave = HashMap()
                                sender.sendMessage(WAntiCheatPro.TITLE + "Successful execution")
                            }
                            return true
                        }

                    })
                    .registerCommands()
                    .setModuleName("BanWaveModule")
                    .setModuleAuthor("WetABQ")
                    .context()
        }
    }

}