package menuBar;

import chordFinder.ChordFinder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import sample.Main;
import model.Piece;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;

public class ControllerMenuBar {

    private Piece piece;
    private Main main;
    private String nameFile;

    @FXML
    private MenuBar menuBar;


    @FXML
    private Menu filedevice;

    @FXML
    private void initialize() {

        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        CheckMenuItem checkMenuItem = null;

        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);

                checkMenuItem = new CheckMenuItem(device.getDeviceInfo().getName());

            } catch (MidiUnavailableException e) {
                // Handle or throw exception...
            }

            filedevice.getItems().add(checkMenuItem);
        }
    }

    public void openDialog(ActionEvent actionEvent) throws MidiUnavailableException, InvalidMidiDataException, IOException {


        String workingDir = System.getProperty("user.dir");
        File initialPath = new File(workingDir + "\\src\\midifile");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initialPath);
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        nameFile = file.getName();
        piece = new Piece(file);







    }



}

