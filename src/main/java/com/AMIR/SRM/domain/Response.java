package com.AMIR.SRM.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "responses")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_supp", referencedColumnName = "id")
    private User supplier;

    @ManyToOne
    @JoinColumn(name = "id_order", referencedColumnName = "id")
    private Order order;

    public Response(){}
    public Response(User user, Order order){
        this.supplier = user;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSupplier() {
        return supplier;
    }

    public void setSupplier(User supplier) {
        this.supplier = supplier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
