package top.wetabq.wac.module.reg

import top.wetabq.wac.module.ModuleVersion
import top.wetabq.wac.module.WACModule


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
abstract class RegisterModule(private val module: WACModule<*, *>) {

    protected var context = RegisterContext()

    fun setModuleName(name: String): RegisterModule {
        context.moduleName = name
        return this
    }

    fun setModuleAuthor(name: String): RegisterModule {
        context.moduleAuthor = name
        return this
    }

    fun setModuleVersion(moduleVersion: ModuleVersion): RegisterModule {
        context.moduleVersion = moduleVersion
        return this
    }

    abstract fun context() //Must use

}