package be.rlab.freire.animation

import java.util.*

data class Region(
    val id: UUID,
    val source: Viewport,
    val target: Viewport,
    private var dirty: Boolean
) {
    companion object {
        fun new(
            source: Viewport,
            target: Viewport
        ): Region = Region(
            id = UUID.randomUUID(),
            source,
            target,
            dirty = false
        )
    }

    fun render(): Region = apply {
        if (dirty || source.isDirty()) {
            target.draw(source.cells)
            source.clean()
        }
    }

    fun markAsDirty() {
        dirty = true
    }
}
