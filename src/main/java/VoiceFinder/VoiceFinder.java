package VoiceFinder;

import model.TrackModel;

import java.util.Map;


public class VoiceFinder {

    private Map<Integer, TrackModel> trackModels;
    private int medianNoteOfWholePiece;


    //with these voiceFinder, the new midi file, each track will be annotated with( voice, bass, drum, accompaniment, drum, second melodie etc.)
    public VoiceFinder(Map<Integer, TrackModel> trackModels, int medianNoteOfWholePiece) {

        this.trackModels = trackModels;
        this.medianNoteOfWholePiece = medianNoteOfWholePiece;

        //what's a base line.
        //the lowest part or sequence of notes in a piece of music.

        //chords.
        //it's have many notes in the same time. But not always. Harpège

        //find voice.
        //many pulse have notes of the melodie

        return;
    }

    public Map<Integer, TrackModel> voiceFinder(){
        drumFinder();
        bassFinder();
        melodieFinder();
        findAccompaniment();

        return trackModels;

            }

            public void drumFinder() {
                //fist find drum and rename it.
                for (TrackModel trackModel : trackModels.values()) {

                    if (trackModel.channel == 9){
                trackModel.trackName = "Drum";
            }
        }
    }

    public void bassFinder() {

        for (TrackModel trackModel : trackModels.values()) {
            if (trackModel.channel != 9){
                if(trackModel.averageNumberOfNotesByPulse < 1.05){ //eleminate chords.
                    if (trackModel.medianNote < medianNoteOfWholePiece){                        //bass = lower than medianNote
                        trackModel.trackName = "Bass";
                    }
                }
            }
        }
    }

    public void melodieFinder() {

        for (TrackModel trackModel : trackModels.values()) {
            if (trackModel.channel != 9){
                if(trackModel.averageNumberOfNotesByPulse < 1.05){ //eleminate chords.
                    if (trackModel.medianNote > medianNoteOfWholePiece){                        //Melody lower than medianNote
                        trackModel.trackName = "Melody";
                    }
                }
            }
        }
    }

    public void findAccompaniment() {

        for (TrackModel trackModel : trackModels.values()) {
            if (trackModel.channel != 9){
                if(trackModel.averageNumberOfNotesByPulse > 1.05){ //eleminate chords.
                    if (trackModel.medianNote < medianNoteOfWholePiece +8  && trackModel.medianNote > medianNoteOfWholePiece -8 ){                        //Melody lower than medianNote
                        trackModel.trackName = "Accompaniment";
                    }
                }
            }
        }
    }


}