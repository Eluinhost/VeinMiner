package gg.uhc.veinminer

import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class Entry : JavaPlugin() {
    override fun onEnable() {
        config.options().copyDefaults(true)
        saveConfig()

        val traverser = VeinTraverser(config.getInt("max amount"))

        val toBreak = config.getStringList("break types").mapNotNull { convertToMaterial(it) }.toSet()
        val toBreakFor = config.getStringList("break items").mapNotNull { convertToMaterial(it) }.toSet()

        val listener = DigListener(traverser, toBreak, toBreakFor)
        server.pluginManager.registerEvents(listener, this)
    }

    fun convertToMaterial(name: String) : Material? = try {
        Material.valueOf(name.toUpperCase().trim().replace(" ", "_"))
    } catch (ex: IllegalArgumentException) {
        logger.warning("$name is not a valid material type")
        null
    }
}
