package com.wishlist.dto.request;

import com.wishlist.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWishlistRequestDTO {

    @NotNull(message = "O ID do cliente não pode ser nulo")
    @Schema(description = "UUID do cliente")
    private UUID customerId;

    @Valid
    @Schema(description = "Lista de produtos na Wishlist")
    private List<ProductDTO> wishlist;
}