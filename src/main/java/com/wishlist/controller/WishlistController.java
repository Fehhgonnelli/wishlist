package com.wishlist.controller;

import com.wishlist.dto.request.CustomerWishlistRequestDTO;
import com.wishlist.dto.request.RemoveProductsRequestDTO;
import com.wishlist.dto.response.CustomerWishlistResponseDTO;
import com.wishlist.dto.response.ProductCheckResponseDTO;
import com.wishlist.exception.CustomerNotFoundException;
import com.wishlist.exception.NoItemsAddedException;
import com.wishlist.exception.NoItemsDeletedException;
import com.wishlist.service.CustomerWishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@Tag(name = "Wishlist", description = "API de gerenciamento de Wishlist")
@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Validated
public class WishlistController {
    private final CustomerWishListService customerWishListService;

    @Operation(
            summary = "Buscar a Wishlist do cliente",
            description = "Recupera todos os produtos na Wishlist de um cliente específico"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wishlist encontrada com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerWishlistResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping(value = "/{customerId}")
    public CustomerWishlistResponseDTO getWishlist(
            @Parameter(description = "ID único do cliente", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull(message = "O ID do cliente não pode ser nulo") UUID customerId
    ) throws CustomerNotFoundException {
        log.info("Buscando wishlist para o cliente: {}", customerId);
        try {
            CustomerWishlistResponseDTO response = customerWishListService.getWishlist(customerId);
            log.debug("Wishlist encontrada com sucesso para o cliente: {}", response);
            return response;
        } catch (CustomerNotFoundException e) {
            log.error("Erro ao buscar wishlist para o cliente: {}", customerId, e);
            throw e;
        }
    }

    @Operation(
            summary = "Verificar se um produto está na wishlist",
            description = "Verifica se um produto específico está na wishlist de um cliente e retorna detalhes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificação realizada com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductCheckResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping(value = "/product/check/{productId}/{customerId}")
    public ResponseEntity<ProductCheckResponseDTO> checkIfProductIsInWishlist(
            @Parameter(description = "ID do produto a ser verificado", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull(message = "O ID do produto não pode ser nulo") UUID productId,
            @Parameter(description = "ID do cliente", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotNull(message = "O ID do cliente não pode ser nulo") UUID customerId
    ) throws CustomerNotFoundException {
        log.info("Verificando se o produto {} está na wishlist do cliente {}", productId, customerId);
        try {
            ProductCheckResponseDTO response = customerWishListService.checkProductInWishlist(customerId, productId);
            return ResponseEntity.ok(response);
        } catch (CustomerNotFoundException e) {
            log.error("Erro ao verificar produto na wishlist: cliente {} não encontrado", customerId, e);
            throw e;
        }
    }



    @Operation(
            summary = "Adicionar produtos à Wishlist",
            description = "Adiciona um ou mais produtos à Wishlist do cliente. Cada cliente pode ter no máximo 20 produtos em sua lista. " +
                    "Se a solicitação ultrapassar esse limite, nenhum produto será adicionado e uma exceção será lançada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos adicionados com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerWishlistResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Erro na requisição ou limite de produtos excedido",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CustomerWishlistResponseDTO addProductsToWishlist(
            @Parameter(description = "Dados dos produtos a serem adicionados")
            @RequestBody
            @Valid
            @NotNull(message = "O corpo da requisição não pode ser nulo")
            CustomerWishlistRequestDTO customerWishlist
    ) throws NoItemsAddedException {
        log.info("Adicionando produtos à wishlist do cliente: {}", customerWishlist.getCustomerId());
        try {
            CustomerWishlistResponseDTO response = customerWishListService.addItemsWishlist(customerWishlist);
            log.debug("Produtos adicionados com sucesso à wishlist do cliente: {}", response);
            return response;
        } catch (NoItemsAddedException e) {
            log.error("Erro ao adicionar produtos à wishlist do cliente: {}", customerWishlist.getCustomerId(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Remover produtos da Wishlist",
            description = "Remove um ou mais produtos da Wishlist do cliente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos removidos com sucesso (quando returnBody=true)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerWishlistResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Produtos removidos com sucesso (quando returnBody=false)",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado ou produtos não encontrados",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @DeleteMapping("/product")
    public ResponseEntity<?> removeProductsFromWishlist(
            @Parameter(description = "Dados dos produtos a serem removidos")
            @RequestBody @Valid RemoveProductsRequestDTO request,
            @Parameter(
                    description = "Define se a resposta deve incluir a lista atualizada"
            )
            @RequestParam(defaultValue = "false") boolean returnBody
    ) throws CustomerNotFoundException, NoItemsDeletedException {
        log.info("Removendo produtos da wishlist do cliente: {}", request.getCustomerId());
        try {
            if (returnBody) {
                CustomerWishlistResponseDTO response = customerWishListService.deleteItemsFromWishlistWithResponse(
                        request.getCustomerId(),
                        request.getProductIds()
                );
                log.debug("Produtos removidos com sucesso e resposta gerada para o cliente: {}", response);
                return ResponseEntity.ok(response);
            }

            customerWishListService.deleteItemsFromWishlist(
                    request.getCustomerId(),
                    request.getProductIds()
            );
            log.debug("Produtos removidos com sucesso da wishlist do cliente: {}", request.getCustomerId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao remover produtos da wishlist do cliente: {}", request.getCustomerId(), e);
            throw e;
        }
    }

}
