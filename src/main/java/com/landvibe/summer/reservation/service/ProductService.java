package com.landvibe.summer.reservation.service;

import com.landvibe.summer.reservation.dto.request.ProductRequest;
import com.landvibe.summer.reservation.entity.Product;
import com.landvibe.summer.reservation.dto.response.ProductDetail;
import com.landvibe.summer.reservation.dto.response.Products;
import com.landvibe.summer.reservation.repository.CategoryRepository;
import com.landvibe.summer.reservation.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    public Long create(ProductRequest request) {
        Product product = Product.builder()
                .category(categoryRepository.getCategory(request.getCategoryId()).orElse(null))
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        validate(product, request.getCategoryId());
        save(product);
        return product.getId();
    }

    public List<Products> getProductsRes() {
        List<Product> product = productRepository.findAll();
        List<Products> products = new ArrayList<>();
        for (int i = 0; i < product.size(); i++) {
            Product nowPro = product.get(i);
            Products nowPros = Products.builder()
                    .id(nowPro.getId())
                    .categoryId(nowPro.getCategory().getCateId())
                    .name(nowPro.getName())
                    .createdAt(nowPro.getCreatedAt())
                    .build();
            products.add(nowPros);
        }
        return products;
    }

    public ProductDetail productDetail(Long productId) {
        Product product = productRepository.getReferenceById(productId);
        ProductDetail productDetail = ProductDetail.builder()
                .categoryId(product.getCategory().getCateId())
                .categoryName(product.getCategory().getCateName())
                .name(product.getName())
                .description(product.getDescription())
                .id(product.getId())
                .createdAt(product.getCreatedAt())
                .build();
        return productDetail;
    }

    public void validate(Product product, Long categoryId) throws IllegalStateException {
        List<Product> products = productRepository.findAll();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(product.getName())) {
                throw new IllegalStateException("이미 존재하는 상품입니다.");
            } else if (categoryRepository.findById(categoryId).orElse(null) == null) {
                throw new IllegalStateException("해당 카테고리가 존재하지 않습니다");
            }
        }
    }
}
