public class CurrentMarketTracker {
    private static CurrentMarketTracker instance;

    private CurrentMarketTracker() { }

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) {
        double marketWidth = 0.0;
        if (buyPrice != null && sellPrice != null) {
            marketWidth = sellPrice.getValue() - buyPrice.getValue();
        }

        CurrentMarketSide buySide = new CurrentMarketSide(buyPrice, buyVolume);
        CurrentMarketSide sellSide = new CurrentMarketSide(sellPrice, sellVolume);


        System.out.println("*********** Current Market ***********");
        System.out.printf("* %s   %s - %s [$%.2f]%n", symbol,
                buySide.toString(), sellSide.toString(), marketWidth);
        System.out.println("**************************************");


        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }
}