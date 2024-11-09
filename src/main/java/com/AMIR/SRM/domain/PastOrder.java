package com.AMIR.SRM.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name="past_orders")
public class PastOrder {
    @Id
    private long id;

    private String product_name;
    private String description;
    private double max_price;
    private int count;
    private Date expected_date;
    private double real_price;
    private Date real_date;
    private String status;
    private String author;

    private String provider;

    public PastOrder(){

    }

    public PastOrder(Order order) {
        this.id = order.getId();
        this.product_name = order.getProduct_name();
        this.description = order.getDescription();
        this.max_price = order.getMax_price();
        this.count = order.getCount();
        this.expected_date = order.getExpected_date();
        this.real_price = order.getReal_price();
        this.real_date = order.getReal_date();
        this.status = "canceled";
        this.author = order.getAuthor();
        this.provider = order.getProvider();
    }

    public PastOrder(Order order, String status) {
        this.id = order.getId();
        this.product_name = order.getProduct_name();
        this.description = order.getDescription();
        this.max_price = order.getMax_price();
        this.count = order.getCount();
        this.expected_date = order.getExpected_date();
        this.real_price = order.getReal_price();
        if (order.getReal_date() != null)
            this.real_date = order.getReal_date();
        this.status = status;
        this.author = order.getAuthor();
        if (order.getProvider() != null)
            this.provider = order.getProvider();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMax_price() {
        return max_price;
    }

    public void setMax_price(double max_price) {
        this.max_price = max_price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getExpected_date() {
        return expected_date;
    }

    public void setExpected_date(Date expected_date) {
        this.expected_date = expected_date;
    }

    public double getReal_price() {
        return real_price;
    }

    public void setReal_price(double real_price) {
        this.real_price = real_price;
    }

    public Date getReal_date() {
        return real_date;
    }

    public void setReal_date(Date real_date) {
        this.real_date = real_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
