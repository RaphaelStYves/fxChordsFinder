package model;

import VoiceFinder.VoiceFinder;
import chordFinder.ChordFinder;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Piece {

        //Property
    private int resolution;
    private float divisionType;
    private int cTranspose;
    private int pieceLenght16;
    private float bpm = 0;
    private String name = "noName";
    public List<Note> notes = new ArrayList<>();
    public Map<Integer, Pulse> pulses = new HashMap<>();
    private Map<Integer, TrackModel> trackModels = new HashMap<>();
    private Sequence sequence;
    private Track track;
    private ChordFinder chords;
    private int trackNumber = 0;
    public int medianNoteOfWholePiece;
    File file;



    private static final int NOTE_ON = 0x90;
    private static final int NOTE_OFF = 0x80;
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public Piece(File file) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        this.file = file;
        createMidiObject();
    }

    public Piece createMidiObject()throws IOException, InvalidMidiDataException, MidiUnavailableException {

        this.name = fileNameWithoutExtension(file);

        Sequence sequence = MidiSystem.getSequence(file);
        Sequencer seqr = MidiSystem.getSequencer();
        seqr.open();
        seqr.setSequence(sequence);

        bpm = seqr.getTempoInBPM();
        resolution = sequence.getResolution();
        divisionType = sequence.getDivisionType();



        for (Track track : sequence.getTracks()) {
            trackNumber++;

            for (int i = 0; i < track.size(); i++) {

                MidiEvent event = track.get(i);

                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;


                    if ((sm.getCommand() == NOTE_ON)) {
                        Note note = new Note();

                        note.setTracknumber(trackNumber);
                        note.setChannel(sm.getChannel());
                        note.setIndex(i);
                        note.setOn(true);
                        note.setNote(sm.getData1());
                        note.setVelocity(sm.getData2());
                        note.setPulse(event.getTick());
                        note.transformetoPulse16(event.getTick());
                        note.setOctave((sm.getData1() / 12) - 1);



                        note.setNotename(NOTE_NAMES[sm.getData1() % 12]);

                        notes.add(note);


                    } else if ((sm.getCommand() == NOTE_OFF))  {
                        Note note = new Note();

                        note.setTracknumber(trackNumber);
                        note.setChannel(sm.getChannel());
                        note.setIndex(i);
                        note.setOn(false);
                        note.setNote(sm.getData1());
                        note.setPulse(event.getTick());
                        note.setVelocity(sm.getData2());

                        note.setNotename(NOTE_NAMES[sm.getData1() % 12]);

                        notes.add(note);




                    }
                }
            }
        }

        changeNoteWithVeloZeroToFalse();
        calculateLenghtOfEachNote();
        removeNoteOff();
        removeNotesWithNoLenght();
        transposeEachNotestoCTonality();
        findPieceLenght16();
        findTheMedianNote();


        chords = new ChordFinder(this);

        createNotesObjectFromChords();
        createTrackList();

        trackModels= new VoiceFinder(trackModels, medianNoteOfWholePiece).voiceFinder();

        createMidiFileWithMidiObject();

        return this;
    }

    public void findTheMedianNote() {

        Collections.sort(notes, (o1, o2) -> Integer.valueOf(o1.getNote()).compareTo(o2.getNote()));
        if (notes.size() % 2 == 0)
            medianNoteOfWholePiece = (notes.get(notes.size()/2).getNote() + notes.get(notes.size()/2 - 1).getNote())/2;
        else
            medianNoteOfWholePiece = notes.get(notes.size()/2).getNote();

    }

    private void findPieceLenght16() {

        int temp = 0;

        for (Note note : notes) {
            if (note.getPulse16() + note.getLenght16() > temp) {
                temp = note.getPulse16() + note.getLenght16();
            }
        }

        pieceLenght16 = temp;

    }




    private void createNotesObjectFromChords(){

        trackNumber++;

        for (Map.Entry<Integer, List<Integer>> entry : chords.mapfondamentalChordsForEachPulse.entrySet()){

            for (int j = 0; j < entry.getValue().size(); j++) {

                Note note = new Note();

                note.setTracknumber(trackNumber);
                note.setChannel(15);
                note.setIndex(entry.getKey());
                note.setOn(true);
                note.setNote(entry.getValue().get(j));
                note.setVelocity(80);
                note.setPulse16(entry.getKey());
                note.setLenght16(1);
                note.setOctave(entry.getValue().get(j) - 1);


                note.setNotename(NOTE_NAMES[entry.getValue().get(j) % 12]);
                notes.add(note);

            }

        }



    }

    private void removeNotesWithNoLenght() {

        for (int i = 0; i < notes.size(); i++) {
            if((notes.get(i).getOn() == true) && notes.get(i).getLenght16() == 0){
                notes.remove(i);
                        i -= 1;
            }
        }
    }

    private void removeNoteOff(){

        for (int i = 0; i < notes.size(); i++) {
            if((notes.get(i).getOn() == false)){
                notes.remove(i);
                i -= 1;
            }
        }

    }

