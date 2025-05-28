package com.wishlist.service;

import com.wishlist.domain.CustomerWishlistEntity;
import com.wishlist.domain.ProductEntity;
import com.wishlist.dto.CustomerWishlistDTO;
import com.wishlist.dto.ProductDTO;
import com.wishlist.dto.request.CustomerWishlistRequestDTO;
import com.wishlist.dto.response.CustomerWishlistResponseDTO;
import com.wishlist.dto.response.ProductCheckResponseDTO;
import com.wishlist.exception.CustomerNotFoundException;
import com.wishlist.exception.NoItemsAddedException;
import com.wishlist.exception.NoItemsDeletedException;
import com.wishlist.exception.WishlistLimitExceededException;
import com.wishlist.repository.CustomerWishlistRepository;
import com.wishlist.utils.ProductUtils;
import com.wishlist.utils.mapper.CustomerWishlistMapper;
import com.wishlist.utils.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerWishListService {

    private final CustomerWishlistRepository repository;
    private final CustomerWishlistMapper mapperWishlist;
    private final ProductMapper productMapper;

    public CustomerWishlistResponseDTO getWishlist(UUID customerId) throws CustomerNotFoundException {
        CustomerWishlistEntity customer = findCustomerById(customerId);

        log.debug("formatando resposta: {}", customer);
        return mapperWishlist.toResponseDTO(customer);
    }

    @Transactional
    public CustomerWishlistResponseDTO addItemsWishlist(CustomerWishlistRequestDTO wishlistDTO) throws NoItemsAddedException {
        try {
            CustomerWishlistEntity customer = findCustomerById(wishlistDTO.getCustomerId());
            return addItemsToWishlist(customer, wishlistDTO.getWishlist());
        } catch (CustomerNotFoundException e) {
            return createNewWishlist(mapperWishlist.requestToDTO(wishlistDTO));
        }
    }

    private CustomerWishlistResponseDTO createNewWishlist(CustomerWishlistDTO wishlistDTO) {
        log.info("Criando nova wishlist para o cliente: {}", wishlistDTO.getCustomerId());
        CustomerWishlistEntity newCustomer = mapperWishlist.toEntity(wishlistDTO);
        newCustomer.setDateCreation(LocalDateTime.now());
        return mapperWishlist.toResponseDTO(repository.save(newCustomer));
    }

    @Transactional
    public void deleteItemsFromWishlist(UUID customerId, List<UUID> productsToDelete) throws CustomerNotFoundException, NoItemsDeletedException {
        log.info("Removendo produtos da wishlist do cliente: {}", customerId);
        CustomerWishlistEntity customer = findCustomerById(customerId);
        removeProductsFromWishlist(customer, productsToDelete);
        repository.save(customer);
    }

    @Transactional
    public CustomerWishlistResponseDTO deleteItemsFromWishlistWithResponse(UUID customerId, List<UUID> productsToDelete) throws CustomerNotFoundException, NoItemsDeletedException {
        CustomerWishlistEntity customer = findCustomerById(customerId);
        removeProductsFromWishlist(customer, productsToDelete);
        return mapperWishlist.toResponseDTO(repository.save(customer));
    }

    private CustomerWishlistResponseDTO addItemsToWishlist(CustomerWishlistEntity customer, List<ProductDTO> wishlistDTO) throws NoItemsAddedException {
        log.info("Adicionando novos produtos na Wishlist: {}", customer.getCustomerId());
        if (customer.getWishlist() == null) {
            customer.setWishlist(new ArrayList<>());
        }
        List<ProductEntity> currentWishlist = customer.getWishlist();
        List<ProductEntity> newItems = wishlistDTO.stream()
                .filter(productDTO -> !ProductUtils.isProductInList(currentWishlist, productDTO.getProductId()))
                .map(productMapper::toEntity)
                .toList();
        log.debug("Qtq itens na lista atual [{}], quantidade para ser adicionados [{}]", currentWishlist.size(), newItems.size());

        if (newItems.isEmpty()) {
            throw new NoItemsAddedException("Nenhum produto foi adicionado pois os itens selecionados já constam na sua Wishlist");
        }
        if (currentWishlist.size() + newItems.size() > 20) {
            throw new WishlistLimitExceededException(
                    "Não é possível adicionar mais itens. O limite máximo é de 20 itens na wishlist."
            );
        }

        currentWishlist.addAll(newItems);
        customer.setDateUpdate(LocalDateTime.now());
        return mapperWishlist.toResponseDTO(repository.save(customer));
    }

    private CustomerWishlistEntity findCustomerById(UUID customerId) throws CustomerNotFoundException {
        log.debug("Buscando customer: {}", customerId);
        return repository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Usuário não encontrado"));
    }

    private void removeProductsFromWishlist(CustomerWishlistEntity customer, List<UUID> productsToDelete) throws NoItemsDeletedException {
        log.debug("Removendo produtos da wishlist do cliente: {}", customer.getCustomerId());
        if (!isValidDeletionRequest(customer, productsToDelete)) {
            throw new NoItemsDeletedException("Nenhum produto válido foi fornecido para remoção");
        }

        int originalSize = customer.getWishlist().size();
        Set<UUID> productsToRemove = new HashSet<>(productsToDelete);
        customer.getWishlist().removeIf(product -> productsToRemove.contains(product.getProductId()));

        log.debug("Produtos removidos da wishlist do cliente: {}", customer.getCustomerId());

        if (customer.getWishlist().size() == originalSize) {
            throw new NoItemsDeletedException("Nenhum dos produtos fornecidos foi encontrado na wishlist");
        }

        customer.setDateUpdate(LocalDateTime.now());
    }

    private boolean isValidDeletionRequest(CustomerWishlistEntity customer, List<UUID> productsToDelete) {
        return customer.getWishlist() != null &&
                productsToDelete != null &&
                !productsToDelete.isEmpty();
    }

    public ProductCheckResponseDTO checkProductInWishlist(UUID customerId, UUID productId) throws CustomerNotFoundException {
        CustomerWishlistEntity customer = findCustomerById(customerId);

        ProductCheckResponseDTO.ProductDetailsDTO productDetails = null;
        boolean inWishlist = false;

        if (customer.getWishlist() != null) {
            Optional<ProductEntity> productOpt = customer.getWishlist().stream()
                    .filter(product -> product.getProductId().equals(productId))
                    .findFirst();

            if (productOpt.isPresent()) {
                ProductEntity product = productOpt.get();
                inWishlist = true;

                productDetails = ProductCheckResponseDTO.ProductDetailsDTO.builder()
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .build();
            }
        }

        return ProductCheckResponseDTO.builder()
                .customerId(customerId)
                .productId(productId)
                .inWishlist(inWishlist)
                .product(productDetails)
                .build();
    }






}

