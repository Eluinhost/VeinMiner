package gg.uhc.veinminer

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class DigListener(val traverser: VeinTraverser, val multiBreakFor: Set<Material>, val allowedItems: Set<Material>) : Listener {
    private val RANDOM = Random()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        if (event.player.itemInHand.type !in allowedItems) return
        if (event.block.type !in multiBreakFor) return

        // Remove the block that is currently being broken to avoid double check
        val toBreak = traverser.getMatchingTypesAtLocation(event.block).minus(event.block)

        val item = event.player.itemInHand
        val canTakeDamage = item.type.maxDurability > 0
        val chance : Double = if (canTakeDamage) chanceToTakeDamage(item) else 0.0
        var tempDurability = item.durability

        for (block in toBreak) {
            if (canTakeDamage && tempDurability >= item.type.maxDurability) break

            block.breakNaturally(event.player.itemInHand)

            if (canTakeDamage && RANDOM.nextDouble() < chance) {
                tempDurability++
            }
        }

        if (canTakeDamage) {
            item.durability = tempDurability
        }
    }

    private fun chanceToTakeDamage(item: ItemStack): Double = 1.0
}
