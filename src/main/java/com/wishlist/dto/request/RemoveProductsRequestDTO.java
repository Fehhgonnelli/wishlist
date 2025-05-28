package com.wishlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RemoveProductsRequestDTO {

    @NotNull(message = "O ID do cliente não pode ser nulo")
    @Schema(description = "UUID do cliente")
    private UUID customerId;

    @NotEmpty(message = "A lista de produtos para remoção não pode estar vazia")
    @Valid
    @NotNull
    @Schema(description = "Lista de UUIDs dos produtos a serem removidos")
    private List<UUID> productIds;

}