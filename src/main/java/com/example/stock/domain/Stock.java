package com.example.stock.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    // Optimistic Lock
    @Version
    private Long version;

    public Stock(){
    }

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("foo");
        }
        this.quantity = this.quantity - quantity;
    }
}
