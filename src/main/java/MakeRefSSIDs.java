import java.util.*;


public class MakeRefSSIDs {
    Map<String, Map<String, ScanList>> roomPosList;
    Map<String, AvgStrengthMap> AllPosAvgStrengthMapOfSelectedSSIDs; // roomPosNo, AvgStrengthMap(SSID, avg strength)
    Map<String, Integer> AllSSIDsCountMap;

    public MakeRefSSIDs() {
        roomPosList = new HashMap<>();
        AllPosAvgStrengthMapOfSelectedSSIDs = new HashMap<>();
        AllSSIDsCountMap = new HashMap<>();
    }

    public void setRoomPosList(Map<String, Map<String, ScanList>> roomPosList) {
        this.roomPosList = roomPosList;
    }

    public Map<String, Integer> getAllSSIDsCountMap() {
        for (Map.Entry<String, Map<String, ScanList>> roomPosNo : roomPosList.entrySet()) {
            for (Map.Entry<String, ScanList> scanList : roomPosNo.getValue().entrySet()) {
                List<String> SSIDs = scanList.getValue().getSSIDList();
                for (String SSID : SSIDs) {
                    if (AllSSIDsCountMap.containsKey(SSID)) {
                        AllSSIDsCountMap.put(SSID, AllSSIDsCountMap.get(SSID) + 1);
                    } else {
                        AllSSIDsCountMap.put(SSID, 1);
                    }
                }
            }
        }

        // sort the SSIDs by count in descending order

        // Convert HashMap to List of Map.Entry
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(AllSSIDsCountMap.entrySet());

        // Sort the list based on values in descending order
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Create a new LinkedHashMap to store the sorted entries
        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        // Iterate through the sorted list and put entries into the new LinkedHashMap
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public Map<String, AvgStrengthMap> getRefVectors(List<String> selectedSSIDList) {
        for (Map.Entry<String, Map<String, ScanList>> roomPosNo : roomPosList.entrySet()) {
            for (Map.Entry<String, ScanList> scanList : roomPosNo.getValue().entrySet()) {
                AvgStrengthMap avgStrengthMap = new AvgStrengthMap(scanList.getValue().getAvgStrengthMapAfterRemovingOutliers());

                AvgStrengthMap avgStrengthMapOfSelectedSSIDs = new AvgStrengthMap();
                for (String SSID : selectedSSIDList) {
                    if(avgStrengthMap.map.containsKey(SSID)) {
                        avgStrengthMapOfSelectedSSIDs.map.put(SSID, avgStrengthMap.map.get(SSID));
                    }
                    else {
                        avgStrengthMapOfSelectedSSIDs.map.put(SSID, 0.0);
                    }
                }
                AllPosAvgStrengthMapOfSelectedSSIDs.put(roomPosNo.getKey(), avgStrengthMapOfSelectedSSIDs);
            }
        }
        return AllPosAvgStrengthMapOfSelectedSSIDs;
    }


}
