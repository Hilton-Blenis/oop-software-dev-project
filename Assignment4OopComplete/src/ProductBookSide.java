import java.util.*;

public class ProductBookSide {
    private final GlobalConstants.BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(GlobalConstants.BookSide side) throws TradingException {
        if (side == null) {
            throw new TradingException("BookSide cannot be null.");
        }
        this.side = side;
        Comparator<Price> priceComparator = (side == GlobalConstants.BookSide.BUY) ?
                Comparator.reverseOrder() : Comparator.naturalOrder();
        this.bookEntries = new TreeMap<>(priceComparator);
    }




    public TradableDTO add(Tradable t) throws TradingException {
        if (t == null) {
            throw new TradingException("Tradable cannot be null");
        }
        if (t.getPrice() == null) {
            throw new TradingException("Tradable price cannot be null");
        }
        if (!bookEntries.containsKey(t.getPrice())) {
            bookEntries.put(t.getPrice(), new ArrayList<>());
        }
        bookEntries.get(t.getPrice()).add(t);
        //System.out.println("**ADD: " + t);
        TradableDTO dto = new TradableDTO(t);
        UserManager.getInstance().updateTradable(t.getUser(), dto);
        return dto;
    }


    public TradableDTO cancel(String tradableId) throws TradingException{
        //is anyone reading this?
        if (tradableId == null || tradableId.isEmpty()) return null;

        for (Iterator<Map.Entry<Price, ArrayList<Tradable>>> iterator = bookEntries.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Price, ArrayList<Tradable>> entry = iterator.next();
            ArrayList<Tradable> tradables = entry.getValue();

            for (Iterator<Tradable> tradableIterator = tradables.iterator(); tradableIterator.hasNext(); ) {
                Tradable t = tradableIterator.next();
                if (t.getId().equals(tradableId)) {


                    t.setCancelledVolume(t.getCancelledVolume() + t.getRemainingVolume());
                    t.setRemainingVolume(0);

                    tradableIterator.remove();
                    if (tradables.isEmpty()) {
                        iterator.remove();
                    }

                    TradableDTO dto = new TradableDTO(t);
                    UserManager.getInstance().updateTradable(t.getUser(), dto);
                    return dto;

                }
            }
        }
        return null;
    }

    public TradableDTO removeQuotesForUser(String user) throws TradingException {
        if (user == null) {
            throw new TradingException("ERROR: removeQuotesForUser called with null user.");
        }
        TradableDTO lastDto = null;
        List<Price> emptyPrices = new ArrayList<>();
        for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet()) {
            List<Tradable> tradables = entry.getValue();
            Iterator<Tradable> iterator = tradables.iterator();
            while (iterator.hasNext()) {
                Tradable t = iterator.next();
                if (t instanceof QuoteSide && t.getUser().equals(user)) {
                    //System.out.println("**CANCEL: " + t);
                    iterator.remove();
                    lastDto = new TradableDTO(t);
                    UserManager.getInstance().updateTradable(t.getUser(), lastDto);
                }
            }
            if (tradables.isEmpty()) {
                emptyPrices.add(entry.getKey());
            }
        }
        for (Price price : emptyPrices) {
            bookEntries.remove(price);
        }
        return lastDto;
    }










    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        }
        return bookEntries.firstKey();
    }


    public int topOfBookVolume() {
        if (bookEntries.isEmpty()) return 0;
        ArrayList<Tradable> topTradables = bookEntries.firstEntry().getValue();
        return topTradables.stream().mapToInt(Tradable::getRemainingVolume).sum();
    }

    public void tradeOut(Price price, int volToTrade) throws TradingException{
        if (price == null || volToTrade <= 0) return;

        while (!bookEntries.isEmpty() && volToTrade > 0) {
            Map.Entry<Price, ArrayList<Tradable>> topEntry = bookEntries.firstEntry();
            Price topPrice = topEntry.getKey();

            boolean meetsThreshold;
            if (side == GlobalConstants.BookSide.BUY) {
                meetsThreshold = topPrice.isGreaterThanOrEqual(price);
            } else {
                meetsThreshold = topPrice.isLessThanOrEqual(price);
            }
            if (!meetsThreshold) {
                break;
            }

            ArrayList<Tradable> atPriceList = topEntry.getValue();
            if (atPriceList.isEmpty()) {
                bookEntries.remove(topPrice);
                continue;
            }


            int totalVolAtPrice = atPriceList.stream()
                    .mapToInt(Tradable::getRemainingVolume)
                    .sum();

            if (totalVolAtPrice == 0) {
                bookEntries.remove(topPrice);
                continue;
            }
            if (volToTrade >= totalVolAtPrice) {
                for (Tradable t : atPriceList) {
                    int rem = t.getRemainingVolume();
                    if (rem > 0) {
                        t.setFilledVolume(t.getFilledVolume() + rem);
                        volToTrade -= rem;
                        t.setRemainingVolume(0);
                        System.out.println("FULL FILL: (" + t.getSide() + " " + rem + ") " + t);
                        UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
                    }
                }
                bookEntries.remove(topPrice);

            } else {


                int remainder = volToTrade;
                for (Tradable t : atPriceList) {
                    int rv = t.getRemainingVolume();
                    if (rv <= 0) continue;

                    double ratio = (double) rv / (double) totalVolAtPrice;
                    int nextFill = (int) Math.ceil(ratio * volToTrade);
                    nextFill = Math.min(nextFill, remainder);
                    t.setFilledVolume(t.getFilledVolume() + nextFill);
                    t.setRemainingVolume(rv - nextFill);
                    remainder -= nextFill;
                    boolean nowFull = (t.getRemainingVolume() == 0);

                    if (nowFull) {
                        System.out.println("FULL FILL: (" + t.getSide() + " " + nextFill + ") " + t);
                        UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
                    } else {
                        System.out.println("PARTIAL FILL: (" + t.getSide() + " " + nextFill + ") " + t);
                        UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
                    }
                    if (remainder == 0) {
                        break;
                    }
                }
                atPriceList.removeIf(t -> t.getRemainingVolume() == 0);
                if (atPriceList.isEmpty()) {
                    bookEntries.remove(topPrice);
                }
                break;
            }
        }
    }


    @Override
    public String toString() {
        if (bookEntries.isEmpty()) {
            return String.format("Side: %s%n<Empty>", side);
        }
        String result = String.format("Side: %s%n", side);
        for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet()) {
            for (Tradable t : entry.getValue()) {
                result = result + String.format(
                        "%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s%n",
                        t.getUser(),
                        t.getSide(),
                        t.getProduct(),
                        t.getPrice(),
                        t.getOriginalVolume(),
                        t.getRemainingVolume(),
                        t.getFilledVolume(),
                        t.getCancelledVolume(),
                        t.getId()
                );
            }
        }
        return result;
    }
}
