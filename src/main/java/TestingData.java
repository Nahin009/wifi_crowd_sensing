import java.util.*;

public class TestingData {
    private static final int K = 5;

    List<String> refPointsInside, refPointsOutside, testPointsInside, testPointsOutside;
    List<String> AllRefPoints, AllTestPoints;

    Map<String, AvgStrengthMap> AllPosAvgStrengthMapOfSelectedSSIDs;
    Map<String, AvgStrengthMap> refPoints, testPoints;

    Coordinates coordinates;
    Map<String, Pair<Integer, Integer>> posGridMapping;
    BoundaryPoints boundaryPoints;

    public TestingData(List<String> refPointsInside, List<String> refPointsOutside, List<String> testPointsInside, List<String> testPointsOutside, Map<String, AvgStrengthMap> AllPosAvgStrengthMapOfSelectedSSIDs) {
        this.refPointsInside = refPointsInside;
        this.refPointsOutside = refPointsOutside;
        this.testPointsInside = testPointsInside;
        this.testPointsOutside = testPointsOutside;
        AllRefPoints = this.refPointsInside;
        AllRefPoints.addAll(this.refPointsOutside);
        AllTestPoints = this.testPointsInside;
        AllTestPoints.addAll(this.testPointsOutside);

        this.AllPosAvgStrengthMapOfSelectedSSIDs = AllPosAvgStrengthMapOfSelectedSSIDs;

        refPoints = new HashMap<>();
        testPoints = new HashMap<>();
        for (String refPoint : AllRefPoints) {
            refPoints.put(refPoint, AllPosAvgStrengthMapOfSelectedSSIDs.get(refPoint));
        }
        for (String testPoint : AllTestPoints) {
            testPoints.put(testPoint, AllPosAvgStrengthMapOfSelectedSSIDs.get(testPoint));
        }

        coordinates = new Coordinates();
        posGridMapping = coordinates.getposGridMapping();
        boundaryPoints = coordinates.getBoundaryPoints();
    }

