package com.cejv416.dbjavafxdemo.presentation;

import com.cejv416.dbjavafxdemo.business.FishManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class DisplayFX extends Application {

    private TextArea textArea;

    /**
     * This basic FX start method creates a text area, puts it in a scene and
     * then puts it on the stage.
     * @param primaryStage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Creates the textArea and fills it with the results of retrieving all
        // the fish
        textArea = new TextArea(getTheFish());

        textArea.setWrapText(false);
        textArea.setPrefSize(500, 600);
        textArea.setStyle("-fx-font-size:14pt; -fx-font-weight:bold; -fx-font-family:Consolas, monaco, monospace");

        Scene myScene = new Scene(textArea);

        primaryStage.setScene(myScene);
        primaryStage.setTitle("DB Viewer");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Retrieve the records and display them
     */
    private String getTheFish() {
        FishManager fm = new FishManager();
        String records = fm.retrieveFish();
        if (records != null && records.length() > 0) {
            return (records);
        } else {
            return ("No records to display.");
        }
    }

    /**
     * It all begins here
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
