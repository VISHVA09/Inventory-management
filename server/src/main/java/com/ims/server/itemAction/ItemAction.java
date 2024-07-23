package com.ims.server.itemAction;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.ims.server.item.Item;

@Entity
@Data
public class ItemAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdDate;

    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public ItemAction() {
        this.price = BigDecimal.ZERO;
        this.quantity = 0L;
    }

     public ItemAction(Long id,BigDecimal price, Long quantity, Item item, LocalDateTime lastModifiedDate) {
        this.price = price;
        this.quantity = quantity;
        this.id = id;
        this.item = item;
        this.lastModifiedDate = lastModifiedDate;
    }

     public ItemAction(Long id,BigDecimal price, Long quantity, Item item) {
        this.price = price;
        this.quantity = quantity;
        this.id = id;
        this.item = item;
    }

    // Getter and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
