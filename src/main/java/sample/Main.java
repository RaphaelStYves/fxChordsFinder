package sample;

import VoiceFinder.VoiceFinder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

