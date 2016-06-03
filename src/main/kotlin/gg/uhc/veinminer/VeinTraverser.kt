package gg.uhc.veinminer

import com.google.common.collect.Lists
import org.bukkit.Material
import org.bukkit.block.Block

class VeinTraverser(val maxSize: Int) {
    fun convertType(type: Material) = when(type) {
        Material.GLOWING_REDSTONE_ORE -> Material.REDSTONE_ORE
        else -> type
    }

    fun getMatchingTypesAtLocation(block: Block): Set<Block> {
        val type = convertType(block.type)

        val matching = mutableSetOf(block)

        // FIFO list of blocks awaiting checks
        val waitingForChecks = Lists.newLinkedList<Block>()
        waitingForChecks.add(block)

        var current: Block
        var check: Block?

        while (waitingForChecks.size > 0) {
            // remove the first item in the list
            current = waitingForChecks.poll()

            // for every surrounding block
            for (x in -1..1) {
                for (y in -1..1) {
                    for (z in -1..1) {
                        // get block at the location
                        check = current.getRelative(x, y, z)

                        // skip outside world and wrong types
                        if (check == null || type != convertType(check.type)) continue

                        // add to matching set
                        if (matching.add(check)) {
                            // if the location wasn't already found add to list and quit if we're at max size
                            waitingForChecks.add(check)
                            if (matching.size == maxSize) {
                                return matching
                            }
                        }
                    }
                }
            }
        }

        return matching
    }
}
