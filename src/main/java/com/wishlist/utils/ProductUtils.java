package com.wishlist.utils;

import com.wishlist.domain.ProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public final class ProductUtils {

    private ProductUtils() {
    }

    public static boolean isProductInList(List<ProductEntity> productList, UUID productId) {
        log.debug("Validando Produtos existentes na lista: {}", productId);
        if (productList == null || productId == null) {
            return false;
        }
        return productList.stream()
                .anyMatch(product -> product.getProductId().equals(productId));
    }
}