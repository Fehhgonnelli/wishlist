package com.wishlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductDTO {

    @NotNull(message ="O ID do produto não pode ser nulo")
    private UUID productId;

    @NotBlank(message = "O nome do produto não pode estar em branco")
    private String name;

    private String description;

    @NotNull(message = "O preço não pode ser nulo")
    @Positive(message = "O preço deve ser maior que zero")
    private Double price;
}