//    private void createPulsesList(){
//
//        for (int i = 0; i < notes.size(); i++) {
//
//            if ( !pulses.containsKey(notes.get(i).getPulse16())){
//
//                Pulse pulse = new Pulse();
//
//                pulses.put((notes.get(i).getPulse16()), pulse);
//                pulse.addNote((notes.get(i)));
//
//            }else {
//
//                Pulse pulse = pulses.get((notes.get(i).getPulse16()));
//                pulse.addNote(notes.get(i));
//
//            }
//
//        }
//
//    }

    private void createTrackList(){

        for (int i = 0; i < notes.size(); i++) {

            if ( !trackModels.containsKey(notes.get(i).getTracknumber())){

                TrackModel tracknumber = new TrackModel();

                trackModels.put((notes.get(i).getTracknumber()), tracknumber);
                tracknumber.channel = notes.get(i).channel;
                tracknumber.addNote((notes.get(i)));

            }else {

                TrackModel tracknumber = trackModels.get((notes.get(i).getTracknumber()));
                tracknumber.addNote(notes.get(i));
            }

        }
        for (TrackModel trackModel : trackModels.values()) {
            trackModel.findAllDataOfTheTrack();
        }
    }

    private void transposeEachNotestoCTonality() {

        calculateTransposeForCTonality();

        for (int i = 0; i < notes.size(); i++) {

            if(notes.get(i).channel != 9){
                notes.get(i).setNote(notes.get(i).getNote() + cTranspose);
            }
        }
    }

    public String fileNameWithoutExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos > 0 && pos < (name.length() - 1)) {
            // there is a '.' and it's not the first, or last character.
            return name.substring(0,  pos);
        }
        return name;
    }



    private void changeNoteWithVeloZeroToFalse() {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getVelocity() == 0) {
                notes.get(i).setOn(false);
            }
        }
    }



    private void calculateLenghtOfEachNote() {

        int nnote;
        int nnote2;
        int cchan;
        int cchan2;
        long mtick;
        long mtick2;


        for (int i = 0; i < notes.size(); i++) {

            if (notes.get(i).getOn() == false) {
                nnote = notes.get(i).getNote();
                cchan = notes.get(i).getTracknumber();
                mtick = notes.get(i).getPulse();

                for (int j = i - 1; j >= 0; j--) {
                    nnote2 = notes.get(j).getNote();
                    cchan2 = notes.get(j).getTracknumber();

                    if (nnote == nnote2 && cchan == cchan2) {
                        mtick2 = notes.get(j).getPulse();

                        if (mtick != mtick2) {
                            notes.get(j).setLength(mtick - mtick2);
                            notes.get(j).setLenght16(mtick - mtick2);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void calculateTransposeForCTonality() {

        int noteId;
        Map<Integer, Integer> mapTrans = new HashMap<>();
        //calcul chaque note pour chaque offset
        for (int offSetDT = 0; offSetDT < 12; offSetDT++) {
            int temp = 0;
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getChannel() != 9) {
                    noteId = (notes.get(i).getNote() + offSetDT) % 12;
                    if (noteId == 1 || noteId == 3 || noteId == 6 || noteId == 8 || noteId == 10) {
                        temp += 1;
                    }
                }
            }
            mapTrans.put(offSetDT, temp);
        }
        //Finding Key associated with max Value in a Java Map
        cTranspose = Collections.min(mapTrans.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

//    private void findPieceLenghtInSemiquaver() {
//
//        int temp = 0;
//
//        for (Note note : notes) {
//            if (note.getPulse16() + note.getLenght16() > temp) {
//                temp = note.getPulse16() + note.getLenght16();
//            }
//        }
//
//        pieceLenght16 = temp;
//
//    }

    public void createMidiFileWithMidiObject() throws InvalidMidiDataException {

        //Create the sequence(midi)
        sequence = new Sequence(getDivisionType(), getResolution());
        track = sequence.createTrack();

        byte[] tempo120 ={(byte)0x07, (byte)0xA1,(byte)0x20};
        track.add(new MidiEvent(new MetaMessage(0x51, tempo120,  3),0));

       for (Map.Entry<Integer, TrackModel> entry : trackModels.entrySet()) {



           track = sequence.createTrack();
           track.add(new MidiEvent(new MetaMessage(0x03, entry.getValue().trackName.getBytes(), entry.getValue().trackName.getBytes().length),0));

           ShortMessage sm = new ShortMessage();
           sm.setMessage(ShortMessage.PROGRAM_CHANGE, entry.getValue().getNotes().get(0).getChannel(), entry.getValue().getNotes().get(0).getInstrument(), 0);
           track.add(new MidiEvent(sm, 0));

          for (int i = 0; i < entry.getValue().getNotes().size(); i++) {




            //loop for each note in notes//

            if (notes.get(i).getOn() == true) {
                ShortMessage on = new ShortMessage();
                on.setMessage(NOTE_ON, entry.getValue().getNotes().get(i).getChannel(), entry.getValue().getNotes().get(i).getNote(), entry.getValue().getNotes().get(i).getVelocity());
                track.add(new MidiEvent(on, entry.getValue().getNotes().get(i).getPulse16()*(getResolution()/4)));

                ShortMessage off = new ShortMessage();
                off.setMessage(NOTE_OFF, entry.getValue().getNotes().get(i).getChannel(), entry.getValue().getNotes().get(i).getNote(), 0);
                track.add(new MidiEvent(off, (entry.getValue().getNotes().get(i).getPulse16() + entry.getValue().getNotes().get(i).getLenght16())*(getResolution()/4)));
            }

        }


        }

        try {
            saveMidiFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveMidiFile() throws IOException {
        String workingDir = System.getProperty("user.dir");

        try {
            MidiSystem.write(sequence,1, new File(workingDir +  "\\src\\main\\java\\midifile\\" + getName()+"(rebooted).mid"));

        } catch (IOException e) {
        }


        File file = new File(workingDir +  "\\src\\main\\java\\midifile\\" + getName()+"(rebooted).mid");
        //Desktop.getDesktop().open(file);

    }


    public int getResolution() {
        return resolution;
    }

    public void setcTranspose(int cTranspose) {
        this.cTranspose = cTranspose;
    }

    public float getBpm() {
        return bpm;
    }

    public int getPieceLenght16() {
        return  pieceLenght16 ;
    }

    public float getDivisionType() {
        return divisionType;
    }

    public String getName() {
        return name;
    }


    public Map<Integer, TrackModel> getTrackNumbers() {
        return trackModels;
    }

   public class Note  {

        private int note;
        private boolean on;
        private int velocity;
        private long pulse;
        private int pulse16;
        private int channel;
        private int tracknumber;
        private long lenght;
        private int lenght16;
        private int octave;
        private String notename;
        private int instrument;
        private int index;
        private int chordAjuste;

        //PUBLIC Properties
        public int getLenght16() { return lenght16; }

        public int getPulse16() { return pulse16; }



        public int getVelocity() {
            return velocity;
        }

        public int getChannel() {
            return channel;
        }

        public boolean getOn() {
            return on;
        }

        public int getTracknumber() {
            return tracknumber;
        }

        //PRIVATE Properties

        public int getNote() {
            return note;
        }

        private void setVelocity(int velocity) {
            this.velocity = velocity;
        }

        private void setChannel(int channel) {
            this.channel = channel;
        }

        private void setTracknumber(int tracknumber) {
            this.tracknumber = tracknumber;
        }

        private void setOctave(int octave) {
            this.octave = octave;
        }

        private void setNotename(String notename) {
            this.notename = notename;
        }

        private long getPulse() {
            return pulse;
        }

        private void setPulse(long pulse) {this.pulse = pulse;}

        private void setLength(long length) {
            this.lenght = length;
        }

       public long getLength() {
           return lenght;
       }

        private void setOn(boolean on) {
            this.on = on;
        }

        private void setIndex(int index) {
            this.index = index;
        }

        private void setNote(int note) {
            this.note = note;
        }

        private void setLenght16(long lenght) {

            if (((int)((lenght / ((double)getResolution()/4))) < 1)){
                this.lenght16= 1;
            }else {
                this.lenght16 = (int)((lenght/((double)getResolution()/4))+.5);
            }
        }

        private void setPulse16(int pulse16) {
            this.pulse16 = pulse16;
        }

       public int getInstrument() {
           return instrument;
       }


       public int transformetoPulse16(long tick) {
           this.pulse16 = (int)((pulse/((double)getResolution()/4))+.5);
           return  pulse16;
       }
   }
}