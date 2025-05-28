package com.wishlist.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customer_wishlist")
public class CustomerWishlistEntity {

    @Id
    private UUID customerId;

    private List<ProductEntity> wishlist;

    private LocalDateTime dateCreation;

    private LocalDateTime dateUpdate;

}
