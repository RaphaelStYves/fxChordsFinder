package model;

import java.util.*;


public class TrackModel  {

    public ArrayList<Piece.Note> notes = new ArrayList<>();
    public Map<Integer, List> mapPulses = new HashMap<>();
    public int channel;
    public int numberOfNotes;
    public int highestNote;
    public int lowestNote=127;
    public int medianNote;
    public String trackName = "default";
    public int nbOfPulseWithMinOneNote;
    public double averageNumberOfNotesByPulse; //if average = around 1 than = melodie, if it's 3 by is chords because more


    public void addNote(Piece.Note note){
        this.notes.add(note);
        ++numberOfNotes;
        if(note.getNote() > highestNote){
            highestNote = note.getNote();
        }
        if(note.getNote() < lowestNote){
            lowestNote = note.getNote();
        }

        if(mapPulses.get(note.getPulse16()) == null){
            List list = new ArrayList();
            mapPulses.put(note.getPulse16(),list);
            list.add(note.getNote());
        }else{
            mapPulses.get(note.getPulse16()).add(note.getNote());
        }
    }

    public ArrayList<Piece.Note> getNotes() {
        return notes;
    }
    public void findAllDataOfTheTrack(){
        findTheMedianNote();
        findNbOfPulseWithMinOneNote();
        findAverageNumberOfNotesByPulse();
    }

    private void findAverageNumberOfNotesByPulse() {
        averageNumberOfNotesByPulse=numberOfNotes/nbOfPulseWithMinOneNote;
    }

    private void findNbOfPulseWithMinOneNote() {
        nbOfPulseWithMinOneNote = mapPulses.size();
    }

    public void findTheMedianNote() {

        Collections.sort(notes, (o1, o2) -> Integer.valueOf(o1.getNote()).compareTo(o2.getNote()));
        if (notes.size() % 2 == 0)
            medianNote = (notes.get(notes.size()/2).getNote() + notes.get(notes.size()/2 - 1).getNote())/2;
        else
            medianNote = notes.get(notes.size()/2).getNote();

    }


}
