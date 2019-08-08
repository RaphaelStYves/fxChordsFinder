package createCSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CsvAllPulseAllChords {




    public CsvAllPulseAllChords(Map<String, Map<Integer, Integer>> mapScoreByChordAllPulses, Map<String, List<Integer>> mapTableForceChords){

        PrintWriter pw;
        String workingDir = System.getProperty("user.dir");

        try {
            pw = new PrintWriter(new File(workingDir +  "\\src\\main\\java\\midifile\\test.csv"));

            StringBuffer csvHeader = new StringBuffer("");
            StringBuffer csvData = new StringBuffer("");

            csvHeader.append('c');
            csvHeader.append(';');

            for (int i = 0; i <mapScoreByChordAllPulses.get("C").size() ; i++) {
                csvHeader.append(i);
                csvHeader.append(';');
                         }
            csvHeader.append('\n');


            // write header
            pw.write(csvHeader.toString());

            // write data


            for (String chord : mapTableForceChords.keySet()) {
                csvData.append(chord);
                csvData.append(';');
                for (int i = 0; i < mapScoreByChordAllPulses.get(chord).size() ; i++) {
                    csvData.append(mapScoreByChordAllPulses.get(chord).get(i));
                    csvData.append(';');
                }
                csvData.append('\n');
            }

            pw.write(csvData.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      }




}
