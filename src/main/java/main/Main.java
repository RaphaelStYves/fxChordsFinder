package main;

import VoiceFinder.VoiceFinder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import menuBar.ControllerMenuBar;
import model.Piece;


public class Main extends Application {

    private Piece newPiece;

    private ControllerMenuBar controllerMenuBar;
    private VoiceFinder voiceFinder;
    private Stage stage;



    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root,222,222);

    public Main(){

    }
    @Override
    public void start(Stage primaryStage) throws Exception{

        this.stage = primaryStage;

        FXMLLoader loader;

        //MenuBar
        VBox vbox = new VBox();

        loader = new FXMLLoader();
        System.out.println(getClass().getResource("/menuBar/menuBar.fxml"));
        loader.setLocation(getClass().getResource("/menuBar/menuBar.fxml"));
        vbox.getChildren().add(loader.load());


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Current project is modified");
        alert.setContentText("Save?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Yes", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Yes", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);






        controllerMenuBar = loader.getController();

        root.setTop(vbox);

        primaryStage.setScene(scene);

        primaryStage.show();




    }

//    public void createPiece(File file) throws MidiUnavailableException, InvalidMidiDataException, IOException {
//
//        //Create Piece
//        newPiece = controllerMenuBar.createNewPiece(file);
//
//    }

    public static void main(String[] args) {
        launch(args);
    }

}

