package VoiceFinder;

import model.Piece;
import model.TrackModel;

import java.util.Map;

/**Find what kind of track is it.
 * voice, drum, chords, bass, solo.
 *
 *
 */

public class VoiceFinder {

    private Piece piece;


    public VoiceFinder() {

    }

    private void findVoiceTrack() {

        multiplyOne32PulseWithTheSeconde32Pulse();

    }

    public void multiplyOne32PulseWithTheSeconde32Pulse()  {

        loadTrackInAArrayPulse();
        findNumberOfnoteInTrack();

        for (Map.Entry<Integer, TrackModel> entry : piece.getTrackNumbers().entrySet()) {

            int sumOfpulses32 =0;
            for (int i = 0; i < entry.getValue().getArrayPulses().length ; i += 32) {

                if(i + 64< entry.getValue().getArrayPulses().length){ //this if is for stopping a the last 32
                    for (int j = 0; j < 32 ; j++) {
                        sumOfpulses32 += entry.getValue().getArrayPulses()[i+j]*entry.getValue().getArrayPulses()[i+j+32]; //if in the first block of 32,  notes appears at the same time of the seconde block of 32 then the score will be good.
                    }
                }
            }
           entry.getValue().setScorePulseForTwo32DivideByNbOfNotes((double)sumOfpulses32/ entry.getValue().getNumberOfNotes()); // to normalise score with the quantity of note, we divide by the number of note in the track.
        }
    }


    public void loadTrackInAArrayPulse() {

        for (Map.Entry<Integer, TrackModel> entry : piece.getTrackNumbers().entrySet()) {
            int[] pulses = new int[piece.getPieceLenght16()];

            for (int j = 0; j < entry.getValue().getNotes().size(); j++) {
                pulses[entry.getValue().getNotes().get(j).getPulse16()] += 1;
            }
            entry.getValue().setArrayPulses(pulses);
        }
    }

    public void findNumberOfnoteInTrack(){

        for (Map.Entry<Integer, TrackModel> entry : piece.getTrackNumbers().entrySet()) {

            int sum = 0;
            for (int j = 0; j < entry.getValue().getNotes().size(); j++) {
                if(entry.getValue().getNotes().get(j).getOn() == true){
                    sum +=1;
                }
            }
            entry.getValue().setNumberOfNotes(sum);
        }

    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}