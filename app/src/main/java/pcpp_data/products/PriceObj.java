package pcpp_data.products;

public class PriceObj {
    private double basePrice;
    private double shipping;
    private boolean avail;
    private String merchant;
    private String purchaseLink;

    public PriceObj() {

    }

    public double getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    public double getShipping() {
        return shipping;
    }
    public void setShipping(double shipping) {
        this.shipping = shipping;
    }
    public boolean isAvail() {
        return avail;
    }
    public void setAvail(boolean avail) {
        this.avail = avail;
    }
    public String getMerchant() {
        return merchant;
    }
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
    public String getPurchaseLink() {
        return purchaseLink;
    }
    public void setPurchaseLink(String purchaseLink) {
        this.purchaseLink = purchaseLink;
    }

}
