package chordFinder;

import java.util.ArrayList;
import java.util.List;

public class FullBeatChordAllChordsOne32 {



    public List<PartOfOneBeatChordAllChordsOne32> listFullBeatChordAllChordsOne32= new ArrayList<>();
    public double score;
    public int segPartOfBeat;



    public void put(int segPartOfBeat,PartOfOneBeatChordAllChordsOne32 partOfOneBeatChordAllChordsOne32){

        this.listFullBeatChordAllChordsOne32.add(partOfOneBeatChordAllChordsOne32);

        this.segPartOfBeat = segPartOfBeat;
    }


    public void setTotalScore(List<Integer> chordBeat) {

        double ajusting =  1+(((double)8-chordBeat.size())/10);
        double temp = 0;

        for (PartOfOneBeatChordAllChordsOne32 value: listFullBeatChordAllChordsOne32) {
            temp += value.bestChordScore;

        }
        score = (temp * ajusting);

    }

}
