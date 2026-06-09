package com.myproject.restaurant_service.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "menuitems")
@Data
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private Double itemPrice;

    @Column(name = "item_image_url")
    private String itemImageURL;

    @Column(name = "restaurant_id")
    private Long restaurantId;
}
