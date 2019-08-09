package chordFinder.algo32;

import chordFinder.PulseChord;
import model.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chord32 {

    private Piece piece;
    String workingDir = System.getProperty("user.dir");

    private Map<String, List<Integer>> mapTableForceChords = new HashMap<>();
    private Map<String, List<Integer>> mapTableBeatsChords = new HashMap<>();

    private Map<Integer, AllBeatsChordsAllChordsOne32> mapAllBeatChordsAllChordsAll32 = new HashMap<>();

    public Map<Integer,Map<String, PulseChord>> mapScoreOfEachPulseOfAllChords  = new HashMap<>();

    public Map<String, Map<Integer, Integer>> mapScoreByChordAllPulses = new HashMap<>();


    public Map<Integer, String> mapBestChordForEachPulse = new HashMap<>();


    public void putOnlyBestChordsForEachPulseInAList() {


        for (AllBeatsChordsAllChordsOne32 AllbeatChordsOne32 : mapAllBeatChordsAllChordsAll32.values()) {

            FullBeatChordAllChordsOne32 bestBeatChord = AllbeatChordsOne32.mapAllBeatChordsAllChordsOne32.get(AllbeatChordsOne32.bestChordsBeat);
            List<PartOfOneBeatChordAllChordsOne32> listFullBeatChordAllChordsOne32 = bestBeatChord.listFullBeatChordAllChordsOne32;

            for ( PartOfOneBeatChordAllChordsOne32 partOfOneBeat: listFullBeatChordAllChordsOne32) {

                for (int i = 0; i < partOfOneBeat.numberOfPulse; i++) {
                    mapBestChordForEachPulse.put(partOfOneBeat.startPulse + i ,partOfOneBeat.bestChord);
                }

            }


        }
    }




    public void findAllBeatChordsAllChordsAll32() {

        for (int startPulse = 0; startPulse < piece.getPieceLenght16(); startPulse += 32) {

            if (startPulse + 32 <= piece.getPieceLenght16()) {
                mapAllBeatChordsAllChordsAll32.put(startPulse, findAllBeatsChordsAllChordsOne32(startPulse));
            }
        }

    }

    public AllBeatsChordsAllChordsOne32 findAllBeatsChordsAllChordsOne32(int startPulse) {

        AllBeatsChordsAllChordsOne32 allBeatsChordsAllChordsOne32 = new AllBeatsChordsAllChordsOne32();

        for (Map.Entry<String, List<Integer>> chordBeat : mapTableBeatsChords.entrySet()) {
            allBeatsChordsAllChordsOne32.put(chordBeat.getKey(), findBestChordsForOneFullBeatChordsForOne32(chordBeat.getValue(), startPulse));
        }

        allBeatsChordsAllChordsOne32.findBestChordBeat();

        return allBeatsChordsAllChordsOne32;

    }


    //here we have all the best possible chords for one beat with the total score like 8,8,8,8.
    // We call FullBeatChordAllChordsOne32 we have map of all part of beat and the sum of all beat.
    public FullBeatChordAllChordsOne32 findBestChordsForOneFullBeatChordsForOne32(List<Integer> chordBeat, int startPulse) {

        FullBeatChordAllChordsOne32 fullBeatChordAllChordsOne32 = new FullBeatChordAllChordsOne32();
        int pulseDone = 0;
        for (int numberOfPulse : chordBeat) {
            fullBeatChordAllChordsOne32.put(numberOfPulse, findBestChordOfPartOfOneBeatChordAllChordsOne32(startPulse + pulseDone, numberOfPulse));
            pulseDone += numberOfPulse;
        }

        //ajuste, majore, augment en fonction du BeatChordPart and BeatChord. Plus un beatChord est grand plus il sera majorer.
        fullBeatChordAllChordsOne32.setTotalScore(chordBeat);

        return fullBeatChordAllChordsOne32;
    }

    //here we can say. We know the best Chord for this part and his score. Lors for the part 8 in a 8,8,8,8, C is the best chord and his score are 34.
    //The if we call ParOfOneBeatChordAllCHordsOne32 we can know the only 2 importantes informations.
    public PartOfOneBeatChordAllChordsOne32 findBestChordOfPartOfOneBeatChordAllChordsOne32(int startPulse, int numberOfPulse) {

        PartOfOneBeatChordAllChordsOne32 partOfOneBeatChordAllChordsOne32 = new PartOfOneBeatChordAllChordsOne32();


        for (Map.Entry<String, List<Integer>> forceChord : mapTableForceChords.entrySet()) {
            partOfOneBeatChordAllChordsOne32.put(forceChord.getKey(), findPartOfOneBeatChordOneChordOne32(startPulse, numberOfPulse, forceChord.getKey()));
        }

        partOfOneBeatChordAllChordsOne32.setBestChordforThisPart();
        partOfOneBeatChordAllChordsOne32.setStartPulse(startPulse);
        partOfOneBeatChordAllChordsOne32.setChordBeatSeq(numberOfPulse);

        return partOfOneBeatChordAllChordsOne32;
    }

    public int findPartOfOneBeatChordOneChordOne32(int startPulse, int numberOfPulse, String forceChord) {

        PartOfOneBeatChordOneChordOne32 partOfOneBeatChordOneChordOne32 = new PartOfOneBeatChordOneChordOne32();

        int temp = 0;
        for (int indexPulse = 0; indexPulse < numberOfPulse; indexPulse++) {
            temp += mapScoreByChordAllPulses.get(forceChord).get((startPulse + indexPulse));
        }

        partOfOneBeatChordOneChordOne32.setScore(temp);

        return temp;
    }
    public void getMapofChordBeat() throws IOException {
        BufferedReader br;
        String line;

        List<Integer> beats;

        br = new BufferedReader(new FileReader(workingDir + "\\src\\main\\java\\chordFinder\\algo32\\ChordBeat.csv"));


        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] beat = line.split(";");

            beats = new ArrayList<>();
            for (int i = 1; i < beat.length; i++) {
                if (Integer.parseInt(beat[i]) > 0) {

                    beats.add(Integer.parseInt(beat[i]));
                }
            }
            mapTableBeatsChords.put(beat[0], beats);
        }
    }

}
