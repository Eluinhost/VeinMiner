package gg.uhc.veinminer

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class DigListener(val traverser: VeinTraverser, val multiBreakFor: Set<Material>, val allowedItems: Set<Material>) : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        if (!allowedItems.contains(event.player.itemInHand.type)) return
        if (!multiBreakFor.contains(event.block.type)) return

        // Remove the block that is currently being broken to avoid double check
        val toBreak = traverser.getMatchingTypesAtLocation(event.block).minus(event.block)

        val item = event.player.itemInHand
        val canTakeDamage = item.type.maxDurability > 0
        val chance : Double = if (canTakeDamage) chanceToTakeDamage(item) else 0.0
        var tempDurability = item.durability

        for (block in toBreak) {
            // if it has dropped to 1 then stop breaking more, the block itself will
            // take the final durability after the event has ran
            if (canTakeDamage && tempDurability <= 1) break

            block.breakNaturally(event.player.itemInHand)

            if (canTakeDamage && RANDOM.nextDouble() < chance) {
                tempDurability--
            }
        }

        if (canTakeDamage) {
            item.durability = tempDurability
        }
    }

    protected fun chanceToTakeDamage(item: ItemStack): Double {
        return 1.0
    }

    companion object {
        protected val RANDOM = Random()
    }
}
