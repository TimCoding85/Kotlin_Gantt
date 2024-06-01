import androidx.compose.animation.slideOutVertically
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlin.math.absoluteValue

class GanttChartExample : Application() {


    override fun start(primaryStage: Stage) {



        val pane = Pane(canvas())





    }
    private fun canvas():Canvas{
        val canvas = Canvas()
        val gc = canvas.graphicsContext2D
        gc.fill=Color.web("Color.BLUE")



        return canvas
    }
}