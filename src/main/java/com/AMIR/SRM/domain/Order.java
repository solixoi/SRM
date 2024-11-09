package com.AMIR.SRM.domain;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.sql.Date;
import java.util.Calendar;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String product_name;
    private String description;
    private double max_price;
    private int count;
    private Date expected_date;
    private double real_price;
    private Date real_date;
    private String author;
    private boolean is_approved;

    private String provider;
    public Order(){

    }

    public Order(String product_name, String description, double max_price, int count, Date expected_date) {
        this.product_name = product_name;
        this.description = description;
        this.max_price = max_price;
        this.count = count;
        this.expected_date = expected_date;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        this.author = currentPrincipalName;
        this.is_approved = false;
    }

    public Order(PastOrder pastOrder) {
        this.product_name = pastOrder.getProduct_name();
        this.description = pastOrder.getDescription();
        this.max_price = pastOrder.getMax_price();
        this.count = pastOrder.getCount();

        Date currDate = new Date(System.currentTimeMillis());
        if(pastOrder.getExpected_date().before(currDate)) this.expected_date = currDate;
        else this.expected_date = pastOrder.getExpected_date();

        this.author = pastOrder.getAuthor();
        this.is_approved = false;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isIs_approved() {
        return is_approved;
    }

    public void setIs_approved(boolean is_approved) {
        this.is_approved = is_approved;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
