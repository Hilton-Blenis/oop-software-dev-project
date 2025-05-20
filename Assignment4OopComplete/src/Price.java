public final class Price implements Comparable<Price> {
    private final double value;

    public Price(double amount) throws TradingException {
        if (amount < 0) {
            throw new TradingException("Price cannot be negative.");
        }
        this.value = amount;
    }

    public double getValue() {
        return value;
    }



    @Override
    public int compareTo(Price other) {
        return Double.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return "$" + String.format("%.2f", value);
    }

    public boolean isGreaterThanOrEqual(Price price) {
        return this.value >= price.value;

    }

    public boolean isLessThanOrEqual(Price price) {
        return this.value <= price.value;
    }
}
