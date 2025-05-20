import java.util.HashMap;
import java.util.Map;

public class PriceFactory {
    private static final Map<Double, Price> pCache = new HashMap<>();



    public static Price makePrice( String priceString) throws TradingException {
        if (priceString == null || !priceString.matches("\\$?\\d+(\\.\\d{2})?")) {
            throw new TradingException("Invalid price string: " + priceString);
        }
        double value = Double.parseDouble(priceString.replace("$", ""));
        value = Math.round(value * 100.0) / 100.0;
        return pCache.computeIfAbsent(value, key -> {
            try {
                return new Price(key);
            } catch (TradingException e) {
                throw new RuntimeException("Unexpected error creating Price: " + e.getMessage(), e);
            }
        });

    }
    public static Price makePrice(int value) {
        double dvalue = (double) value /100;
        return pCache.computeIfAbsent(dvalue, key -> {
            try {
                return new Price(key);
            } catch (TradingException e) {
                throw new RuntimeException("Unexpected error creating Price: " + e.getMessage(), e);
            }
        });
    }

}