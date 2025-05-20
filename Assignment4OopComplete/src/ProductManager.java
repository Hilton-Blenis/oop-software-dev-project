import java.util.HashMap;
import java.util.Random;
// Music to listen to while grading this assignment: Code Monkey by Johnathan Coulton

public class ProductManager {
    private static final ProductManager instance = new ProductManager();
    private final HashMap<String, ProductBook> productBooks;

    private ProductManager() {
        productBooks = new HashMap<>();
    }

    public static ProductManager getInstance() {
        return instance;
    }

    public void addProduct(String symbol) throws DataValidationException, TradingException {
        if (symbol == null || !symbol.matches("^[A-Z0-9.]{1,5}$")) {
            throw new DataValidationException("Invalid product symbol: " + symbol);
        }
        productBooks.put(symbol, new ProductBook(symbol));
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        ProductBook book = productBooks.get(symbol);
        if (book == null) {
            throw new DataValidationException("Product does not exist: " + symbol);
        }
        return book;
    }

    public String getRandomProduct() throws DataValidationException {
        if (productBooks.isEmpty()) {
            throw new DataValidationException("No products exist");
        }
        return productBooks.keySet().toArray(new String[0])[new Random().nextInt(productBooks.size())];
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, TradingException {
        if (o == null) {
            throw new DataValidationException("Tradable cannot be null");
        }
        ProductBook book = getProductBook(o.getProduct());
        TradableDTO dto = book.add(o);
        UserManager.getInstance().updateTradable(o.getUser(), dto);
        return dto;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, TradingException {
        if (q == null) {
            throw new DataValidationException("Quote cannot be null");
        }
        ProductBook book = getProductBook(q.getSymbol());
        book.removeQuotesForUser(q.getUser());
        TradableDTO buyDto = addTradable(q.getQuoteSide(GlobalConstants.BookSide.BUY));
        TradableDTO sellDto = addTradable(q.getQuoteSide(GlobalConstants.BookSide.SELL));
        return new TradableDTO[]{buyDto, sellDto};
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException, TradingException {
        if (o == null) {
            throw new DataValidationException("TradableDTO cannot be null");
        }
        ProductBook book = getProductBook(o.product());
        TradableDTO result = book.cancel(o.side(), o.tradableId());
        if (result == null) {
            System.out.println("Failed to cancel tradable: " + o.tradableId());
        }
        return result;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, TradingException {
        if (symbol == null || user == null) {
            throw new DataValidationException("Symbol or user cannot be null");
        }
        ProductBook book = getProductBook(symbol);
        return book.removeQuotesForUser(user);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ProductBook book : productBooks.values()) {
            sb.append(book.toString());
        }
        return sb.toString();
    }
}