package journey.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.application.Application;

public class CreditsController extends Application {

    /**
     * Open the given URL in the default browser.

     * @param url String the url to search the web for.
     */
    private void openLink(String url) {
        getHostServices().showDocument(url);
    }

    @FXML private void chargingStationIcons() {
        openLink("https://www.flaticon.com/free-icons/charging-station");
    }
    @FXML private void customerJourneyIcons() {
        openLink("https://www.flaticon.com/free-icons/customer-journey");
    }
    @FXML private void planningIcons() {
        openLink("https://www.flaticon.com/free-icons/planning");
    }
    @FXML private void checkIcons() {
        openLink("https://www.flaticon.com/free-icons/check");
    }
    @FXML private void mapsIcons() {
        openLink("https://www.flaticon.com/free-icons/maps");
    }
    @FXML private void excelIcons() {
        openLink("https://www.flaticon.com/free-icons/excel");
    }
    @FXML private void questionIcons() {
        openLink("https://www.flaticon.com/free-icons/question");
    }
    @FXML private void userIcons() {
        openLink("https://www.flaticon.com/free-icons/user");
    }
    @FXML private void locationIcons() {
        openLink("https://www.flaticon.com/free-icons/location");
    }
    @FXML private void menuIcons() {
        openLink("https://www.flaticon.com/free-icons/menu");
    }
    @FXML private void closeIcons() {
        openLink("https://www.flaticon.com/free-icons/close");
    }
    @FXML private void logOutIcons() {
        openLink("https://www.flaticon.com/free-icons/logout");
    }

    /**
     * Initialises credits controller.

     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (Boolean.FALSE.equals(isNowFocused)) {
                stage.close();
            }
        });
    }
}
