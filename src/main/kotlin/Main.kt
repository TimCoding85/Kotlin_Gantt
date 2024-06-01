import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

data class GanttBar(
    var x: Double,
    var y: Double,
    var width: Double,
    val height: Double,
    val project: String,
    val startDate: LocalDate
) : java.io.Serializable

class GanttChart : Application() {
    private val projects = mutableListOf("Projekt A", "Projekt B", "Projekt C")
    private var bars = mutableListOf(
        GanttBar(50.0, 50.0, 100.0, 30.0, "Projekt A", LocalDate.of(2024, 5, 1)),
        GanttBar(200.0, 100.0, 150.0, 30.0, "Projekt B", LocalDate.of(2024, 5, 15)),
        GanttBar(400.0, 150.0, 120.0, 30.0, "Projekt C", LocalDate.of(2024, 6, 1))
    )
    private var selectedBar: GanttBar? = null
    private var dragStartX: Double = 0.0
    private var dragStartY: Double = 0.0
    private var projectHeight = 50.0
    private val startDate = LocalDate.of(2024, 5, 1) // Startdatum für die Berechnung der x-Koordinate
    private var isEditing: Boolean = false

    override fun start(primaryStage: Stage) {
        val canvas = Canvas(2000.0, 600.0)
        val gc = canvas.graphicsContext2D
        val pane = Pane(canvas)
        val scrollPane = ScrollPane(pane)

        val saveButton = Button("Speichern")
        val loadButton = Button("Laden")
        val addButton = Button("Projekt hinzufügen")

        saveButton.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Serialized Files", "*.ser"))
            val file = fileChooser.showSaveDialog(primaryStage)
            if (file != null) {
                saveBarsToFile(file)
            }
        }

