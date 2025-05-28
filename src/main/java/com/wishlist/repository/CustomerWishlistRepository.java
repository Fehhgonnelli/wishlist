package com.wishlist.repository;

import com.wishlist.domain.CustomerWishlistEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerWishlistRepository  extends MongoRepository<CustomerWishlistEntity, String> {
    Optional<CustomerWishlistEntity> findByCustomerId(UUID customerId);
}
