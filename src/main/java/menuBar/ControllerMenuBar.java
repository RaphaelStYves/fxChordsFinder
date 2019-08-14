package menuBar;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import model.Piece;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;

public class ControllerMenuBar {


    @FXML
    private MenuBar menuBar;

    public void openDialog() throws MidiUnavailableException, InvalidMidiDataException, IOException {


        String workingDir = System.getProperty("user.dir");
        File initialPath = new File(workingDir + "\\src\\main\\java\\midifile");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initialPath);
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        file.getName();
        new Piece(file);







    }



}

