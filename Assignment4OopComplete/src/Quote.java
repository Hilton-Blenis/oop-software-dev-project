public class Quote {
    private final String user;
    private final String product;
    private final QuoteSide buyside;
    private final QuoteSide sellside;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName)
            throws TradingException {
        if (userName == null) {
            throw new TradingException("User code: invalid, cannot be null");
        }
        if (!userName.matches("[A-Z]{3}")) {
            throw new TradingException("User code: invalid: " + userName);
        }
        this.user = userName;

        if (!symbol.matches("[A-Z0-9.]{1,5}")) {
            throw new TradingException("Invalid symbol");
        }
        this.product = symbol;

        if (buyPrice == null || sellPrice == null) {
            throw new TradingException("Invalid price: cannot be null");
        }

        this.buyside = new QuoteSide(userName, symbol, buyPrice, buyVolume, GlobalConstants.BookSide.BUY);
        this.sellside = new QuoteSide(userName, symbol, sellPrice, sellVolume, GlobalConstants.BookSide.SELL);
    }
    public String getSymbol() {
        return this.product;
    }


    public Tradable getQuoteSide(GlobalConstants.BookSide bookSide) {
        return bookSide == GlobalConstants.BookSide.BUY ? buyside : sellside;
    }

    public String getUser() {
            return this.user;
    }
    @Override
    public String toString() {
        return String.format("Quote{User: %s, Product: %s, Buy Price: %s, Buy Volume: %d, Sell Price: %s, Sell Volume: %d}",
                user, product, buyside.getPrice(), buyside.getOriginalVolume(),
                sellside.getPrice(), sellside.getOriginalVolume());
    }

}
