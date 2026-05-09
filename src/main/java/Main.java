import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String JsonFilePath = "src/main/204_ALL.json";

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

        selectedSSID.add("Galaxy M124213"); //204 203
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
        selectedSSID.add("CSE-G09"); //204
        selectedSSID.add("dlink"); //204 203
//        selectedSSID.add("CSE-401"); //203


        try {
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

            //print out the ref points and test points of inside and outside
//            System.out.println("Ref Points Inside: " + refPointsInside);
//            System.out.println("Ref Points Outside: " + refPointsOutside);
//            System.out.println("Test Points Inside: " + testPointsInside);
//            System.out.println("Test Points Outside: " + testPointsOutside);

            makeRefSSIDs.setRoomPosList(roomPosList);

            Map<String, Integer> AllSSIDsCountMap = makeRefSSIDs.getAllSSIDsCountMap();
            //print out the SSIDs and their count
//            for (Map.Entry<String, Integer> entry : AllSSIDsCountMap.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }

            Map<String, AvgStrengthMap> AllPosAvgStrengthMapOfSelectedSSIDs = makeRefSSIDs.getRefVectors(selectedSSID);
            //print out the ref points and their avg strength of selected SSIDs
            for (Map.Entry<String, AvgStrengthMap> entry : AllPosAvgStrengthMapOfSelectedSSIDs.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue().map);
            }

//
//            testingData = new TestingData(refPointsInside, refPointsOutside, testPointsInside, testPointsOutside, AllPosAvgStrengthMapOfSelectedSSIDs);
//            testingData.KNN_With_MajorityVoting();
//            testingData.KNN_With_CoOrdinate_Checking();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

