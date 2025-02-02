package com.feelmycode.parabole.controller;

import com.feelmycode.parabole.domain.Product;
import com.feelmycode.parabole.dto.ProductDetailListResponseDto;
import com.feelmycode.parabole.dto.ProductDto;
import com.feelmycode.parabole.dto.ProductRequestDto;
import com.feelmycode.parabole.dto.ProductResponseDto;
import com.feelmycode.parabole.global.api.ParaboleResponse;
import com.feelmycode.parabole.global.error.exception.ParaboleException;
import com.feelmycode.parabole.global.util.StringUtil;
import com.feelmycode.parabole.service.ProductDetailService;
import com.feelmycode.parabole.service.ProductService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

    private final static int DEFAULT_SIZE = 20;
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    @GetMapping("/list")
    public ResponseEntity<ParaboleResponse> getProductList(@RequestParam(required = false) String sellerId,
                                            @RequestParam(required = false) String storeName,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) String productName,
                                            @PageableDefault(size = DEFAULT_SIZE) Pageable pageable) {

        Long getSellerId = 0L;
        String getStoreName = StringUtil.controllerParamIsBlank(storeName) ? "" : storeName;
        String getCategory = StringUtil.controllerParamIsBlank(category) ? "" : category;
        String getProductName = StringUtil.controllerParamIsBlank(productName) ? "" : productName;

        if(!StringUtil.controllerParamIsBlank(sellerId)) {
            try {
                getSellerId = Long.parseLong(sellerId);
            } catch (NumberFormatException e) {
                throw new ParaboleException(HttpStatus.BAD_REQUEST, "잘못된 판매자 id 입니다. 상품목록 조회에 실패했습니다.");
            }
        }

        log.info("getProductList - getSellerId : {} / getStoreName : {} / getProductName : {} / getCategory : {} / getPageable : {}", getSellerId, getStoreName, getProductName, getCategory, pageable);
        Page<ProductDto> response = productService.getProductList(getSellerId, getStoreName,
            getProductName, getCategory, pageable);

        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "상품 전시", response);
    }

    @GetMapping("/data")
    public ProductResponseDto getProducts(@RequestParam Long productId) {
        Product response = productService.getProduct(productId);
        ProductResponseDto dto = new ProductResponseDto(response.getId(), response.getName(), response.getThumbnailImg());
        return dto;
    }

    @PostMapping
    public ResponseEntity<ParaboleResponse> createProduct(@RequestAttribute("userId") Long userId, @RequestBody ProductRequestDto product) {
        Long productId = productService.saveProduct(userId, product);
        return ParaboleResponse.CommonResponse(HttpStatus.CREATED, true, "상품 생성", productId);
    }

    @GetMapping
    public ResponseEntity<ParaboleResponse> getProduct(@RequestParam Long productId) {
        ProductDetailListResponseDto response = productService.getProductDetail(productId);
        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "상품 상세 정보", response);
    }

    @GetMapping("/seller/list")
    public ResponseEntity<ParaboleResponse> getProductBySellerId(@RequestAttribute Long sellerId, @PageableDefault(size = DEFAULT_SIZE) Pageable pageable) {
        log.info("Get Product By Seller Id : {} ", sellerId);
        Page<ProductDto> response = productService.getProductList(sellerId, "", "", "", pageable);
        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "판매자가 등록한 상품 목록", response);
    }

    @GetMapping("/{productId}/stock/{stock}")
    public Boolean setProductRemains(@PathVariable("productId") Long productId,
        @PathVariable("stock") Integer stock) {
        log.info("Set Product Remains By Event Server : {} ", productId);
        return productService.setProductRemains(productId, Long.valueOf(stock));
    }
}
