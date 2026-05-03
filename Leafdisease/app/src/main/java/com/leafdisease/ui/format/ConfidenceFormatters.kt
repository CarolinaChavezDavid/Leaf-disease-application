package com.leafdisease.ui.format

import java.util.Locale

fun Float.toPercentageString(decimals: Int = 1): String {
    return String.format(Locale.getDefault(), "%.${decimals}f%%", this * 100)
}
