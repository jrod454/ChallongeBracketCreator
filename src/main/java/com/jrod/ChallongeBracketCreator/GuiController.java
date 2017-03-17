package com.jrod.ChallongeBracketCreator;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Jared Win Eaton on 3/15/2016.
 */
@Component
public class GuiController
{
    @Autowired
    private Controller controller;

    @FXML
    private TextField pathTextField;

    @FXML
    private Button createTournamentButton;

    @FXML
    private Button selectFileButton;

    private String errorText;

    private FileChooser fileChooser;

    private Stage stage;

    public GuiController()
    {
        this.fileChooser = new FileChooser();
    }

    @FXML
    private void selectFileAndUpdatePathTextField()
    {
        pathTextField.setText(fileChooser.showOpenDialog(stage).getPath());
    }

    @FXML
    private void submitCreateTournament()
    {
        try
        {
            System.out.println(pathTextField.getText());
            if(controller == null)
            {
                System.out.println("controller  was null");
            }
            controller.createBracketSilent(pathTextField.getText());
        } catch (IOException e)
        {
            createAlert(e.getMessage());
        }
        createAlert("Tournament created successfully!", "Success!");
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void createAlert(String error)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }

    public void createAlert(String message, String title)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
