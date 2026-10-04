package io.github.sds100.keymapper.base.constraints

enum class CompareStateValueOperatorEnum(val operator: String) {
    NONE(""),
    IS("is"),
    IS_NOT("is not"),
    EQUAL_TO("=="),
    NOT_EQUAL_TO("!="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">=");

    companion object {
        fun fromOperator(value: String): CompareStateValueOperatorEnum {
            try {
                return CompareStateValueOperatorEnum.valueOf(value)
            } catch (_: IllegalArgumentException) {
                for (enumVal in entries) {
                    if (enumVal.operator == value) {
                        return enumVal
                    }
                }
                throw IllegalArgumentException("No enum for $value")
            }
        }
    }
}