public class QuoteSide implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private final String id;
    private final GlobalConstants.BookSide side;

    public QuoteSide(String user, String product, Price price, int originalVolume, GlobalConstants.BookSide side)
            throws TradingException {
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new TradingException("User code must be exactly 3 uppercase letters: " + user);
        }
        this.user = user;

        if (!product.matches("[A-Z0-9.]{1,5}")) {
            throw new TradingException("Product code invalid");
        }
        this.product = product;

        if (price == null) {
            throw new TradingException("Price invalid: can't be null");
        }
        this.price = price;

        if (originalVolume <= 0 || originalVolume >= 10_000) {
            throw new TradingException("OriginalVolume invalid");
        }
        this.originalVolume = originalVolume;

        if (side == null) {
            throw new TradingException("Side invalid: can't be null");
        }
        this.side = side;

        setRemainingVolume(originalVolume);
        setFilledVolume(0);
        setCancelledVolume(0);

        this.id = user + product + price + System.nanoTime();
    }

    // Getters
    public String getUser() {
        return user;
    }

    public String getProduct() {
        return product;
    }

    public Price getPrice() {
        return price;
    }

    public int getOriginalVolume() {
        return originalVolume;
    }

    public int getRemainingVolume() {
        return remainingVolume;
    }

    public int getFilledVolume() {
        return filledVolume;
    }

    public int getCancelledVolume() {
        return cancelledVolume;
    }

    public String getId() {
        return id;
    }

    public GlobalConstants.BookSide getSide() {
        return side;
    }

    // Modifiers
    public void setRemainingVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume) {
            throw new TradingException("RemainingVolume must be between 0 and OriginalVolume");
        }
        this.remainingVolume = newVol;
    }

    public void setFilledVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume || (newVol + cancelledVolume) > originalVolume) {
            throw new TradingException("FilledVolume: invalid (must not exceed remaining volume)");
        }
        this.filledVolume = newVol;
    }

    public void setCancelledVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume || (newVol + filledVolume) > originalVolume) {
            throw new TradingException("CancelledVolume: invalid (must not exceed remaining volume)");
        }
        this.cancelledVolume = newVol;
    }


    @Override
    public String toString() {
        return String.format(
                "%s %s side quote for %s: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id
        );
    }


    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(
                this.id,
                this.user,
                this.product,
                this.price,
                this.originalVolume,
                this.remainingVolume,
                this.cancelledVolume,
                this.filledVolume,
                this.side
        );
    }
}