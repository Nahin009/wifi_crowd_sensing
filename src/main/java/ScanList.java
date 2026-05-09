import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}

public class ScanList {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("DateTime")
    private String DateTime;
    @JsonProperty("DeviceModel")
    private String DeviceModel;
    @JsonProperty("ScanList")
    private List<List<wifiNode>> scanList;

    List<String> SSIDList; //All Captured SSID
    
    Map<String, Integer> countMap; //All Captured SSID vs their count out of 18 (As more than one arrived SSID in a single scan is removed)
    
    Map<String, List<Integer>> sortedStrengthMap; //All Captured SSID vs their strength in ascending order
    
    Map<String, Double> avgStrengthMapAfterRemovingOutliers; //All Captured SSID vs their average strength after removing outliers
    
    Map<String, Double> standardDeviationMap;

    List<wifiNode> averageScanList;
    List<wifiNode> selectedScanList;

    List<String> selectedSSID;

    public ScanList() {
        scanList = new ArrayList<>();
        averageScanList = new ArrayList<>();
        selectedScanList = new ArrayList<>();
        selectedSSID = new ArrayList<>();

    }

    public ScanList(String Datetime, String DeviceModel, List<List<wifiNode>> scanList) {
        this.DateTime = Datetime;
        this.DeviceModel = DeviceModel;
        this.scanList = scanList;
    }


    public String getDatetime() {
        return DateTime;
    }

    public void setDatetime(String Datetime) {
        this.DateTime = Datetime;
    }





    public void RemoveUnknownSSIDs() {
        for (List<wifiNode> wifiNodes : scanList) {
            int size = wifiNodes.size();
            for (int j = 0; j < size; j++) {
                if (wifiNodes.get(j).getSSID().isEmpty()) {
                    wifiNodes.remove(j);
                    j--;
                    size--;
                }
            }
        }
    }

    public void removeDuplicateSSIDs() {
        //if a SSID is found more than once anywhere in a scanList, remove all the SSID with that name from the scanList
        List<String> duplicateSSID = new ArrayList<>();
        for (List<wifiNode> wifiNodes : scanList) {
            int size = wifiNodes.size();
            for (int j = 0; j < size; j++) {
                for (int k = j + 1; k < size; k++) {
                    if (wifiNodes.get(j).getSSID().equals(wifiNodes.get(k).getSSID())) {
                        duplicateSSID.add(wifiNodes.get(j).getSSID());
                    }
                }
            }
        }

        for (List<wifiNode> wifiNodes : scanList) {
            int size = wifiNodes.size();
            for (int j = 0; j < size; j++) {
                if (duplicateSSID.contains(wifiNodes.get(j).getSSID())) {
                    wifiNodes.remove(j);
                    j--;
                    size--;
                }
            }
        }
    }


    public List<String> getSSIDList() {
        RemoveUnknownSSIDs();
        removeDuplicateSSIDs();

        SSIDList = new ArrayList<>();
        for (List<wifiNode> wifiNodes : scanList) {
            for (wifiNode wifiNode : wifiNodes) {
                if (!SSIDList.contains(wifiNode.getSSID())) {
                    SSIDList.add(wifiNode.getSSID());
                }
            }
        }
        return SSIDList;
    }


    public Map<String, Integer> getCountMap() {
        RemoveUnknownSSIDs();
        removeDuplicateSSIDs();
        
        countMap = new HashMap<>();
        for (List<wifiNode> wifiNodes : scanList) {
            for (wifiNode wifiNode : wifiNodes) {
                if (countMap.containsKey(wifiNode.getSSID())) {
                    countMap.put(wifiNode.getSSID(), countMap.get(wifiNode.getSSID()) + 1);
                } else {
                    countMap.put(wifiNode.getSSID(), 1);
                }
            }
        }
        return countMap;
    }
    
    public Map<String, List<Integer>> getSortedStrengthMap() {
        RemoveUnknownSSIDs();
        removeDuplicateSSIDs();
        
        sortedStrengthMap = new HashMap<>();
        for (List<wifiNode> wifiNodes : scanList) {
            for (wifiNode wifiNode : wifiNodes) {
                if (sortedStrengthMap.containsKey(wifiNode.getSSID())) {
                    sortedStrengthMap.get(wifiNode.getSSID()).add(wifiNode.getStrength());
                } else {
                    List<Integer> list = new ArrayList<>();
                    list.add(wifiNode.getStrength());
                    sortedStrengthMap.put(wifiNode.getSSID(), list);
                }
            }
        }
        for (Map.Entry<String, List<Integer>> entry : sortedStrengthMap.entrySet()) {
            entry.getValue().sort(Integer::compareTo);
        }
        return sortedStrengthMap;
    }
    
