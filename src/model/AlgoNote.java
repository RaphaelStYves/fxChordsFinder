package model;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlogNote  receive in 3 inputs
 * 1) the original Echord (find by the user or the program)
 * 2) the new EChords (chose by the user)
 * 3) and the original note.
 *
 * The output is the new note.
 * the algorith is in with 3 datas bats
 * 1) ChordBeat.csv.
 * 2) chordForce.csv.
 * 3) ChordStingToDegres.csv.
 *
 *
 * The alogorim is very simple but the next version, the algo will be upgrade.
 */

public  class AlgoNote {

    public static int changenote(String origChord, String newChord, int note) {
        if (origChord == null){
            return  note;
        }
        if (newChord == null){
            return  note;
        }

        int newNote = 0;
        int degre = 0;
        int ajuste;




        return newNote;
    }






}




