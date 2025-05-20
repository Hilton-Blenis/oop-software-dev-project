import java.util.HashMap;

public class User implements CurrentMarketObserver {
    private final String userId;
    private final HashMap<String, TradableDTO> tradables;
    private final HashMap<String, String> currentMarkets;

    public User(String userId) throws TradingException {
        setUserId(userId);
        this.userId = userId;
        this.tradables = new HashMap<>();
        this.currentMarkets = new HashMap<>();
    }

    private void setUserId(String userId) throws TradingException {
        if (userId == null || !userId.matches("[A-Z]{3}")) {
            throw new TradingException("User ID must be exactly 3 uppercase letters: " + userId);
        }
    }

    public void updateTradable(TradableDTO o) {
        if (o != null) {
            tradables.put(o.tradableId(), o);
        }
    }

    // Implementing the CurrentMarketObserver interface
    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        String marketString = symbol + "   " + buySide.toString() + " - " + sellSide.toString();
        currentMarkets.put(symbol, marketString);
    }

    // Method to retrieve current markets for display
    public String getCurrentMarkets() {
        if (currentMarkets.isEmpty()) {
            return "No current markets available.";
        }
        StringBuilder sb = new StringBuilder();
        for (String market : currentMarkets.values()) {
            sb.append(market).append("\n");
        }
        return sb.toString().trim();
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User Id: " + userId + "\n");
        for (TradableDTO dto : tradables.values()) {
            sb.append(String.format("Product: %s, Price: %s, OriginalVolume: %d, RemainingVolume: %d, CancelledVolume: %d, FilledVolume: %d, User: %s, Side: %s, Id: %s\n",
                    dto.product(), dto.price(), dto.originalVolume(), dto.remainingVolume(),
                    dto.cancelledVolume(), dto.filledVolume(), dto.user(), dto.side(), dto.tradableId()));
        }
        return sb.toString();
    }
}