        loadButton.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Serialized Files", "*.ser"))
            val file = fileChooser.showOpenDialog(primaryStage)
            if (file != null) {
                loadBarsFromFile(file)
                drawBars(gc)
            }
        }

        addButton.setOnAction {
            showAddProjectDialog(primaryStage, gc)
        }

        val hbox = HBox(10.0, saveButton, loadButton, addButton)
        hbox.padding = Insets(10.0)
        val vbox = VBox(10.0, scrollPane, hbox)
        vbox.padding = Insets(10.0)

        drawBars(gc)

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            if (event.clickCount == 2) {
                for (bar in bars) {
                    if (event.x >= bar.x && event.x <= bar.x + bar.width && event.y >= bar.y && event.y <= bar.y + bar.height) {
                        selectedBar = bar
                        isEditing = true
                        dragStartX = event.x
                        break
                    }
                }
            } else if (event.clickCount == 1) {
                if (isEditing) {
                    isEditing = false
                } else {
                    for (bar in bars) {
                        if (event.x >= bar.x && event.x <= bar.x + bar.width && event.y >= bar.y && event.y <= bar.y + bar.height) {
                            selectedBar = bar
                            dragStartX = event.x - bar.x
                            dragStartY = event.y - bar.y
                            break
                        }
                    }
                }
            }
        }

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED) { event ->
            selectedBar?.let {
                if (isEditing) {
                    it.width = Math.max(20.0, event.x - it.x)
                } else {
                    it.x = event.x - dragStartX
                    it.y = clamp(event.y - dragStartY, getProjectY(it.project), getProjectY(it.project) + projectHeight - it.height)
                }
                drawBars(gc)
            }
        }

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED) {
            selectedBar = null
        }

        primaryStage.scene = Scene(vbox)
        primaryStage.title = "Gantt Diagram"
        primaryStage.show()

        primaryStage.widthProperty().addListener { _, _, _ -> drawBars(gc) }
        primaryStage.heightProperty().addListener { _, _, _ -> drawBars(gc) }
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        return Math.max(min, Math.min(max, value))
    }

    private fun getProjectY(project: String): Double {
        val index = projects.indexOf(project)
        return 50.0 + index * projectHeight
    }

    private fun calculateX(startDate: LocalDate): Double {
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(this.startDate, startDate)
        return 50.0 + daysBetween * 20.0 // 20.0 ist die Breite eines Tages im Diagramm
    }

    private fun showAddProjectDialog(owner: Stage, gc: GraphicsContext) {
        val dialog = Stage()
        dialog.initModality(Modality.APPLICATION_MODAL)
        dialog.initOwner(owner)
        dialog.title = "Projekt hinzufügen"

        val projectField = TextField()
        val startDateField = TextField()
        val durationField = TextField()
        val addButton = Button("Hinzufügen")

        val gridPane = GridPane()
        gridPane.add(Label("Projektname:"), 0, 0)
        gridPane.add(projectField, 1, 0)
        gridPane.add(Label("Startdatum (yyyy-mm-dd):"), 0, 1)
        gridPane.add(startDateField, 1, 1)
        gridPane.add(Label("Dauer (Tage):"), 0, 2)
        gridPane.add(durationField, 1, 2)
        gridPane.add(addButton, 1, 3)
        gridPane.padding = Insets(10.0)
        gridPane.hgap = 10.0
        gridPane.vgap = 10.0

        addButton.setOnAction {
            val projectName = projectField.text
            val startDate = LocalDate.parse(startDateField.text, DateTimeFormatter.ISO_DATE)
            val duration = durationField.text.toDouble()
            val newY = (projects.size * projectHeight) + 50.0

            if (projectName.isNotEmpty() && duration > 0) {
                projects.add(projectName)
                val newBar = GanttBar(calculateX(startDate), newY, duration * 20.0, 30.0, projectName, startDate)
                bars.add(newBar)
                drawBars(gc)
                dialog.close()
            }
        }

        dialog.scene = Scene(gridPane)
        dialog.showAndWait()
    }

    private fun drawBars(gc: GraphicsContext) {
        gc.clearRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
        drawGrid(gc)
        drawTodayLine(gc) // Neue Linie für den heutigen Tag
        gc.stroke = Color.BLACK
        gc.lineWidth = 2.0

        for (bar in bars) {
            gc.fill = Color.LIGHTBLUE
            gc.fillRect(bar.x, bar.y, bar.width, bar.height)
            gc.strokeRect(bar.x, bar.y, bar.width, bar.height)
        }
    }

    private fun drawGrid(gc: GraphicsContext) {
        val daysInWeek = 7
        val cellWidth = 20.0
        val cellHeight = projectHeight
        val startDate = LocalDate.of(2024, 5, 1)
        val endDate = startDate.plusMonths(6)

        gc.stroke = Color.LIGHTGRAY
        gc.lineWidth = 1.0

        // Draw vertical lines for days and weeks
        var x = 50.0
        var date = startDate
        while (date.isBefore(endDate)) {
            gc.strokeLine(x, 0.0, x, gc.canvas.height)
            if (date.dayOfWeek == WeekFields.of(Locale.getDefault()).firstDayOfWeek) {
                gc.stroke = Color.BLACK
                gc.strokeLine(x, 0.0, x, gc.canvas.height)
                gc.stroke = Color.LIGHTGRAY
            }
            x += cellWidth
            date = date.plusDays(1)
        }

        // Draw horizontal lines for projects
        var y = 50.0
        for (project in projects) {
            gc.strokeLine(0.0, y, gc.canvas.width, y)
            y += cellHeight
        }

        // Draw project labels
        y = 50.0
        for (project in projects) {
            gc.strokeText(project, 10.0, y + cellHeight / 2)
            y += cellHeight
        }

        // Draw day labels
        x = 50.0
        date = startDate
        while (date.isBefore(endDate)) {
            gc.strokeText(date.dayOfMonth.toString(), x, 40.0)
            if (date.dayOfWeek == WeekFields.of(Locale.getDefault()).firstDayOfWeek) {
                gc.strokeText("KW" + date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()), x, 20.0)
            }
            x += cellWidth
            date = date.plusDays(1)
        }
    }

    private fun drawTodayLine(gc: GraphicsContext) {
        val today = LocalDate.now()
        val x = calculateX(today)
        gc.stroke = Color.YELLOW
        gc.lineWidth = 1.0
        gc.strokeLine(x, 0.0, x, gc.canvas.height)
    }

    private fun saveBarsToFile(file: File) {
        ObjectOutputStream(file.outputStream()).use { oos ->
            oos.writeObject(bars)
        }
    }

    private fun loadBarsFromFile(file: File) {
        ObjectInputStream(file.inputStream()).use { ois ->
            bars = ois.readObject() as MutableList<GanttBar>
            // Update project list
            projects.clear()
            projects.addAll(bars.map { it.project }.distinct())
        }
    }
}

fun main() {
    Application.launch(GanttChart::class.java)
}
