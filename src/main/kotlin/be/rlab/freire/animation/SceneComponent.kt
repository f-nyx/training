package be.rlab.freire.animation

abstract class SceneComponent {
    abstract val name: String
    open fun start(scene: Scene) {}
    open fun update(frame: Frame) {}
}
