package be.rlab.freire.animation.support

import be.rlab.freire.animation.*
import be.rlab.freire.animation.AnimationFigure.Companion.figure
import be.rlab.freire.animation.Rect.Companion.rect
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CellUtilsTest {
    companion object {
        @JvmStatic
        fun figureToSceneArguments() = listOf(
            FigureToSceneArgument(
                figureRect = rect(top = -4, left = -9, height = 4, width = 8),
                sceneRect = rect(top = -3, left = -7, height = 20, width = 5),
                regionWidth = 5,
                result = """
                      ( '
                     /  )
                    (__)|
                """.trimIndent()
            ),
            FigureToSceneArgument(
                figureRect = rect(top = 7, left = 1, height = 4, width = 8),
                sceneRect = rect(top = 0, left = 0, height = 10, width = 8),
                regionWidth = 7,
                result = """
                    \    /\
                     )  ( '
                    (  /  )
                """.trimIndent()
            ),
            FigureToSceneArgument(
                figureRect = rect(top = 1, left = 1, height = 4, width = 8),
                sceneRect = rect(top = 0, left = 0, height = 10, width = 10),
                regionWidth = 8,
                result = """
                    \    /\ 
                     )  ( ')
                    (  /  ) 
                     \(__)| 
                """.trimIndent()
            )
        )
    }

    data class FigureToSceneArgument(
        val figureRect: Rect,
        val sceneRect: Rect,
        val regionWidth: Int,
        val result: String
    )

    @Test
    fun extractRegion() {
        val cat = readResource("ascii/character.txt")
        val matrix = Cell.frameFrom(cat, 4, 8)
        val region = SceneUtils.extractRegion(matrix, width = 8, rect(top = 2, left = 3, 5, 2))
        assert(Cell.toString(region, 5) == """
            /  ) 
            __)| 
        """.trimIndent())
    }

    @ParameterizedTest
    @MethodSource("figureToSceneArguments")
    fun figureToScene(arg: FigureToSceneArgument) {
        val cat = readResource("ascii/character.txt")
        val figure: AnimationFigure = figure("cat", arg.figureRect) { draw { cat } }.draw(cat)
        val frame = Frame.new(arg.sceneRect, emptyList(), deltaTime = 0.0)
//        val region = PixelUtils.figureToScene(figure, frame)
//        assert(Pixel.toString(region.pixels, arg.regionWidth) == arg.result)
    }
}
