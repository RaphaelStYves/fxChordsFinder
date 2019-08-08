package chordFinder;


import createCSV.CsvAllPulseAllChords;
import model.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class ChordFinder {

    String workingDir = System.getProperty("user.dir");

    private Piece piece;

    private Map<String, List<Integer>> mapTableForceChords = new HashMap<>();
    private Map<String, List<Integer>> mapTableBeatsChords = new HashMap<>();
    private Map<String, List<Integer>> mapRefMapFondamentalChords = new HashMap<>();
    public Map<String, Map<Integer, Integer>> mapScoreByChordAllPulses = new HashMap<>();

    private Map<Integer, List<Integer>> mapfondamentalChordsForEachPulse = new HashMap<>();

    public Map<Integer,Map<String,PulseChord>> mapScoreOfEachPulseOfAllChords  = new HashMap<>();
    public Map<Integer, Integer> mapScoreOneChordAllPulses;

    private int[][] pieceMOD12;





    public Map<Integer, String> mapBestChordForEachPulse = new HashMap<>();


    public ChordFinder(Piece piece) throws IOException {
        this.piece = piece;

        getMapofChordBeat();
        getMapofChordsForce();
        getMapofFondamentalChords();
        findScoreOfAllPulsesAndAllChords();

        doTheAlgorithm2();

        transformeEChordInRealMidiNote();

    }

    private void doTheAlgorithm2() {

        int valueOfTheLastPulseChord=0;

        for (Map.Entry<Integer, Map<String, PulseChord>> chords : mapScoreOfEachPulseOfAllChords.entrySet()) {

            int maxValue = chords.getValue().entrySet().stream().max((entry1, entry2) -> entry1.getValue().score > entry2.getValue().score ? 1 : -1).get().getValue().score;

            if(valueOfTheLastPulseChord-maxValue > 1){

                List<PulseChord> listOfallMaxPulseChords = new ArrayList<>();
                for (PulseChord chord : chords.getValue().values()) {
                    //proceed all max score of one pulse.
                    if (chord.score == maxValue) {
                        listOfallMaxPulseChords.add(chord);
                    }
                }
                PulseChord pulseChord = foreachMaxPulseChordCheckTheNextsPulseScoreUntilOnePulseScoreAreBetter(listOfallMaxPulseChords);
                valueOfTheLastPulseChord = pulseChord.score;

            }


        }
    }

    private PulseChord foreachMaxPulseChordCheckTheNextsPulseScoreUntilOnePulseScoreAreBetter(List<PulseChord> listOfallMaxPulseChords) {

        Boolean bestSequenceWasFound = false;
        int offset = 1;

        while (bestSequenceWasFound = false) {

            for (PulseChord pulseChord : listOfallMaxPulseChords) {

                pulseChord.nextsScorePulse = mapScoreOfEachPulseOfAllChords.get(pulseChord.indexPulse + offset).get(pulseChord.Chord).score;

            }
            listOfallMaxPulseChords= checkIfTheNextPulseHaveAPulseScoreWithAHigherScore(listOfallMaxPulseChords);

            if (listOfallMaxPulseChords.size() ==1 ){
                bestSequenceWasFound = true;
            }

            ++offset;

        }

        return listOfallMaxPulseChords.get(0);

    }

    private List<PulseChord>  checkIfTheNextPulseHaveAPulseScoreWithAHigherScore(List<PulseChord> listOfallMaxPulseChords) {
        int maxScore = listOfallMaxPulseChords.stream().max((entry1, entry2) -> entry1.score > entry2.score ? 1 : -1).get().score;

        for (PulseChord pulseChord : listOfallMaxPulseChords) {

            if (pulseChord.score < maxScore) {
                listOfallMaxPulseChords.remove(pulseChord);
            }

        }
        return listOfallMaxPulseChords;
    }


    private void transformeEChordInRealMidiNote() {

        for (Map.Entry<Integer, String> entry : mapBestChordForEachPulse.entrySet()) {

            mapfondamentalChordsForEachPulse.put(entry.getKey(), mapRefMapFondamentalChords.entrySet().stream().filter(eChordListEntry -> eChordListEntry.getKey().equals(entry.getValue())).map(Map.Entry::getValue).findFirst().orElse(null));
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

        br = new BufferedReader(new FileReader(workingDir + "\\src\\main\\java\\chordFinder\\ChordForce3.csv"));
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

        br = new BufferedReader(new FileReader(workingDir + "\\src\\main\\java\\chordFinder\\fondamentalChords.csv"));
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

    public Map<Integer, List<Integer>> getMapfondamentalChordsForEachPulse() {
        return mapfondamentalChordsForEachPulse;
    }
}



