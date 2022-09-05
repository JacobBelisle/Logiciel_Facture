package com.gui_java;

public class Article implements YmlWritable {
    private String description;
    private int quantite;
    private Boolean taxes;
    private Float price;
    private Float total;

    public Article(String description, int quantite, Float price, Boolean taxes) {
        this.description = description;
        this.quantite = quantite;
        this.price = price;
        this.taxes = taxes;

        if (this.taxes) {
            calculTaxes();
        } else {
            this.total = quantite * price;
        }
    }

    public Article() {
        this.description = "";
        this.quantite = 0;
        this.price = 0f;
        this.taxes = false;
        this.total = 0f;
    }

    private void calculTaxes() {
        Float p = this.price * quantite;
        Float TPS = rounded(p * 5 / 100);
        Float TVQ = rounded((float) (p * 9.975 / 100));
        this.total = rounded(p + TPS + TVQ);
    }

    private Float rounded(Float f) {
        Float n = (float) (Math.round(f * 100.0) / 100.0);
        return n;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getTaxes() {
        return taxes;
    }

    public void setTaxes(Boolean taxes) {
        this.taxes = taxes;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getTotal() {
        return this.total;
    }

    @Override
    public String toString() {
        return "Article{" +
                "description='" + description + '\'' +
                ", price=" + price +
                ", taxes=" + taxes +
                ", total=" + total +
                '}';
    }


    @Override
    public String toFile() {
        return "\tdescription: " + description + '\n' +
                "\tquantite: " + quantite + '\n' +
                "\ttaxes: " + taxes.toString() + '\n' +
                "\tprice: " + price.toString() + '\n' +
                "\ttotal: " + total.toString();
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void updateTotal() {
        if (this.taxes)
            calculTaxes();
        else
            this.total = this.quantite * this.price;
    }
}
