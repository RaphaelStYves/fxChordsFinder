package model;

import java.util.ArrayList;

public class TrackModel {


    private ArrayList<Piece.Note> notes = new ArrayList<>();
    private double scorePulseForTwo32DivideByNbOfNotes;
    private int numberOfNotes;
    private int[] arrayPulses;

    public void addNote(Piece.Note note){
        this.notes.add(note);
    }

    public ArrayList<Piece.Note> getNotes() {
        return notes;
    }

    public double getscorePulseForTwo32DivideByNbOfNotes() {
        return scorePulseForTwo32DivideByNbOfNotes;
    }

    public void setScorePulseForTwo32DivideByNbOfNotes(double scorePulseForTwo32DivideByNbOfNotes) {
        this.scorePulseForTwo32DivideByNbOfNotes = scorePulseForTwo32DivideByNbOfNotes;
    }

    public int getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(int numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public int[] getArrayPulses() {
        return arrayPulses;
    }

    public void setArrayPulses(int[] arrayPulses) {
        this.arrayPulses = arrayPulses;
    }
}
