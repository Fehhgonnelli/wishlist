package com.wishlist.utils.mapper;

import com.wishlist.domain.ProductEntity;
import com.wishlist.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductDTO dto);
    ProductDTO toDTO(ProductEntity entity);
}
