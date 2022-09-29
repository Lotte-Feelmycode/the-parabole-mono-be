package com.feelmycode.parabole.controller;

import com.feelmycode.parabole.domain.Product;
import com.feelmycode.parabole.domain.ProductDetail;
import com.feelmycode.parabole.domain.User;
import com.feelmycode.parabole.dto.ProductDetailListResponseDto;
import com.feelmycode.parabole.global.api.ParaboleResponse;
import com.feelmycode.parabole.global.error.exception.ParaboleException;
import com.feelmycode.parabole.service.ProductDetailService;
import com.feelmycode.parabole.service.ProductService;
import com.feelmycode.parabole.service.SellerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductDetailService productDetailService;
    private final static int DEFAULT_PAGE = 0;
    private final static int DEFAULT_SIZE = 20;

    // TODO: DTO를 사용해서 parameter를 깔끔하게 받고 한번에 NULL처리를 해서 초기화하기
    // +@ Valid를 custom해서 validation할 때 인터페이스 받아서 커스텀으로 초기화할 수도 있음
    @GetMapping("/list")
    public ResponseEntity<ParaboleResponse> getProductList(@RequestParam(required=false) Long sellerId,
                                            @RequestParam(required = false) String sellerName,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) String productName,
                                            @RequestParam(required = false) Pageable pageable) {
        Long getSellerId = 0L;
        String getSellerName = "";
        String getCategory = "";
        String getProductName = "";
        Pageable getPageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);

        if(sellerId != null || sellerId != 0L) {
            getSellerId = sellerId;
        }
        if(sellerName != null && !sellerName.equals("null") && !sellerName.equals("")) {
            getSellerName = sellerName;
        }
        if(category != null && !category.equals("null") && !category.equals("")) {
            getCategory = category;
        }
        if(productName != null && !productName.equals("null") && !productName.equals("")) {
            if(productName.equals("error")) {
                throw new ParaboleException(HttpStatus.BAD_REQUEST, "error test");
            }
            getProductName = productName;
        }
        if(pageable != null) {
            getPageable = pageable;
        }

        Page<Product> response = productService.getProductList(getSellerId, getSellerName,
            getProductName, getCategory, getPageable);
            
        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "상품 전시", response);
    }

    @PostMapping
    public ResponseEntity<ParaboleResponse> createProduct(@RequestParam Long userId, @RequestBody Product product) {
    
        log.info("TEST {}", "INFO");
        log.warn("TEST {}", "WARN");
        log.error("TEST {}", "ERROR");
        log.error("TEST {}({})", "ERROR", "ERROR");
        
        productService.saveProduct(userId, product);
        
        return ParaboleResponse.CommonResponse(HttpStatus.CREATED, true, "상품 생성");
    }

    @PostMapping("/detail")
    public ResponseEntity<ParaboleResponse> createProductDetail(@RequestBody ProductDetail productDetail, @RequestParam Long userId) {
        productDetailService.createProductDetail(userId, productDetail);
        return ParaboleResponse.CommonResponse(HttpStatus.CREATED, true, "상품 상세 정보 추가");
    }

    @PatchMapping
    public ResponseEntity<ParaboleResponse> updateProduct(@RequestParam Long userId, @RequestBody Product product) {

        productService.updateProduct(userId, product);

        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "상품정보 수정");
    }

    @GetMapping
    public ResponseEntity<ParaboleResponse> getProduct(@RequestParam Long productId) {

        Product product = productService.getProduct(productId);
        List<ProductDetail> productDetailList = productDetailService.getProductDetailList(productId);

        ProductDetailListResponseDto responseDto = new ProductDetailListResponseDto(product, productDetailList);

        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "상품 상세 정보", responseDto);
    }
}
