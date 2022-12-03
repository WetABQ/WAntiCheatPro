package top.wetabq.wac.checks


/**
 * top.wetabq.wac.WAntiCheatPro
 *
 * @author WetABQ Copyright (c) 2019.06
 * @version 1.0
 */
enum class CheckType(private val type: CheckTypeType,private val parent: CheckType?) {

    ALL(CheckTypeType.SPECIAL, null),

    BLOCKBREAK(CheckTypeType.GROUP, CheckType.ALL),
    BLOCKBREAK_FASTBREAK(CheckTypeType.CHECK, BLOCKBREAK),
    BLOCKBREAK_REACH(CheckTypeType.CHECK, BLOCKBREAK),
    BLOCKBREAK_WRONGBREAK(CheckTypeType.CHECK, BLOCKBREAK),
    BLOCKBREAK_HIDDENMINE(CheckTypeType.SPECIAL, BLOCKBREAK),

    BLOCKPLACE(CheckTypeType.GROUP, CheckType.ALL),
    BLOCKPLACE_WRONGPLACE(CheckTypeType.CHECK, BLOCKPLACE),
    BLOCKPLACE_REACH(CheckTypeType.CHECK, BLOCKPLACE),

    CHAT(CheckTypeType.GROUP, CheckType.ALL),
    CHAT_COLOR(CheckTypeType.CHECK, CHAT),
    CHAT_COMMANDS(CheckTypeType.CHECK, CHAT),
    CHAT_TEXT(CheckTypeType.CHECK, CHAT),

    FIGHT(CheckTypeType.CHECK, CheckType.ALL),
    FIGHT_WRONGATTACK(CheckTypeType.CHECK, FIGHT),
    FIGHT_CRITICAL(CheckTypeType.CHECK, FIGHT),
    FIGHT_REACH(CheckTypeType.CHECK, FIGHT),
    FIGHT_AUTOAIM(CheckTypeType.CHECK, FIGHT),

    MOVING(CheckTypeType.GROUP, CheckType.ALL),
    MOVING_MOREPACKETS(CheckTypeType.CHECK, MOVING),
    MOVING_NOFALL(CheckTypeType.CHECK, MOVING),
    MOVING_SURVIVALFLY(CheckTypeType.CHECK, MOVING),
    MOVING_SPEED(CheckTypeType.GROUP, MOVING),
    MOVING_HIGHJUMP(CheckTypeType.CHECK, MOVING),
    MOVING_THROUGHWALL(CheckTypeType.CHECK, MOVING);

    enum class CheckTypeType {
        /** Special types, like ALL  */
        SPECIAL,
        /** Potentially obsolete: A check group that is not a check itself.  */
        GROUP,
        /** An actual check. Could in future still have sub checks.  */
        CHECK
    }

    fun getType(): CheckTypeType {
        return type
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    fun getName(): String {
        return this.toString().toLowerCase().replace("_", ".")
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    fun getParent(): CheckType? {
        return parent
    }

    companion object {
        @JvmStatic
        fun fromTypeName(typeName: String): CheckType? {
            for (type in CheckType.values()) {
                if (type.getName() == typeName) {
                    return type
                }
            }
            return null
        }
    }

}