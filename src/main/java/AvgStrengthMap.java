import java.util.HashMap;
import java.util.Map;

public class AvgStrengthMap {
    Map<String, Double> map;  // SSID, avg strength

    public AvgStrengthMap() {
        map = new HashMap<>();
    }

    public AvgStrengthMap(Map<String, Double> map) {
        this.map = map;
    }
}