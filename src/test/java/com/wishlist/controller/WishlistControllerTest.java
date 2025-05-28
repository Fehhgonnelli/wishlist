package com.wishlist.controller;

import com.wishlist.dto.request.CustomerWishlistRequestDTO;
import com.wishlist.dto.response.CustomerWishlistResponseDTO;
import com.wishlist.dto.response.ProductCheckResponseDTO;
import com.wishlist.exception.CustomerNotFoundException;
import com.wishlist.exception.NoItemsAddedException;
import com.wishlist.exception.NoItemsDeletedException;
import com.wishlist.service.CustomerWishListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerWishListService customerWishListService;

    private UUID customerId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void quandoBuscarWishlist_deveRetornarSucesso() throws Exception {
        CustomerWishlistResponseDTO customerWishlistResponse = criarRespostaWishlistVazia(customerId);
        when(customerWishListService.getWishlist(customerId)).thenReturn(customerWishlistResponse);

        mockMvc.perform(get("/api/wishlist/{customerId}", customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(criarJsonWishlistVazia(customerId)));
    }

    @Test
    void quandoBuscarWishlistClienteNaoEncontrado_deveRetornarNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Cliente não encontrado"))
                .when(customerWishListService).getWishlist(customerId);

        mockMvc.perform(get("/api/wishlist/{customerId}", customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoVerificarProdutoNaWishlist_deveRetornarSucesso() throws Exception {
        ProductCheckResponseDTO productCheckResponse = criarRespostaVerificacaoProduto(customerId, productId, true);
        when(customerWishListService.checkProductInWishlist(customerId, productId)).thenReturn(productCheckResponse);

        mockMvc.perform(get("/api/wishlist/product/check/{productId}/{customerId}", productId, customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(criarJsonVerificacaoProduto(customerId, productId)));
    }

    @Test
    void quandoVerificarProdutoClienteNaoEncontrado_deveRetornarNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Cliente não encontrado"))
                .when(customerWishListService).checkProductInWishlist(customerId, productId);

        mockMvc.perform(get("/api/wishlist/product/check/{productId}/{customerId}", productId, customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoAdicionarProdutosNaWishlist_deveRetornarSucesso() throws Exception {
        CustomerWishlistResponseDTO response = criarRespostaWishlistVazia(customerId);
        when(customerWishListService.addItemsWishlist(any(CustomerWishlistRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoWishlistVazia(customerId)))
                .andExpect(status().isOk())
                .andExpect(content().json(criarJsonWishlistVazia(customerId)));
    }

    @Test
    void quandoAdicionarProdutosNenhumItemAdicionado_deveRetornarConflict() throws Exception {
        doThrow(new NoItemsAddedException("Nenhum item foi adicionado à wishlist"))
                .when(customerWishListService).addItemsWishlist(any(CustomerWishlistRequestDTO.class));

        mockMvc.perform(put("/api/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoWishlistVazia(customerId)))
                .andExpect(status().isConflict());
    }

    @Test
    void quandoRemoverProdutosComRetornoCorpo_deveRetornarSucesso() throws Exception {
        CustomerWishlistResponseDTO response = criarRespostaWishlistVazia(customerId);
        when(customerWishListService.deleteItemsFromWishlistWithResponse(eq(customerId), anyList()))
                .thenReturn(response);

        mockMvc.perform(delete("/api/wishlist/product?returnBody=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoRemocaoProdutos(customerId, productId)))
                .andExpect(status().isOk())
                .andExpect(content().json(criarJsonWishlistVazia(customerId)));
    }

    @Test
    void quandoRemoverProdutosSemRetornoCorpo_deveRetornarNoContent() throws Exception {
        doNothing().when(customerWishListService).deleteItemsFromWishlist(eq(customerId), anyList());

        mockMvc.perform(delete("/api/wishlist/product?returnBody=false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoRemocaoProdutos(customerId, productId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void quandoRemoverProdutosClienteNaoEncontrado_deveRetornarNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Cliente não encontrado"))
                .when(customerWishListService).deleteItemsFromWishlist(eq(customerId), anyList());

        mockMvc.perform(delete("/api/wishlist/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoRemocaoProdutos(customerId, productId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoRemoverProdutosNenhumItemRemovido_deveRetornarBadRequest() throws Exception {
        doThrow(new NoItemsDeletedException("Nenhum item foi removido"))
                .when(customerWishListService).deleteItemsFromWishlist(eq(customerId), anyList());

        mockMvc.perform(delete("/api/wishlist/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarJsonRequisicaoRemocaoProdutos(customerId, productId)))
                .andExpect(status().isBadRequest());
    }

    private CustomerWishlistResponseDTO criarRespostaWishlistVazia(UUID customerId) {
        return CustomerWishlistResponseDTO.builder()
                .customerId(customerId)
                .wishlist(Collections.emptyList())
                .totalPrice(0L)
                .formattedTotalPrice("$0.00")
                .build();
    }

    private ProductCheckResponseDTO criarRespostaVerificacaoProduto(UUID customerId, UUID productId, boolean inWishlist) {
        ProductCheckResponseDTO.ProductDetailsDTO productDetails = ProductCheckResponseDTO.ProductDetailsDTO.builder()
                .name("Test Product")
                .description("Product Description")
                .price(100L)
                .build();

        return ProductCheckResponseDTO.builder()
                .customerId(customerId)
                .productId(productId)
                .inWishlist(inWishlist)
                .product(productDetails)
                .build();
    }

    private String criarJsonWishlistVazia(UUID customerId) {
        return String.format("""
                {
                    "customerId": "%s",
                    "wishlist": [],
                    "totalPrice": 0,
                    "formattedTotalPrice": "$0.00"
                }
                """, customerId);
    }

    private String criarJsonRequisicaoWishlistVazia(UUID customerId) {
        return String.format("""
                {
                    "customerId": "%s",
                    "wishlist": []
                }
                """, customerId);
    }

    private String criarJsonVerificacaoProduto(UUID customerId, UUID productId) {
        return String.format("""
                {
                    "customerId": "%s",
                    "productId": "%s",
                    "inWishlist": true,
                    "product": {
                        "name": "Test Product",
                        "description": "Product Description",
                        "price": 100
                    }
                }
                """, customerId, productId);
    }

    private String criarJsonRequisicaoRemocaoProdutos(UUID customerId, UUID productId) {
        return String.format("""
                {
                    "customerId": "%s",
                    "productIds": ["%s"]
                }
                """, customerId, productId);
    }
}