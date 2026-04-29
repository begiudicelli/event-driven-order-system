package com.backend.event_driven_order_system.service;

import com.backend.event_driven_order_system.dto.requests.CreateProductRequest;
import com.backend.event_driven_order_system.dto.responses.PageResponse;
import com.backend.event_driven_order_system.dto.responses.ProductResponse;
import com.backend.event_driven_order_system.entity.Product;
import com.backend.event_driven_order_system.exception.ProductAlreadyExistsException;
import com.backend.event_driven_order_system.exception.ProductInactiveException;
import com.backend.event_driven_order_system.exception.ResourceNotFoundException;
import com.backend.event_driven_order_system.mapper.ProductMapper;
import com.backend.event_driven_order_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductResponse create(CreateProductRequest request) {

        if (repository.existsByName(request.name())) {
            throw new ProductAlreadyExistsException("Product already exists");
        }

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setActive(true);

        return mapper.toResponse(repository.save(product));
    }

    public ProductResponse getById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        return mapper.toResponse(product);
    }

    public PageResponse<ProductResponse> getAll(Pageable pageable) {

        Page<ProductResponse> page = repository.findAll(pageable)
                .map(mapper::toResponse);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public ProductResponse update(Long id, CreateProductRequest request) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!product.isActive()) {
            throw new ProductInactiveException("Cannot update inactive product");
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        return mapper.toResponse(repository.save(product));
    }

    public void deactivate(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        product.setActive(false);
        repository.save(product);
    }
}