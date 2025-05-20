import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentMarketPublisher {
    private static final CurrentMarketPublisher instance = new CurrentMarketPublisher();
    public static CurrentMarketPublisher getInstance() {
        return instance;
    }
    private CurrentMarketPublisher() {
        filters = new HashMap<>();
    }

    private final Map<String, List<CurrentMarketObserver>> filters;

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (!filters.containsKey(symbol)) {
            filters.put(symbol, new ArrayList<>());
        }
        if (!filters.get(symbol).contains(cmo)) {
            filters.get(symbol).add(cmo);
        }
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (!filters.containsKey(symbol)) {
            return;
        }
        filters.get(symbol).remove(cmo);
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        if (!filters.containsKey(symbol)) {
            return;
        }
        for (CurrentMarketObserver obs : filters.get(symbol)) {
            obs.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}