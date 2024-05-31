import androidx.compose.animation.slideOutVertically
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
import kotlin.math.absoluteValue

class GanttChartExample : Application() {


    override fun start(primaryStage: Stage) {
        primaryStage.title = "Gantt Chart Example"
        var rectangleWidth = 30.0
        var rectangleHeight = 10.0
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
            val rectangle = Rectangle(rectangleWidth, rectangleHeight)
            var dragDeltaX = 0.0
            var dragDeltaY = 0.0
            val gridSize = 10.0 // Beispielwert für gridSize

            rectangle.widthProperty().addListener { _, _, newValue ->
                rectangleWidth = newValue.toDouble()
            }

            rectangle.heightProperty().addListener { _, _, newValue ->
                rectangleHeight = newValue.toDouble()
            }

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
                        data.xValue = rectangle.translateX.absoluteValue / 10
                        print(data.xValue)
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




        data1.forEach { data ->
            val rectangle = data.node as Rectangle
            rectangle.translateX =
                data.xValue.toDouble() * 10 // Multiplizieren Sie mit 10, um die Position auf der X-Achse zu erhöhen
            data.node = rectangle
        }



        series1.data.addAll(data1)

        chart.data.addAll(series1)

        val scene = Scene(chart, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()


    }


    // Funktion, um die Werte auf die nächstgelegenen Vielfachen von gridSize zu runden
    fun roundToGrid(value: Double, gridSize: Double): Double {
        return Math.round(value / gridSize) * gridSize
    }
}