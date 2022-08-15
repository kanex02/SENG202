package journey.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the main window
 * @author seng202 teaching team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    @FXML private ChoiceBox chargerBox;
    @FXML private TextField registrationTextBox;
    @FXML private Button viewPrevJourneys;
    @FXML private ComboBox filterList;
    @FXML private ComboBox sortList;
    @FXML private AnchorPane scrollPane_inner;
    @FXML private Button recordJourneyStartButton;
    @FXML private Button recordJourneyEndButton;
    @FXML private TextField chargingStationTextField;
    @FXML private TextArea stationDetailTextArea;

    @FXML private void userDropdown(Event event) {
        log.info("User dropdown button pressed!");
    }


    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
//    public void init(Stage stage) {
//    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {
        log.info("Button has been clicked");
    }
}