    public Map<String, Double> getAvgStrengthMapAfterRemovingOutliers() {
        RemoveUnknownSSIDs();
        removeDuplicateSSIDs();
        getSortedStrengthMap();
        
        avgStrengthMapAfterRemovingOutliers = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry : sortedStrengthMap.entrySet()) {
            int size = entry.getValue().size();

            // remove the top and bottom 20% of the strength values

//            int start = (int) Math.ceil(size * 0.2);
//            int end = (int) Math.floor(size * 0.8);
//
//            double sum = 0;
//            for (int i = start; i < end; i++) {
//                sum += entry.getValue().get(i);
//            }
//            avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) (sum / (end - start + 1)));


            if(size  < 5) {
                int sum = 0;
                for (int i = 0; i < size; i++) {
                    sum += entry.getValue().get(i);
                }
                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) Math.round(((double) sum / size) * 100.0) / 100.0);
            } else {
//                int start = (int) Math.ceil(size * 0.2);
//                int end = (int) Math.floor(size * 0.8);
//
//                double sum = 0;
//                for (int i = start; i < end; i++) {
//                    sum += entry.getValue().get(i);
//                }
//                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) Math.round((sum / (end - start + 1)) * 100.0) / 100.0);



                // Use either Interquartile Range (IQR) or Modified Z-Score method
                // Uncomment either method below depending on your use case

                 applyIQR(entry.getKey(), entry.getValue());
//                applyModifiedZScore(entry.getKey(), entry.getValue());
            }


//            if(size  < 5) {
//                int sum = 0;
//                for (int i = 0; i < size; i++) {
//                    sum += entry.getValue().get(i);
//                }
//                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) (sum / size));
//            }
//            else if (size > 15) {
//                int sum = 0;
//                for (int i = 3; i < size - 3; i++) {
//                    sum += entry.getValue().get(i);
//                }
//                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) (sum / (size - 6)));
//            }
//            else if(size > 10) {
//                int sum = 0;
//                for (int i = 2; i < size - 2; i++) {
//                    sum += entry.getValue().get(i);
//                }
//                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) (sum / (size - 4)));
//            }
//            else {
//                int sum = 0;
//                for (int i = 1; i < size - 1; i++) {
//                    sum += entry.getValue().get(i);
//                }
//                avgStrengthMapAfterRemovingOutliers.put(entry.getKey(), (double) (sum / (size - 2)));
//            }
        }

        return avgStrengthMapAfterRemovingOutliers;
    }


    // 1. Interquartile Ranging (IQR) Implementation
    private void applyIQR(String key, List<Integer> strengths) {
        Collections.sort(strengths); // Sorting for quartile calculation

        // Calculate Q1 and Q3
        double Q1 = getPercentile(strengths, 25);
        double Q3 = getPercentile(strengths, 75);
        double IQR = Q3 - Q1;

        // Determine the lower and upper bounds
        double lowerBound = Q1 - 1.5 * IQR;
        double upperBound = Q3 + 1.5 * IQR;

        // Filter out values outside the bounds
        List<Integer> filteredValues = new ArrayList<>();
        for (int value : strengths) {
            if (value >= lowerBound && value <= upperBound) {
                filteredValues.add(value);
            }
        }

        if (filteredValues.isEmpty()) {
            avgStrengthMapAfterRemovingOutliers.put(key, -90.0); // Handle edge case
        } else {
            // Calculate the average of the filtered values
            double sum = 0;
            for (int value : filteredValues) {
                sum += value;
            }
            double avg = sum / filteredValues.size();
            avgStrengthMapAfterRemovingOutliers.put(key, (double) Math.round(avg * 100.0) / 100.0);
        }
    }

    // Helper method to calculate percentile
    private double getPercentile(List<Integer> values, double percentile) {
        int size = values.size();
        double rank = percentile / 100.0 * (size - 1);
        int lowerIndex = (int) Math.floor(rank);
        int upperIndex = (int) Math.ceil(rank);

        if (lowerIndex == upperIndex) {
            return values.get(lowerIndex);
        }

        double lowerValue = values.get(lowerIndex);
        double upperValue = values.get(upperIndex);

        return lowerValue + (rank - lowerIndex) * (upperValue - lowerValue); // Linear interpolation for percentiles
    }

    // 2. Modified Z-Score Implementation
    private void applyModifiedZScore(String key, List<Integer> strengths) {
        int size = strengths.size();

        // Check for empty or null input
        if (size == 0) {
            avgStrengthMapAfterRemovingOutliers.put(key, -90.0); // Handle invalid input case
            return;
        }

        // Calculate the median
        double median = getMedian(strengths);

        // Calculate the Median Absolute Deviation (MAD)
        List<Double> deviations = new ArrayList<>();
        for (int value : strengths) {
            deviations.add(Math.abs(value - median));
        }
        double MAD = getMedian(deviations);

        // If MAD is 0, avoid division by zero
        if (MAD == 0) {
            avgStrengthMapAfterRemovingOutliers.put(key, median); // All values are same, no need to filter
            return;
        }

        // Modified Z-Score threshold (typically 3.5)
        double threshold = 3.5;

        // Calculate the Modified Z-Score and filter out outliers
        List<Integer> filteredValues = new ArrayList<>();
        for (int value : strengths) {
            double modifiedZScore = 0.6745 * (value - median) / MAD;
            if (Math.abs(modifiedZScore) <= threshold) {
                filteredValues.add(value);
            }
        }

        // Handle case where all values are considered outliers
        if (filteredValues.size() == 0) {
            avgStrengthMapAfterRemovingOutliers.put(key, -90.0); // No valid data left after filtering
        } else {
            // Calculate the average of the filtered values
            double sum = 0;
            for (int value : filteredValues) {
                sum += value;
            }
            double avg = sum / filteredValues.size();
            avgStrengthMapAfterRemovingOutliers.put(key, Math.round(avg * 100.0) / 100.0); // Round to 2 decimal places
        }
    }

    // Helper method to calculate the median
    private double getMedian(List<? extends Number> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty.");
        }

        Collections.sort(values, (a, b) -> Double.compare(a.doubleValue(), b.doubleValue()));
        int size = values.size();
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1).doubleValue() + values.get(size / 2).doubleValue()) / 2.0;
        } else {
            return values.get(size / 2).doubleValue();
        }
    }


