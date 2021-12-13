package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private fun setStart(keyX: String, keyY: String, x: Int, y: Int) {
        preferences.edit()
            .putInt(keyX, x)
            .putInt(keyY, y)
            .apply()
    }

    fun getStart(keyX: String, keyY: String): Array<Int> {
        val x = preferences.getInt(keyX, 0)
        val y = preferences.getInt(keyY, 0)
        return arrayOf(x, y)
    }

    private companion object {
        private const val PREFERENCES_NAME = "PREFS"
    }
}
