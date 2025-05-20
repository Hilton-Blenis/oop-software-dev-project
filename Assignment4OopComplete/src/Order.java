public class Order implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final GlobalConstants.BookSide side;

    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private final String id;

    public Order(String user, String product, Price price, int originalVolume, GlobalConstants.BookSide side)
            throws TradingException {
        // Validate immutable fields directly
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new TradingException("User code must be exactly 3 uppercase letters: " + user);
        }
        this.user = user;

        if (!product.matches("[A-Z0-9.]{1,5}")) {
            throw new TradingException("Product code: invalid");
        }
        this.product = product;

        if (price == null) {
            throw new TradingException("Price: invalid");
        }
        this.price = price;

        if (side == null) {
            throw new TradingException("BookSide: invalid");
        }
        this.side = side;

        if (originalVolume <= 0 || originalVolume >= 10000) {
            throw new TradingException("OriginalVolume: not in range");
        }
        this.originalVolume = originalVolume;

        // Use setters for mutable fields to enforce validation
        setRemainingVolume(originalVolume);
        setCancelledVolume(0);
        setFilledVolume(0);

        this.id = user + product + price + System.nanoTime();
    }

    public String getUser() {
        return user;
    }

    public String getProduct() {
        return product;
    }

    public Price getPrice() {
        return price;
    }

    public GlobalConstants.BookSide getSide() {
        return side;
    }


    public int getOriginalVolume() {
        return originalVolume;
    }

    public int getRemainingVolume() {
        return remainingVolume;
    }

    public int getCancelledVolume() {
        return cancelledVolume;
    }

    public int getFilledVolume() {
        return filledVolume;
    }

    public String getId() {
        return id;
    }

    public void setRemainingVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume) {
            throw new TradingException("RemainingVolume must be between 0 and OriginalVolume");
        }
        this.remainingVolume = newVol;
    }

    public void setCancelledVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume || (newVol + filledVolume) > originalVolume) {
            throw new TradingException("CancelledVolume: invalid (must not exceed remaining volume)");
        }
        this.cancelledVolume = newVol;
    }

    public void setFilledVolume(int newVol) throws TradingException {
        if (newVol < 0 || newVol > originalVolume || (newVol + cancelledVolume) > originalVolume) {
            throw new TradingException("FilledVolume: invalid (must not exceed remaining volume)");
        }
        this.filledVolume = newVol;
    }


    @Override
    public String toString() {
        return String.format("%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s"
                , user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id);

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