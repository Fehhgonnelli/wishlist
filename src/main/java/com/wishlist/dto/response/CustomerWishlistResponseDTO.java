package com.wishlist.dto.response;

import com.wishlist.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWishlistResponseDTO {

    @Schema(description = "UUID do cliente")
    private UUID customerId;

    @Schema(description = "Lista de produtos da Wishlist")
    private List<ProductDTO> wishlist;

    @Schema(description = "Total de produtos na Wishlist")
    private Long totalPrice;

    @Schema(description = "Valor total de produtos na Wishlist formatado")
    private String formattedTotalPrice;

}
