package com.jarabrama.average.repository.impl

import android.content.Context
import android.util.Log
import com.jarabrama.average.model.Settings
import com.jarabrama.average.repository.SettingsRepository
import java.io.File
import java.io.IOException
import java.lang.Double.parseDouble
import java.nio.charset.Charset

class SettingsRepositoryFileBased(private val context: Context) : SettingsRepository {
    private val fileAddress = "settings.txt"
    private val separator = "|"

    init {
        try {
            val file = File(context.filesDir, fileAddress)
            if (!file.exists()) {
                val created = file.createNewFile()
                if (created) {
                    val settings = Settings()
                    context.openFileOutput(fileAddress, Context.MODE_PRIVATE)
                        .bufferedWriter(Charset.defaultCharset()).use {
                            it.write(toText(settings))
                        }
                    Log.i("SettingsRepositoryFileBased: init", "file created: $fileAddress")
                }
            }
        } catch (e: IOException) {
            Log.e("SettingsRepositoryFileBased", e.message, e)
        }
    }

    override fun getSettings(): Settings {
        this.context.openFileInput(fileAddress).bufferedReader().use {
            val text = it.readLine()
            return toSettings(text = text)
        }
    }

    override fun setSettings(settings: Settings): Settings {
        this.context.openFileOutput(fileAddress, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(toText(settings))
        }
        return getSettings()
    }

    private fun toText(settings: Settings): String {
        return "${settings.maxQualification}$separator${settings.minQualification}$separator${settings.goal}"
    }

    private fun toSettings(text: String): Settings {
        val attributes = text.split("|")
        return Settings(
            parseDouble(attributes[0]),
            parseDouble(attributes[1]),
            parseDouble(attributes[2])
        )
    }
}