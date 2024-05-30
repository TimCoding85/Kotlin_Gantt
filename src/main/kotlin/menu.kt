import androidx.compose.ui.unit.dp
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.stage.Stage
import java.awt.Color
import java.awt.Insets
import java.awt.Rectangle


class Gantt : Application() {
    private var border = BorderPane()
    private lateinit var hBox: HBox
    override fun start(primaryStage: Stage) {

        hBox = addHBox()
        addStackPane()
        border.top = hBox

        val scene = Scene(border, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()


    }

    private fun addHBox(): HBox {
        val hBox = HBox()
        hBox.padding = javafx.geometry.Insets(15.0, 12.0, 15.0, 12.0)
        hBox.style = "-fx-background-color: #336699"
        hBox.spacing = 10.0
        val buCurrent = Button("Current")
        buCurrent.setPrefSize(100.0, 20.0)
        val buProject = Button("Projected")
        buProject.setPrefSize(100.0, 20.0)
        hBox.children.addAll(buCurrent, buProject)

        return hBox
    }

    private fun addStackPane(): HBox {
        val stack = StackPane()
        try {

            val helpIcon = javafx.scene.shape.Rectangle(30.0, 25.0)
            helpIcon.fill = LinearGradient(
                0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
                Stop(0.0, javafx.scene.paint.Color.web("#4977A3")),
                Stop(0.5, javafx.scene.paint.Color.web("#B0C6DA")),
                Stop(1.0, javafx.scene.paint.Color.web("#9CB6CF"))
            )

            stack.children.addAll(helpIcon)

            hBox.children.add(stack)
        } catch (e: Exception) {
            ("Failur: ${e.stackTrace}")
        }
        return hBox
    }


}