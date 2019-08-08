package chordFinder.algo32;

import java.util.HashMap;
import java.util.Map;

public class PartOfOneBeatChordAllChordsOne32 {

    private Map<String,Integer> mapPartOfOneBeatChordAllChordsOne32;


    public String bestChord;
    public int bestChordScore;
    public int startPulse;
    public int numberOfPulse;

    public void put(String Chord,int partOfOneBeatChordOneChordOne32){
        if (mapPartOfOneBeatChordAllChordsOne32 == null)
        {
            mapPartOfOneBeatChordAllChordsOne32 = new HashMap<String, Integer>();
        }
        this.mapPartOfOneBeatChordAllChordsOne32.put(Chord,partOfOneBeatChordOneChordOne32);
    }

    public void setBestChordforThisPart() {
        findBestChord();
        findBestChordScore();
    }

    public void findBestChord() {
        bestChord =  mapPartOfOneBeatChordAllChordsOne32.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
    }

    public void findBestChordScore() {

        bestChordScore = mapPartOfOneBeatChordAllChordsOne32.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();
    }

    public void setStartPulse(int startPulse) {
    this.startPulse = startPulse;
    }

    public void setChordBeatSeq( int numberOfPulse){
        this.numberOfPulse = numberOfPulse;
    }

}
