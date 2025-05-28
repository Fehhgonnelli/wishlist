package com.wishlist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    private UUID productId;
    private String name;
    private String description;
    private Long price;
}
