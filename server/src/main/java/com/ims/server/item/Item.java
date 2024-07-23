package com.ims.server.item;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private BigDecimal cost;
    private BigDecimal price;
    private Long quantity;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Item() {
		this.code = "IMS-" + this.id;
		this.name = "IMS-Item";
		this.cost = BigDecimal.ZERO;
		this.price = BigDecimal.ZERO;
		this.quantity = 0L;
	}

   

    // Constructor with all fields
    public Item(Long id, String code, String name, BigDecimal cost, BigDecimal price, Long quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.quantity = quantity;
    }

     public Item(Long id, String code,  Long quantity) {
        this.id = id;
        this.code = code;
        this.quantity = quantity;
    }

}
