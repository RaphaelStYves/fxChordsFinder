package chordFinder;

import java.util.HashMap;
import java.util.Map;

public class AllBeatsChordsAllChordsOne32 {

    public Map<String,FullBeatChordAllChordsOne32> mapAllBeatChordsAllChordsOne32;

    public String bestChordsBeat;

    public void put(String indexBeat,FullBeatChordAllChordsOne32 fullBeatChordAllChordsOne32){

        if (mapAllBeatChordsAllChordsOne32 == null)
        {
            mapAllBeatChordsAllChordsOne32 = new HashMap<>();
        }
        this.mapAllBeatChordsAllChordsOne32.put(indexBeat,fullBeatChordAllChordsOne32);
    }

    public void findBestChordBeat() {

        bestChordsBeat = mapAllBeatChordsAllChordsOne32.entrySet().stream().max((entry1, entry2) -> entry1.getValue().score > entry2.getValue().score ? 1 : -1).get().getKey();

    }

}
