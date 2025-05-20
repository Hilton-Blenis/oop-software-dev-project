public record TradableDTO(
        String tradableId,
        String user,
        String product,
        Price price,
        int originalVolume,
        int remainingVolume,
        int cancelledVolume,
        int filledVolume,


        GlobalConstants.BookSide side

) { public TradableDTO(Tradable tradable){

    this(
            tradable.getId(),
            tradable.getUser(),
            tradable.getProduct(),
            tradable.getPrice(),
            tradable.getOriginalVolume(),
            tradable.getRemainingVolume(),
            tradable.getCancelledVolume(),
            tradable.getFilledVolume(),

            tradable.getSide()

    );}
    public String tradableId(){
     return tradableId;
    }
}
