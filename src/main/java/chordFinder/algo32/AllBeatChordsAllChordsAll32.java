package chordFinder.algo32;

import java.util.HashMap;
import java.util.Map;

public class AllBeatChordsAllChordsAll32 {

    private Map<Integer, AllBeatsChordsAllChordsOne32> allBeatChordsAllChordsAll32;

    public double getTotalScoreOfAllBestScore() {
        return totalScoreOfAllBestScore;
    }

    private double totalScoreOfAllBestScore;

    public void put(int indexBeat, AllBeatsChordsAllChordsOne32 allBeatsChordsAllChordsOne32){

        if (allBeatChordsAllChordsAll32 == null)
        {
            allBeatChordsAllChordsAll32 = new HashMap<Integer, AllBeatsChordsAllChordsOne32>();
        }
        this.allBeatChordsAllChordsAll32.put(indexBeat, allBeatsChordsAllChordsOne32);
    }

    public Map<Integer, AllBeatsChordsAllChordsOne32> getAllBeatChordsAllChordsAll32() {
        return allBeatChordsAllChordsAll32;
    }


}
