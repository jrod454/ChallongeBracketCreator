package com.jrod.ChallongeBracketCreator;

import com.sun.java.accessibility.util.GUIInitializedListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Jared Win Eaton on 3/6/2016.
 */
@SpringBootApplication
public class Application extends javafx.application.Application
{
    @Autowired
    private GuiController guiController;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Challonge Bracket Creator");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        ApplicationContext cxt = new AnnotationConfigApplicationContext(Application.class);
        loader.setControllerFactory(cxt::getBean);
        Parent root = loader.load();
        GuiController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        launch(args);
    }
}
