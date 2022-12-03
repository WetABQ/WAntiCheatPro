package top.wetabq.wac.checks.access


/**
 * WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
enum class ViolationType(private val type: String) {
    WARNING("WARNING"),
    SETBACK("SETBACK"),
    KICK("KICK"),
    MUTE("MUTE"),
    BAN("BAN"),
    NONE("none");

    companion object {
        @JvmStatic
        fun fromTypeName(typeName: String): ViolationType? {
            for (type in ViolationType.values()) {
                if (type.type == typeName) {
                    return type
                }
            }
            return null
        }
    }

    override fun toString(): String {
        return this.type
    }

}