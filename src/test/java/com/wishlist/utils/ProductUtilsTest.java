package com.wishlist.utils;

import com.wishlist.domain.ProductEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductUtilsTest {

    @Test
    void quandoListaEIdValidos_ProdutoExisteNaLista_DeveRetornarTrue() {
        
        UUID productId = UUID.randomUUID();
        List<ProductEntity> productList = new ArrayList<>();

        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        productList.add(product);

        boolean result = ProductUtils.isProductInList(productList, productId);

        
        assertTrue(result);
    }

    @Test
    void quandoListaEIdValidos_ProdutoNaoExisteNaLista_DeveRetornarFalse() {
        
        UUID existingProductId = UUID.randomUUID();
        UUID nonExistingProductId = UUID.randomUUID();

        List<ProductEntity> productList = new ArrayList<>();

        ProductEntity product = new ProductEntity();
        product.setProductId(existingProductId);
        productList.add(product);
        
        boolean result = ProductUtils.isProductInList(productList, nonExistingProductId);

        assertFalse(result);
    }

    @Test
    void quandoListaVazia_DeveRetornarFalse() {
        
        UUID productId = UUID.randomUUID();
        List<ProductEntity> productList = new ArrayList<>();
        
        boolean result = ProductUtils.isProductInList(productList, productId);

        
        assertFalse(result);
    }

    @Test
    void quandoListaNula_DeveRetornarFalse() {
        UUID productId = UUID.randomUUID();
        
        boolean result = ProductUtils.isProductInList(null, productId);
        
        assertFalse(result);
    }

    @Test
    void quandoIdNulo_DeveRetornarFalse() {
        
        List<ProductEntity> productList = new ArrayList<>();
        productList.add(new ProductEntity());

        
        boolean result = ProductUtils.isProductInList(productList, null);
        
        assertFalse(result);
    }

    @Test
    void quandoListaEIdNulos_DeveRetornarFalse() {
        
        boolean result = ProductUtils.isProductInList(null, null);
        
        assertFalse(result);
    }


}
