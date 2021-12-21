package be.rlab.freire.animation

class Scene(
    private val screen: Screen,
    val rect: Rect,
    val fps: Int
) {
    companion object {
        const val DEFAULT_FPS: Int = 24

        fun new(
            screen: Screen,
            rect: Rect,
            fps: Int = DEFAULT_FPS
        ): Scene = Scene(
            screen,
            rect,
            fps
        )
    }
    private var playing: Boolean = false
    /** List of registered components. */
    private val components: MutableList<SceneComponent> = mutableListOf()

    fun register(component: SceneComponent): Scene = apply {
        components += component
    }

    fun stop() {
        require(playing) { "the scene is not playing" }
        playing = false
    }

    fun play() {
        require(!playing) { "the scene is already playing" }

        screen.attach()

        val frameInterval: Long = (1000 / fps).toLong()
        var previousTime: Long = System.currentTimeMillis()

        playing = true

        components.forEach { component ->
            component.start(this)
        }

        while (playing) {
            val currentTime: Long = System.currentTimeMillis()
            var deltaTime: Double = (currentTime - previousTime).toDouble()

            if (deltaTime >= frameInterval) {
                deltaTime = frameInterval.toDouble()
                previousTime = System.currentTimeMillis()

                // Update
                val nextFrame: Frame = Frame.new(
                    rect,
                    components.filterIsInstance<AnimationFigure>(),
                    deltaTime
                ).update()

                // Render
                nextFrame.render()
                screen.draw(0, 0, nextFrame.viewport.cells)
                screen.flush()
            }
        }
    }
}
