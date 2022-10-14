package journey.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.BufferedInputStream;
import java.util.Objects;

/**
 * Controller for help pages.
 */
public class HelpController {
    @FXML private javafx.scene.image.ImageView helpImage;
    @FXML private ChoiceBox<String> helpBoxSearch;
    private Stage stage;

    private static final ObservableList<String> helpOptions =
            FXCollections.observableArrayList("Search and Filter", "Record Notes", "Plan a Journey",
                    "Planned Journeys", "Register a Vehicle");

    /**
     * fills helpBox with available help, also adds a listener so change of help -> change of help image.
     */
    private void fillHelp() {
        helpBoxSearch.setItems(helpOptions);
        helpBoxSearch.setValue("Search and Filter");
        helpBoxSearch.getSelectionModel().selectedItemProperty().addListener(((
                ObservableValue<? extends String> observable, String oldValue, String newValue) -> changeImage(newValue)
                ));
    }


    /**
     * Sets help image to a particular image.

     * @param path image path
     */
    private void setHelpImage(String path) {
        Image img = new Image(
            new BufferedInputStream(
                Objects.requireNonNull(getClass().getResourceAsStream(path))
            ));
        helpImage.setImage(img);
        helpImage.fitWidthProperty().bind(stage.widthProperty());
        helpImage.fitHeightProperty().bind(stage.heightProperty());
    }
    /**
     * shows help image based on change in the helpBox.

     * @param image image required to match helpBox->helpImage.
     */
    private void changeImage(String image) {
        switch (image) {
            default -> setHelpImage("");

            case "Search and Filter" -> setHelpImage("/pictures/Search.jpg");

            case "Record Notes" -> setHelpImage("/pictures/Notes.jpg");

            case "Plan a Journey" -> setHelpImage("/pictures/PlanJourney.jpg");

            case "Planned Journeys" -> setHelpImage("/pictures/Planned.jpg");

            case "Register a Vehicle" -> setHelpImage("/pictures/RegisterVehicle.jpg");
        }
    }

    /**
     * initialises help screen, sets search help as initial image.

     * @param helpStage current stage
     */
    void init(Stage helpStage) {
        this.stage = helpStage;
        setHelpImage("/images/Search.jpg");
        fillHelp();
    }


}
