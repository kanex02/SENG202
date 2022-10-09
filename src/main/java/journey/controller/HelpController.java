package journey.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedInputStream;

public class HelpController {
    @FXML private javafx.scene.image.ImageView helpImage;
    @FXML private ChoiceBox<String> helpBoxSearch;
    private Stage stage;
    private Image img;

    private static final ObservableList<String> helpOptions =
            FXCollections.observableArrayList("Search and Filter", "Record Notes", "Plan a Journey",
                    "Planned Journeys", "Register a Vehicle");

    private void fillHelp() {
        helpBoxSearch.setItems(helpOptions);
        helpBoxSearch.setValue("Search and Filter");
        helpBoxSearch.getSelectionModel().selectedItemProperty().addListener(((
                ObservableValue<? extends String> observable, String oldValue, String newValue) -> changeImage(newValue)
                ));
    }
    private void changeImage(String image) {
        if (image.equals("Search and Filter")) {
            img = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/pictures/Search.jpg")
                    ));
            helpImage.setImage(img);
        } else if (image.equals("Record Notes")) {
            img = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/pictures/Notes.jpg")
                    ));

            helpImage.setImage(img);
        } else if (image.equals("Plan a Journey")) {
            img = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/pictures/PlanJourney.jpg")
                    ));
            helpImage.setImage(img);
        } else if (image.equals("Planned Journeys")) {
            img = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/pictures/Planned.jpg")
                    ));
            helpImage.setImage(img);
        } else if (image.equals("Register a Vehicle")){
            img = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/pictures/RegisterVehicle.jpg")
                    ));
            helpImage.setImage(img);
        }
        helpImage.fitWidthProperty().bind(stage.widthProperty());
        helpImage.fitHeightProperty().bind(stage.heightProperty());
    }

    void init(Stage helpStage) {
        this.stage = helpStage;
        img = new Image(
                new BufferedInputStream(
                        getClass().getResourceAsStream("/pictures/Search.png")
                ));
        helpImage.setImage(img);
        helpImage.fitWidthProperty().bind(stage.widthProperty());
        helpImage.fitHeightProperty().bind(stage.heightProperty());
        fillHelp();
    }


}
