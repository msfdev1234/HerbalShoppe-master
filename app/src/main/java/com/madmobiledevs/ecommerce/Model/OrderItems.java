package com.madmobiledevs.ecommerce.Model;

public class OrderItems {

    public String  pid, name, quantity, rate, units, images, price, unit_quantity;

    public OrderItems(){
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit_quantity() {
        return unit_quantity;
    }

    public void setUnit_quantity(String unit_quantity) {
        this.unit_quantity = unit_quantity;
    }

    public OrderItems(String pid, String name, String quantity, String rate, String units, String images, String price, String unit_quantity) {
        this.pid = pid;
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.units = units;
        this.images = images;
        this.price = price;
        this.unit_quantity = unit_quantity;
    }
}
