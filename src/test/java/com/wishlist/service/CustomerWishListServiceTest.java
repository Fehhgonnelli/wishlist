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
import com.wishlist.utils.mapper.CustomerWishlistMapper;
import com.wishlist.utils.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerWishListServiceTest {

    @Mock
    private CustomerWishlistRepository repository;

    @Mock
    private CustomerWishlistMapper mapperWishlist;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CustomerWishListService service;

    @Test
    void quandoClienteExiste_getWishlistDeveRetornarDadosCorretos() throws Exception {
        UUID customerId = UUID.randomUUID();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>());

        CustomerWishlistResponseDTO expectedResponse = CustomerWishlistResponseDTO.builder()
                .customerId(customerId)
                .wishlist(List.of())
                .build();

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));
        when(mapperWishlist.toResponseDTO(customerEntity)).thenReturn(expectedResponse);

        CustomerWishlistResponseDTO response = service.getWishlist(customerId);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(repository).findByCustomerId(customerId);
        verify(mapperWishlist).toResponseDTO(customerEntity);
    }

    @Test
    void quandoClienteNaoExiste_getWishlistDeveLancarCustomerNotFoundException() {
        UUID customerId = UUID.randomUUID();

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.getWishlist(customerId));
        verify(repository).findByCustomerId(customerId);
        verifyNoInteractions(mapperWishlist);
    }

    @Test
    void quandoProdutoEstaNaWishlist_checkProductDeveRetornarRespostaCorreta() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productId);
        productEntity.setName("Product in Wishlist");
        productEntity.setDescription("Description of product");
        productEntity.setPrice(100L);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(List.of(productEntity));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        ProductCheckResponseDTO response = service.checkProductInWishlist(customerId, productId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(productId, response.getProductId());
        assertTrue(response.isInWishlist());
        assertNotNull(response.getProduct());
        assertEquals("Product in Wishlist", response.getProduct().getName());
        assertEquals("Description of product", response.getProduct().getDescription());
        assertEquals(100L, response.getProduct().getPrice());
        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void quandoProdutoNaoEstaNaWishlist_checkProductDeveRetornarRespostaCorreta() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>());

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        ProductCheckResponseDTO response = service.checkProductInWishlist(customerId, productId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(productId, response.getProductId());
        assertFalse(response.isInWishlist());
        assertNull(response.getProduct());
        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void quandoClienteNaoExiste_checkProductDeveLancarCustomerNotFoundException() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.checkProductInWishlist(customerId, productId));
        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void quandoClienteExisteENovoItemAdicionado_deveAdicionarItemComSucesso() throws Exception {
        UUID customerId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(UUID.randomUUID())
                .name("Product A")
                .build();

        CustomerWishlistRequestDTO requestDTO = CustomerWishlistRequestDTO.builder()
                .customerId(customerId)
                .wishlist(List.of(productDTO))
                .build();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>());

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productDTO.getProductId());

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));
        when(productMapper.toEntity(productDTO)).thenReturn(productEntity);
        when(repository.save(any())).thenReturn(customerEntity);
        when(mapperWishlist.toResponseDTO(any())).thenReturn(CustomerWishlistResponseDTO.builder().build());

        CustomerWishlistResponseDTO response = service.addItemsWishlist(requestDTO);

        assertNotNull(response);
        ArgumentCaptor<CustomerWishlistEntity> captor = ArgumentCaptor.forClass(CustomerWishlistEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(1, captor.getValue().getWishlist().size());
    }

    @Test
    void quandoClienteNaoExiste_deveCriarNovaWishlist() throws Exception {
        UUID customerId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(UUID.randomUUID())
                .name("Product B")
                .build();

        CustomerWishlistRequestDTO requestDTO = CustomerWishlistRequestDTO.builder()
                .customerId(customerId)
                .wishlist(List.of(productDTO))
                .build();

        CustomerWishlistEntity newCustomerEntity = new CustomerWishlistEntity();
        newCustomerEntity.setCustomerId(customerId);
        newCustomerEntity.setWishlist(List.of());

        CustomerWishlistDTO customerWishlistDTO = new CustomerWishlistDTO();
        customerWishlistDTO.setCustomerId(customerId);
        customerWishlistDTO.setWishlist(List.of(productDTO));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(mapperWishlist.requestToDTO(requestDTO)).thenReturn(customerWishlistDTO);
        when(mapperWishlist.toEntity(customerWishlistDTO)).thenReturn(newCustomerEntity);
        when(repository.save(any())).thenReturn(newCustomerEntity);
        when(mapperWishlist.toResponseDTO(any())).thenReturn(CustomerWishlistResponseDTO.builder().build());

        CustomerWishlistResponseDTO response = service.addItemsWishlist(requestDTO);

        assertNotNull(response);
        verify(repository).save(any());
        verify(mapperWishlist).toEntity(customerWishlistDTO);
    }

    @Test
    void quandoAdicionandoItensDuplicados_deveLancarNoItemsAddedException() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(productId)
                .name("Duplicate Product")
                .build();

        CustomerWishlistRequestDTO requestDTO = CustomerWishlistRequestDTO.builder()
                .customerId(customerId)
                .wishlist(List.of(productDTO))
                .build();

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setProductId(productId);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(List.of(existingProduct));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        assertThrows(NoItemsAddedException.class, () -> service.addItemsWishlist(requestDTO));
    }

    @Test
    void quandoAdicionandoItensExcedendoLimite_deveLancarWishlistLimitExceededException() {
        UUID customerId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(UUID.randomUUID())
                .name("New Product")
                .build();

        CustomerWishlistRequestDTO requestDTO = CustomerWishlistRequestDTO.builder()
                .customerId(customerId)
                .wishlist(List.of(productDTO))
                .build();

        List<ProductEntity> currentWishlist = Mockito.mock(List.class);
        when(currentWishlist.size()).thenReturn(20);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(currentWishlist);

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        assertThrows(WishlistLimitExceededException.class, () -> service.addItemsWishlist(requestDTO));
    }

    @Test
    void quandoVerificarProdutoEmWishlistNula_deveRetornarNaoEncontrado() throws CustomerNotFoundException {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CustomerWishlistEntity customer = new CustomerWishlistEntity();
        customer.setCustomerId(customerId);
        customer.setWishlist(null);

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customer));

        ProductCheckResponseDTO result = service.checkProductInWishlist(customerId, productId);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(productId, result.getProductId());
        assertFalse(result.isInWishlist());
        assertNull(result.getProduct());
    }


    @Test
    void quandoClienteExisteMasWishlistNula_deveInicializarWishlist() throws Exception {
        UUID customerId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(UUID.randomUUID())
                .name("Produto Teste")
                .build();

        CustomerWishlistRequestDTO requestDTO = CustomerWishlistRequestDTO.builder()
                .customerId(customerId)
                .wishlist(List.of(productDTO))
                .build();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(null);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productDTO.getProductId());

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));
        when(productMapper.toEntity(productDTO)).thenReturn(productEntity);
        when(repository.save(any())).thenReturn(customerEntity);
        when(mapperWishlist.toResponseDTO(any())).thenReturn(CustomerWishlistResponseDTO.builder().build());

        CustomerWishlistResponseDTO response = service.addItemsWishlist(requestDTO);

        assertNotNull(response);
        ArgumentCaptor<CustomerWishlistEntity> captor = ArgumentCaptor.forClass(CustomerWishlistEntity.class);
        verify(repository).save(captor.capture());

        assertNotNull(captor.getValue().getWishlist());
        assertEquals(1, captor.getValue().getWishlist().size());
    }


    @Test
    void quandoRemovendoProdutosComInputsValidos_deveRemoverComSucesso() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productId);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>(List.of(productEntity)));

        CustomerWishlistEntity savedEntity = new CustomerWishlistEntity();
        savedEntity.setCustomerId(customerId);
        savedEntity.setWishlist(new ArrayList<>());

        CustomerWishlistResponseDTO expectedResponse = CustomerWishlistResponseDTO.builder()
                .customerId(customerId)
                .wishlist(new ArrayList<>())
                .totalPrice(0L)
                .formattedTotalPrice("R$0,00")
                .build();

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));
        when(repository.save(any(CustomerWishlistEntity.class))).thenReturn(savedEntity);
        when(mapperWishlist.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        CustomerWishlistResponseDTO response = service.deleteItemsFromWishlistWithResponse(customerId, List.of(productId));

        assertNotNull(response);
        assertTrue(customerEntity.getWishlist().isEmpty());
        assertEquals(expectedResponse, response);
        verify(repository).save(customerEntity);
    }

    @Test
    void quandoDeletandoItensSemResposta_deveRemoverItensComSucesso() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productId);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>(List.of(productEntity)));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));
        when(repository.save(any(CustomerWishlistEntity.class))).thenReturn(customerEntity);

        service.deleteItemsFromWishlist(customerId, List.of(productId));

        assertTrue(customerEntity.getWishlist().isEmpty());
        verify(repository).findByCustomerId(customerId);
        verify(repository).save(customerEntity);
    }

    @Test
    void quandoRemovendoProdutosComWishlistNula_deveLancarNoItemsDeletedException() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(null);

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        NoItemsDeletedException exception = assertThrows(NoItemsDeletedException.class,
                () -> service.deleteItemsFromWishlistWithResponse(customerId, List.of(productId)));

        assertEquals("Nenhum produto válido foi fornecido para remoção", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void quandoRemovendoProdutosComListaDeProdutosNula_deveLancarNoItemsDeletedException() {
        UUID customerId = UUID.randomUUID();
        List<UUID> nullProductIds = null;

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>());

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        NoItemsDeletedException exception = assertThrows(NoItemsDeletedException.class,
                () -> service.deleteItemsFromWishlistWithResponse(customerId, nullProductIds));

        assertEquals("Nenhum produto válido foi fornecido para remoção", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void quandoRemovendoProdutosComListaDeProdutosVazia_deveLancarNoItemsDeletedException() {
        UUID customerId = UUID.randomUUID();
        List<UUID> emptyProductIds = new ArrayList<>();

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>());

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        NoItemsDeletedException exception = assertThrows(NoItemsDeletedException.class,
                () -> service.deleteItemsFromWishlistWithResponse(customerId, emptyProductIds));

        assertEquals("Nenhum produto válido foi fornecido para remoção", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void quandoProdutosNaoEncontradosNaWishlist_deveLancarNoItemsDeletedException() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID nonExistingProductId = UUID.randomUUID();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productId);

        CustomerWishlistEntity customerEntity = new CustomerWishlistEntity();
        customerEntity.setCustomerId(customerId);
        customerEntity.setWishlist(new ArrayList<>(List.of(productEntity)));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(customerEntity));

        NoItemsDeletedException exception = assertThrows(NoItemsDeletedException.class,
                () -> service.deleteItemsFromWishlistWithResponse(customerId, List.of(nonExistingProductId)));

        assertEquals("Nenhum dos produtos fornecidos foi encontrado na wishlist", exception.getMessage());
        assertEquals(1, customerEntity.getWishlist().size());
        verify(repository, never()).save(any());
    }
}
