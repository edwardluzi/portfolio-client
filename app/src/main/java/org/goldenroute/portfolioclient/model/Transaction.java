package org.goldenroute.portfolioclient.model;

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("InstanceVariableNamingConvention")
public class Transaction {

    public enum Type {
        Buying, Selling, ShortSelling, shortCovering
    }

    private Long id;

    private Date timestamp;
    private String ticker;

    private Type type;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal commission;
    private BigDecimal otherCharges;

    public long getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public BigDecimal getTotal() {
        if (this.price == null || this.amount == null) {
            return null;
        }

        BigDecimal total = this.price.multiply(this.amount);

        if (this.commission != null) {
            total = total.add(this.commission);
        }

        if (this.otherCharges != null) {
            total = total.add(this.otherCharges);
        }
        return total;
    }
}
