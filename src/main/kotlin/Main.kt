import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class GanttChartExample : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.title = "Gantt Chart Example"

        val xAxis = NumberAxis()
        val yAxis = CategoryAxis()

        val chart = BarChart<Number, String>(xAxis, yAxis)
        val series1 = XYChart.Series<Number, String>()
        val mapSeries = mutableMapOf("Task 1" to 1, "Task 2" to 2)

        xAxis.isAutoRanging = false
        xAxis.upperBound = 52.0
        xAxis.tickUnit = 1.0
        series1.name = mapSeries.map { entry -> entry.key }.toString()
        println(series1.name.toString())

        val data1 = mapSeries.map { (entry, value) -> XYChart.Data<Number, String>(value, entry) }

        //val data1 = XYChart.Data<Number, String>(1, "Task 1")
        // Funktion, um die Werte auf die nächstgelegenen Vielfachen von gridSize zu runden
        fun roundToGrid(value: Double, gridSize: Double): Double {
            return Math.round(value / gridSize) * gridSize
        }

        data1.forEach { data ->
            val rectangle = Rectangle(30.0, 10.0)
            var dragDeltaX = 0.0
            var dragDeltaY = 0.0
            val gridSize = 10.0 // Beispielwert für gridSize

            rectangle.onMouseClicked = EventHandler { click ->
                if (click.clickCount == 2) {
                    // Setze einen separaten Drag-Handler für die Größenänderung
                    rectangle.setOnMouseDragged { event ->
                        // Größe auf das Raster anpassen
                        rectangle.width = roundToGrid(event.x, gridSize)
                        rectangle.height = roundToGrid(event.y, gridSize)
                    }
                } else if (click.clickCount == 1) {
                    // Entferne den Drag-Handler für die Größenänderung
                    rectangle.setOnMouseDragged(null)
                    // Setze den Drag-Handler für die Positionsänderung zurück
                    rectangle.setOnMouseDragged { mouseEvent ->
                        rectangle.translateX = roundToGrid(mouseEvent.sceneX - dragDeltaX, gridSize)
                        rectangle.translateY = roundToGrid(mouseEvent.sceneY - dragDeltaY, gridSize)
                    }
                }
            }

            rectangle.setOnMousePressed { mouseEvent ->
                println("Drag Activated")
                dragDeltaX = mouseEvent.sceneX - rectangle.translateX
                dragDeltaY = mouseEvent.sceneY - rectangle.translateY
            }

            // Initialer Drag-Handler für die Positionsänderung
            rectangle.setOnMouseDragged { mouseEvent ->
                rectangle.translateX = roundToGrid(mouseEvent.sceneX - dragDeltaX, gridSize)
                rectangle.translateY = roundToGrid(mouseEvent.sceneY - dragDeltaY, gridSize)
            }

            data.node = rectangle
        }








        series1.data.addAll(data1)

        chart.data.addAll(series1)

        val scene = Scene(chart, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()
    }
}

// Funktion, um die Werte auf die nächstgelegenen Vielfachen von gridSize zu runden
fun roundToGrid(value: Double, gridSize: Double): Double {
    return Math.round(value / gridSize) * gridSize
}

fun main() {
    Application.launch(GanttChartExample::class.java)
}