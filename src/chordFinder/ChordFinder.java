package chordFinder;


import createCSV.CsvAllPulseAllChords;
import model.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ChordFinder {

    String workingDir = System.getProperty("user.dir");

    private Piece piece;

    private Map<String, List<Integer>> mapTableForceChords = new HashMap<>();
    private Map<String, List<Integer>> mapTableBeatsChords = new HashMap<>();
    private Map<String, List<Integer>> mapRefMapFondamentalChords = new HashMap<>();

    private Map<Integer, List<Integer>> mapfondamentalChordsForEachPulse = new HashMap<>();


    private Map<Integer, AllBeatsChordsAllChordsOne32> mapAllBeatChordsAllChordsAll32 = new HashMap<>();

    public Map<Integer,Map<String,PulseChord>> mapScoreOfEachPulseOfAllChords  = new HashMap<>();

    private int[][] pieceMOD12;

    public Map<String, Map<Integer, Integer>> mapScoreByChordAllPulses = new HashMap<>();
    public Map<Integer, Integer> mapScoreOneChordAllPulses;

    public Map<Integer, String> mapBestChordForEachPulse = new HashMap<>();


    public ChordFinder(Piece piece) throws IOException {
        this.piece = piece;

        getMapofChordBeat();
        getMapofChordsForce();
        getMapofFondamentalChords();
        findScoreOfAllPulsesAndAllChords();

       findAllBeatChordsAllChordsAll32();


        putOnlyBestChordsForEachPulseInAList();

        transformeEChordInRealMidiNote();


    }

    private void transformeEChordInRealMidiNote() {


        for (Map.Entry<Integer, String> entry : mapBestChordForEachPulse.entrySet()) {

            mapfondamentalChordsForEachPulse.put(entry.getKey(), mapRefMapFondamentalChords.entrySet().stream().filter(eChordListEntry -> eChordListEntry.getKey().equals(entry.getValue())).map(Map.Entry::getValue).findFirst().orElse(null));

        }

    }


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

    public void reFormatPulseChordByChord() {

        for (String ForceChord : mapTableForceChords.keySet()) {

            mapScoreOneChordAllPulses = new HashMap<>();

            for (Map<String, PulseChord> pulses : mapScoreOfEachPulseOfAllChords.values()) {

                PulseChord pulseChord = pulses.get(ForceChord);

                mapScoreOneChordAllPulses.put(pulseChord.indexPulse, pulseChord.score);

            }
            mapScoreByChordAllPulses.put(ForceChord, mapScoreOneChordAllPulses);

        }

    }


    private void findScoreOfAllPulsesAndAllChords() {

        pieceMOD12 = loadPieceInOnesInArray();

        for (int indexPulse = 0; indexPulse < pieceMOD12.length; indexPulse++) {

          Map<String,PulseChord> ListScoreOfEachPulseOfAllChords = new HashMap<>();

            for (Map.Entry<String, List<Integer>> Chord : mapTableForceChords.entrySet()) {

                PulseChord pulseChord = new PulseChord();

                pulseChord.setIndexPulse(indexPulse);
                pulseChord.setChord(Chord.getKey());
                pulseChord.setScore(multiplyForceByNoteForOneChord(indexPulse, Chord.getValue()));

                ListScoreOfEachPulseOfAllChords.put(Chord.getKey(), pulseChord);
            }

            mapScoreOfEachPulseOfAllChords.put(indexPulse, ListScoreOfEachPulseOfAllChords);
        }

        reFormatPulseChordByChord();

        CsvAllPulseAllChords csv = new CsvAllPulseAllChords(mapScoreByChordAllPulses, mapTableForceChords);

    }

    private int multiplyForceByNoteForOneChord(int indexPulse, List<Integer> forceChord) {
        int[] colonne16 = new int[12];
        for (int k = 0; k < pieceMOD12[0].length; k++) {
            colonne16[k] = (pieceMOD12[indexPulse][k] * forceChord.get(k));
        }
        return IntStream.of(colonne16).sum();

    }

    public int[][] loadPieceInOnesInArray() {
        pieceMOD12 = new int[piece.getPieceLenght16()][12];
        //mettre le song sous forme de MOD12 pour en faire l'analyse'
        // ne pas prendre en compte les channels  9 , le drum, il ne doit pas etre considéré dans l'analyse des accords
        for (Piece.Note note : piece.notes) {
            if (note.getChannel() != 9) {
                for (int j = 0; j < note.getLenght16(); j++) {
                    pieceMOD12[note.getPulse16() + j][note.getNote() % 12] = 1;
                }
            }
        }
        fillEmpty16ieme();
        return pieceMOD12;
    }

    private void fillEmpty16ieme() {
        //Remplir les trous d'accords du type aucune note dans 1/16 de temps. Dans ce cas repété les 1 du dernier 1/16.
        // //Ceci permettra de mettre de l,avant une meilleur logique d'évaluation des accords.
        if (!hasfirstColonneEmpty(pieceMOD12)) {
            pieceMOD12[0][0] = 1;
        }

        for (int i = 0; i < piece.getPieceLenght16(); i++) {
            int tempo = 0;
            for (int j = 0; j < 12; j++) {
                tempo = +pieceMOD12[i][j];
                if (tempo != 0) {
                    break;
                }
            }
            if (tempo == 0) {
                for (int n = 0; n < 12; n++) {
                    pieceMOD12[i][n] = pieceMOD12[i - 1][n];
                }
            }
        }
    }

    private boolean hasfirstColonneEmpty(int[][] arraymod12notes) {

        int tempo;
        for (int i = 0; i < 12; i++) {
            tempo = arraymod12notes[0][i];
            if (tempo != 0) {
                return true;
            }
        }
        return false;
    }

    public void getMapofChordsForce() throws IOException {
        BufferedReader br;
        String line;

        br = new BufferedReader(new FileReader(workingDir + "\\src\\chordFinder\\ChordForce3.csv"));
        while ((line = br.readLine()) != null) {

            // use comma as separator
            String[] chord = line.split(";");

            List<Integer> forceChord = new ArrayList<>();
            for (int i = 1; i < chord.length; i++) {
                forceChord.add(Integer.parseInt(chord[i]));

            }

            mapTableForceChords.put(chord[0], forceChord);
        }
    }

    public void getMapofFondamentalChords() throws IOException {
        BufferedReader br;
        String line;

        br = new BufferedReader(new FileReader(workingDir + "\\src\\chordFinder\\fondamentalChords.csv"));
        while ((line = br.readLine()) != null) {

            // use comma as separator
            String[] chord = line.split(";");
            List<Integer> fondamentalChords = new ArrayList<>();
            for (int i = 1; i < chord.length; i++) {
                fondamentalChords.add((int) Integer.parseInt(chord[i]));
            }
            mapRefMapFondamentalChords.put((chord[0]), fondamentalChords);

        }
    }

    public void getMapofChordBeat() throws IOException {
        BufferedReader br;
        String line;

        List<Integer> beats;

        br = new BufferedReader(new FileReader(workingDir + "\\src\\chordFinder\\ChordBeat.csv"));


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

    public Map<Integer, List<Integer>> getMapfondamentalChordsForEachPulse() {
        return mapfondamentalChordsForEachPulse;
    }
}