    private Double EuclideanDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.pow(entry.getValue() - testPoint.map.get(entry.getKey()), 2);
        }
        return Math.sqrt(distance);
    }

    private Double ManhattanDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.abs(entry.getValue() - testPoint.map.get(entry.getKey()));
        }
        return distance;
    }

    private Double ChebyshevDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance = Math.max(distance, Math.abs(entry.getValue() - testPoint.map.get(entry.getKey())));
        }
        return distance;
    }

    private Double MinkowskiDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        int p = 2;
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.pow(Math.abs(entry.getValue() - testPoint.map.get(entry.getKey())), p);
        }
        return Math.pow(distance, 1.0 / p);
    }

    private Double SimpleDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += entry.getValue() - testPoint.map.get(entry.getKey());
        }
        return distance;
    }

    private Double DotProduct(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double dotProduct = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            dotProduct += entry.getValue() * testPoint.map.get(entry.getKey());
        }
        return dotProduct;
    }

    private Double CosineSimilarity(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double dotProduct = DotProduct(refPoint, testPoint);
        double refPointMagnitude = DotProduct(refPoint, refPoint);
        refPointMagnitude = Math.sqrt(refPointMagnitude);
        double testPointMagnitude = DotProduct(testPoint, testPoint);
        testPointMagnitude = Math.sqrt(testPointMagnitude);
        return (refPointMagnitude * testPointMagnitude) / dotProduct;
    }

    private Double JaccardSimilarity(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double dotProduct = DotProduct(refPoint, testPoint);
        double refPointMagnitude = DotProduct(refPoint, refPoint);
        double testPointMagnitude = DotProduct(testPoint, testPoint);
        return dotProduct / (refPointMagnitude + testPointMagnitude - dotProduct);
    }

    private Double PearsonCorrelation(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double refPointSum = 0.0;
        double testPointSum = 0.0;
        double refPointSumSquare = 0.0;
        double testPointSumSquare = 0.0;
        double refPointTestPointSum = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            refPointSum += entry.getValue();
            refPointSumSquare += Math.pow(entry.getValue(), 2);
            testPointSum += testPoint.map.get(entry.getKey());
            testPointSumSquare += Math.pow(testPoint.map.get(entry.getKey()), 2);
            refPointTestPointSum += entry.getValue() * testPoint.map.get(entry.getKey());
        }
        int n = refPoint.map.size();
        return (n * refPointTestPointSum - refPointSum * testPointSum) / (Math.sqrt(n * refPointSumSquare - Math.pow(refPointSum, 2)) * Math.sqrt(n * testPointSumSquare - Math.pow(testPointSum, 2)));
    }

    private Double SpearmanCorrelation(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        int n = refPoint.map.size();
        double refPointSum = 0.0;
        double testPointSum = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            refPointSum += entry.getValue();
            testPointSum += testPoint.map.get(entry.getKey());
        }
        double refPointMean = refPointSum / n;
        double testPointMean = testPointSum / n;
        double refPointSumSquare = 0.0;
        double testPointSumSquare = 0.0;
        double refPointTestPointSum = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            refPointSumSquare += Math.pow(entry.getValue() - refPointMean, 2);
            testPointSumSquare += Math.pow(testPoint.map.get(entry.getKey()) - testPointMean, 2);
            refPointTestPointSum += (entry.getValue() - refPointMean) * (testPoint.map.get(entry.getKey()) - testPointMean);
        }
        return refPointTestPointSum / (Math.sqrt(refPointSumSquare) * Math.sqrt(testPointSumSquare));
    }

    private Double MahalanobisDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        double refPointSum = 0.0;
        double testPointSum = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            refPointSum += entry.getValue();
            testPointSum += testPoint.map.get(entry.getKey());
        }
        double refPointMean = refPointSum / refPoint.map.size();
        double testPointMean = testPointSum / testPoint.map.size();
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.pow(entry.getValue() - testPoint.map.get(entry.getKey()), 2) / (Math.pow(refPointMean - testPointMean, 2));
        }
        return Math.sqrt(distance);
    }

    private Double CanberraDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.abs(entry.getValue() - testPoint.map.get(entry.getKey())) / (Math.abs(entry.getValue()) + Math.abs(testPoint.map.get(entry.getKey())));
        }
        return distance;
    }

    private Double BrayCurtisDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        double refPointSum = 0.0;
        double testPointSum = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            refPointSum += entry.getValue();
            testPointSum += testPoint.map.get(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.abs(entry.getValue() - testPoint.map.get(entry.getKey())) / (refPointSum + testPointSum);
        }
        return distance;
    }

    private Double HaversineDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += Math.pow(entry.getValue() - testPoint.map.get(entry.getKey()), 2);
        }
        return 2 * Math.asin(Math.sqrt(distance));
    }

    private Double HammingDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double distance = 0.0;
        for (Map.Entry<String, Double> entry : refPoint.map.entrySet()) {
            distance += entry.getValue() != testPoint.map.get(entry.getKey()) ? 1 : 0;
        }
        return distance;
    }

    private Double JaccardDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double dotProduct = DotProduct(refPoint, testPoint);
        double refPointMagnitude = DotProduct(refPoint, refPoint);
        double testPointMagnitude = DotProduct(testPoint, testPoint);
        return 1 - dotProduct / (refPointMagnitude + testPointMagnitude - dotProduct);
    }

    private Double TanimotoDistance(AvgStrengthMap refPoint, AvgStrengthMap testPoint) {
        double dotProduct = DotProduct(refPoint, testPoint);
        double refPointMagnitude = DotProduct(refPoint, refPoint);
        double testPointMagnitude = DotProduct(testPoint, testPoint);
        return 1 - dotProduct / (refPointMagnitude + testPointMagnitude - dotProduct);
    }


    public void KNN_With_MajorityVoting() {
        int matchedCount = 0;
        int unmatchedCount = 0;
        // For every test Points, find the K nearest ref points
        for (Map.Entry<String, AvgStrengthMap> testPoint : testPoints.entrySet()) {
            Map<String, Double> distanceMap = new HashMap<>();
            for (Map.Entry<String, AvgStrengthMap> refPoint : refPoints.entrySet()) {
                distanceMap.put(refPoint.getKey(), ManhattanDistance(refPoint.getValue(), testPoint.getValue()));
            }
            List<Map.Entry<String, Double>> distanceList = new ArrayList<>(distanceMap.entrySet());
            distanceList.sort(Map.Entry.comparingByValue());
            Map<String, Integer> countMap = new HashMap<>();
            for (int i = 0; i < K; i++) {
                String refPoint = distanceList.get(i).getKey();
                System.out.println(refPoint);
                if (refPoint.endsWith("_i")) {
                    countMap.put("inside", countMap.getOrDefault("inside", 0) + 1);
                } else {
                    countMap.put("outside", countMap.getOrDefault("outside", 0) + 1);
                }
            }
            if (countMap.getOrDefault("inside", 0) > countMap.getOrDefault("outside", 0)) {
                // Test the result is true or false
                if (testPoint.getKey().endsWith("_i")) {
                    System.out.println(testPoint.getKey() + " -> true");
                    matchedCount++;
                } else {
                    System.out.println(testPoint.getKey() + " -> false");
                    unmatchedCount++;
                }
            } else {
                // Test the result is true or false
                if (testPoint.getKey().endsWith("_i")) {
                    System.out.println(testPoint.getKey() + " -> false");
                    unmatchedCount++;
                } else {
                    System.out.println(testPoint.getKey() + " -> true");
                    matchedCount++;
                }
            }
        }
        System.out.println("Matched: " + matchedCount);
        System.out.println("Unmatched: " + unmatchedCount);
    }


    public void KNN_With_CoOrdinate_Checking() {
        int matchedCount = 0;
        int unmatchedCount = 0;
        // For every test Points, find the K nearest ref points
        for (Map.Entry<String, AvgStrengthMap> testPoint : testPoints.entrySet()) {
            Map<String, Double> distanceMap = new HashMap<>();
            for (Map.Entry<String, AvgStrengthMap> refPoint : refPoints.entrySet()) {
                distanceMap.put(refPoint.getKey(), CanberraDistance(refPoint.getValue(), testPoint.getValue()));
            }
            List<Map.Entry<String, Double>> distanceList = new ArrayList<>(distanceMap.entrySet());
            distanceList.sort(Map.Entry.comparingByValue()); // Sort the distance list

            // Get the K nearest ref points and put them into a list
            List<String> KNearestRefPoints = new ArrayList<>();
            for (int i = 0; i < K; i++) {
                KNearestRefPoints.add(distanceList.get(i).getKey());
            }

            // Find the average of the coordinates of the K nearest ref points
            double x = 0.0;
            double y = 0.0;
            for (String refPoint : KNearestRefPoints) {
                Pair<Integer, Integer> pos = posGridMapping.get(refPoint);
                x += pos.getFirst();
                y += pos.getSecond();
            }

            x /= K;
            y /= K;

            // Check if the average coordinates are inside or outside
            if (boundaryPoints.isInside(x, y)) {
                // Test the result is true or false
                if (testPoint.getKey().endsWith("_i")) {
                    System.out.println(testPoint.getKey() + " -> true");
                    matchedCount++;
                } else {
                    System.out.println(testPoint.getKey() + " -> false");
                    unmatchedCount++;
                }
            } else {
                // Test the result is true or false
                if (testPoint.getKey().endsWith("_i")) {
                    System.out.println(testPoint.getKey() + " -> false");
                    unmatchedCount++;
                } else {
                    System.out.println(testPoint.getKey() + " -> true");
                    matchedCount++;
                }
            }

        }
        System.out.println("Matched: " + matchedCount);
        System.out.println("Unmatched: " + unmatchedCount);
    }

}
