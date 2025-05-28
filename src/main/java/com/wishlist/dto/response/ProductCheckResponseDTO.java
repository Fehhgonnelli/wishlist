package com.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resposta da verificação se um produto está na wishlist do cliente")
public class ProductCheckResponseDTO {

    @Schema(description = "UUID do cliente")
    private UUID customerId;
    
    @Schema(description = "UUID do produto verificado")
    private UUID productId;
    
    @Schema(description = "Indica se o produto está na wishlist do cliente", example = "true")
    private boolean inWishlist;
    
    @Schema(description = "Informações do produto (disponível apenas se estiver na wishlist)")
    private ProductDetailsDTO product;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetailsDTO {
        @Schema(description = "Nome do produto")
        private String name;
        
        @Schema(description = "Descrição do produto")
        private String description;
        
        @Schema(description = "Preço do produto em centavos")
        private Long price;
    }
}