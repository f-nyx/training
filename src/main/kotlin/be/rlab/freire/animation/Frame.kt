package be.rlab.freire.animation

class Frame(
    val viewport: Viewport,
    val figures: List<AnimationFigure>,
    val deltaTime: Double
) {
    companion object {
        fun new(
            rect: Rect,
            figures: List<AnimationFigure>,
            deltaTime: Double
        ): Frame = Frame(Viewport.new(rect), figures, deltaTime)
    }

    fun update(): Frame = apply {
        figures
            .filter { figure -> figure.viewport in viewport }
            .forEach { figure -> viewport.map(figure.viewport.blank()) }
        figures.forEach { figure -> figure.update(this) }
    }

    fun render(): Frame = apply {
        figures
            .filter { figure -> figure.viewport in viewport }
            .map { figure -> figure.viewport.render() }
            .forEach { figureViewport -> viewport.map(figureViewport) }
        viewport.draw("v0.1").render()
    }

    fun findFigureByName(name: String): AnimationFigure? {
        return figures.find { figure -> figure.name == name }
    }
}