//    public Map<String, Double> getStandardDeviationListFromAvgStrengthMap() {
//        RemoveUnknownSSIDs();
//        removeDuplicateSSIDs();
//        getAvgStrengthMapAfterRemovingOutliers();
//
//        standardDeviationMap = new HashMap<>();
//        for (Map.Entry<String, List<Integer>> entry : sortedStrengthMap.entrySet()) {
//            int size = entry.getValue().size();
//            if(size  <= 5) {
//                double sum = 0;
//                for (int i = 0; i < size; i++) {
//                    sum += Math.pow(entry.getValue().get(i) - avgStrengthMapAfterRemovingOutliers.get(entry.getKey()), 2);
//                }
//                standardDeviationMap.put(entry.getKey(), (double) Math.sqrt(sum / size));
//            }
//            else if (size > 15) {
//                double sum = 0;
//                for (int i = 3; i < size - 3; i++) {
//                    sum += Math.pow(entry.getValue().get(i) - avgStrengthMapAfterRemovingOutliers.get(entry.getKey()), 2);
//                }
//                standardDeviationMap.put(entry.getKey(), (double) Math.sqrt(sum / (size - 6)));
//            }
//            else if(size > 10) {
//                double sum = 0;
//                for (int i = 2; i < size - 2; i++) {
//                    sum += Math.pow(entry.getValue().get(i) - avgStrengthMapAfterRemovingOutliers.get(entry.getKey()), 2);
//                }
//                standardDeviationMap.put(entry.getKey(), (double) Math.sqrt(sum / (size - 4)));
//            }
//            else {
//                double sum = 0;
//                for (int i = 1; i < size - 1; i++) {
//                    sum += Math.pow(entry.getValue().get(i) - avgStrengthMapAfterRemovingOutliers.get(entry.getKey()), 2);
//                }
//                standardDeviationMap.put(entry.getKey(), (double) Math.sqrt(sum / (size - 2)));
//            }
//        }
//        return standardDeviationMap;
//
//    }


}
