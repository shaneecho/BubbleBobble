import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by toinehartman on 01/09/15.
 */

public class StartController implements Initializable {
    /**
     * The @FXML annotation links the view element to this object in the controller.
     * The variable name of the object has to match the fx:id of the view element.
     */
    @FXML private GridPane root;

    /**
     * The ImageView is the logo that is shown.
     */
    @FXML private ImageView imageView;

    /**
     * The start button. If you press this the game will start.
     */
    @FXML private Button startButton;

    /**
     * The exit button. If you press this the application will close.
     */
    @FXML private Button exitButton;

    /**
     * The help button. If you press this you will be shown some text that should help you.
     */
    @FXML private Button helpButton;

    /**
     * Initializes the view.
     *
     * This is the place for setting onclick handlers, for example.
     */
    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        startButton.setOnAction(event -> {
            root.visibleProperty().setValue(false);
        });
        helpButton.setOnAction((event -> {
            System.out.println("Help!");
        }));
        exitButton.setOnAction((event -> {
            System.out.println("Exit!");
        }));
    }

    /**
     * This method is run when the view is shown.
     *
     * This is where data can be put in tables, for example.
     */
    public void show() {
    }

}
