import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSV_making {
    public static void main(String[] args) throws IOException {
        String JsonFilePath = "src/main/device_compare/extra_204.json";
//        String JsonFilePath = "src/main/203_ALL_new.json";

        // Read JSON file and convert to Java object
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        MakeRefSSIDs makeRefSSIDs = new MakeRefSSIDs();
        TestingData testingData;
        List<String> refPointsInside = new ArrayList<>();
        List<String> refPointsOutside = new ArrayList<>();
        List<String> testPointsInside = new ArrayList<>();
        List<String> testPointsOutside = new ArrayList<>();

        List<String> selectedSSID = new ArrayList<>();

//        selectedSSID.add("Galaxy M124213"); //204 203
        selectedSSID.add("CSE-204"); //204 203
        selectedSSID.add("CSE-303"); //204 203
        selectedSSID.add("CSE-304"); //204 203
        selectedSSID.add("Hall of Fame"); //204 203
        selectedSSID.add("DataLab@BUET"); //204 203
        selectedSSID.add("CSE-206"); //204 203
        selectedSSID.add("CSE-108"); //204
        selectedSSID.add("CSE-404"); //204
        selectedSSID.add("CSE-G04"); //204 203
        selectedSSID.add("CSE-G07"); //204 203
        selectedSSID.add("CSE-306"); //204 203
//        selectedSSID.add("CSE-G09"); //204(weak)
        selectedSSID.add("dlink"); //204 203
//        selectedSSID.add("CSE-401"); //203


        TypeReference<Map<String, Map<String, ScanList>>> typeReference = new TypeReference<>() {};
        Map<String, Map<String, ScanList>> roomPosList = mapper.readValue(new File(JsonFilePath), typeReference);

        // categorize the inside and outside ref points and test points
        for (Map.Entry<String, Map<String, ScanList>> roomPosNo : roomPosList.entrySet()) {
            String roomPos = roomPosNo.getKey();
            if(roomPos.contains("testing")) {
                if(roomPos.endsWith("_i")) {
                    testPointsInside.add(roomPos);
                } else {
                    testPointsOutside.add(roomPos);
                }
            }
            else {
                if(roomPos.endsWith("_i")) {
                    refPointsInside.add(roomPos);
                } else {
                    refPointsOutside.add(roomPos);
                }
            }
        }

        makeRefSSIDs.setRoomPosList(roomPosList);

        Map<String, AvgStrengthMap> AllPosAvgStrengthMapOfSelectedSSIDs = makeRefSSIDs.getRefVectors(selectedSSID);

        // Write to CSV AllPosAvgStrengthMapOfSelectedSSIDs manually
//        String csvRefFilePath = "src/main/204_all_ref_modified.csv";
//        String csvTestFilePath = "src/main/204_all_test_modified.csv";
        String csvRefFilePath = "src/main/device_compare/204_1_aaaaaaaa.csv";
        String csvTestFilePath = "src/main/device_compare/204_3.csv";

        BufferedWriter RefWriter = new BufferedWriter(new FileWriter(csvRefFilePath));
        BufferedWriter TestWriter = new BufferedWriter(new FileWriter(csvTestFilePath));

        // Write the header
        RefWriter.write(",");
        TestWriter.write(",");
        for (String SSID : selectedSSID) {
            RefWriter.write(SSID + ",");
            TestWriter.write(SSID + ",");
        }

        RefWriter.write("\n");
        TestWriter.write("\n");

        // Write the data
        for (Map.Entry<String, AvgStrengthMap> entry : AllPosAvgStrengthMapOfSelectedSSIDs.entrySet()) {
            if(entry.getKey().contains("testing")) {
                TestWriter.write(entry.getKey() + ",");
                for (String SSID : selectedSSID) {
                    if (entry.getValue().map.containsKey(SSID)) {
                        TestWriter.write(entry.getValue().map.get(SSID) + ",");
                        System.out.println(entry.getKey() + " : " + entry.getValue().map.get(SSID));
                    } else {
                        TestWriter.write(",");
                    }
                }

                if(entry.getKey().endsWith("_i")) {
                    TestWriter.write("1");
                } else {
                    TestWriter.write("0");
                }

                TestWriter.write("\n");
            }
            else {
                RefWriter.write(entry.getKey() + ",");
                for (String SSID : selectedSSID) {
                    if (entry.getValue().map.containsKey(SSID)) {
                        RefWriter.write(entry.getValue().map.get(SSID) + ",");
                        System.out.println(entry.getKey() + " : " + entry.getValue().map.get(SSID));
                    } else {
                        RefWriter.write(",");
                    }
                }

                if(entry.getKey().endsWith("_i")) {
                    RefWriter.write("1");
                } else {
                    RefWriter.write("0");
                }

                RefWriter.write("\n");
            }
        }

        RefWriter.close();
        TestWriter.close();

        System.out.println("CSV file has been created successfully.");



    }
}
