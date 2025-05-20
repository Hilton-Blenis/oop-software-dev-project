public interface Tradable {
    String getId();
    int getRemainingVolume();
    void setCancelledVolume(int newVol) throws TradingException;
    int getCancelledVolume();
    void setRemainingVolume(int newVol) throws TradingException;
    TradableDTO makeTradableDTO();
    Price getPrice();
    void setFilledVolume(int newVol) throws TradingException;
    int getFilledVolume();
    GlobalConstants.BookSide getSide();
    String getUser();
    String getProduct();
    int getOriginalVolume();
}