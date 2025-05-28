package com.wishlist.utils.mapper;

import com.wishlist.domain.CustomerWishlistEntity;
import com.wishlist.dto.CustomerWishlistDTO;
import com.wishlist.dto.request.CustomerWishlistRequestDTO;
import com.wishlist.dto.response.CustomerWishlistResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.NumberFormat;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface CustomerWishlistMapper {

    CustomerWishlistEntity toEntity(CustomerWishlistDTO dto);

    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateUpdate", ignore = true)
    CustomerWishlistDTO requestToDTO(CustomerWishlistRequestDTO request);

    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(entity))")
    @Mapping(target = "formattedTotalPrice", expression = "java(formatPrice(calculateTotalPrice(entity)))")
    CustomerWishlistResponseDTO toResponseDTO(CustomerWishlistEntity entity);

    default Long calculateTotalPrice(CustomerWishlistEntity entity) {
        if (entity == null || entity.getWishlist() == null) {
            return 0L;
        }

        return entity.getWishlist().stream()
                .mapToLong(product -> product.getPrice() != null ? product.getPrice() : 0L)
                .sum();
    }

    default String formatPrice(Long priceInCents) {
        if (priceInCents == null) {
            return "R$0,00";
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(priceInCents / 100.0);
    }



}
