
public class ProductBook {
    private final String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws TradingException {
        if (product == null || !product.matches("^[A-Z0-9.]{1,5}$")) {
            throw new TradingException("Invalid product symbol.");
        }
        this.product = product;
        this.buySide = new ProductBookSide(GlobalConstants.BookSide.BUY);
        this.sellSide = new ProductBookSide(GlobalConstants.BookSide.SELL);
    }

    public TradableDTO add(Tradable t) throws TradingException {
        if (t == null) {
            throw new TradingException("Tradable cannot be null.");
        }
        TradableDTO dto;
        if (t.getSide() == GlobalConstants.BookSide.BUY) {
            dto = buySide.add(t);
        } else {
            dto = sellSide.add(t);
        }
        tryTrade();
        updateMarket();
        return dto;
    }

    public TradableDTO[] add(Quote qte) throws TradingException {
        if (qte == null) {
            throw new TradingException("Quote cannot be null.");
        }
        removeQuotesForUser(qte.getUser());
        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(GlobalConstants.BookSide.BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(GlobalConstants.BookSide.SELL));
        tryTrade();
        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public TradableDTO cancel(GlobalConstants.BookSide side, String orderId) throws TradingException {
        if (orderId == null) {
            throw new TradingException("Invalid order ID for cancellation.");
        }
        TradableDTO dto;
        if (side == GlobalConstants.BookSide.BUY) {
            dto = buySide.cancel(orderId);
        } else {
            dto = sellSide.cancel(orderId);
        }
        updateMarket();
        return dto;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws TradingException {
        if (userName == null) {
            throw new TradingException("Invalid username for quote removal.");
        }
        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);
        updateMarket();
        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public void tryTrade() throws TradingException {
        Price topBuyPrice = buySide.topOfBookPrice();
        Price topSellPrice = sellSide.topOfBookPrice();
        if (topBuyPrice == null || topSellPrice == null) return;
        while (topBuyPrice != null && topSellPrice != null
                && topBuyPrice.isGreaterThanOrEqual(topSellPrice)) {
            int buyVol = buySide.topOfBookVolume();
            int sellVol = sellSide.topOfBookVolume();
            int totalToTrade = Math.min(buyVol, sellVol);
            buySide.tradeOut(topSellPrice, totalToTrade);
            sellSide.tradeOut(topBuyPrice, totalToTrade);
            topBuyPrice = buySide.topOfBookPrice();
            topSellPrice = sellSide.topOfBookPrice();
        }
    }

    private void updateMarket() {
        Price buyPrice = buySide.topOfBookPrice();
        int buyVolume = buySide.topOfBookVolume();
        Price sellPrice = sellSide.topOfBookPrice();
        int sellVolume = sellSide.topOfBookVolume();
        CurrentMarketTracker.getInstance().updateMarket(product, buyPrice, buyVolume, sellPrice, sellVolume);
    }

    public String getTopOfBookString(GlobalConstants.BookSide side) {
        Price topPrice;
        int topVolume;
        if (side == GlobalConstants.BookSide.BUY) {
            topPrice = buySide.topOfBookPrice();
            topVolume = buySide.topOfBookVolume();
        } else {
            topPrice = sellSide.topOfBookPrice();
            topVolume = sellSide.topOfBookVolume();
        }
        if (topPrice == null || topPrice.getValue() == 0.0) {
            return "Top of " + side + " book: $0.00 x 0";
        }
        return "Top of " + side + " book: " + topPrice + " x " + topVolume;
    }

    @Override
    public String toString() {
        return "--------------------------------------------\n" +
                "Product Book: " + product + "\n" +
                buySide + "\n" +
                sellSide + "\n" +
                "--------------------------------------------\n";
    }
}