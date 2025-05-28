package com.wishlist.dto;

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
public class CustomerWishlistDTO {

    @NotNull(message = "O ID do cliente não pode ser nulo")
    private UUID customerId;

    @Valid
    private List<ProductDTO> wishlist;

    private LocalDateTime dateCreation;

    private LocalDateTime dateUpdate;

}
