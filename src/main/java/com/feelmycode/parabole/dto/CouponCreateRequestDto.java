package com.feelmycode.parabole.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.feelmycode.parabole.domain.CouponType;
//import com.feelmycode.parabole.domain.Seller;
import com.feelmycode.parabole.domain.Coupon;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CouponCreateRequestDto {

        private String name;
        private Long sellerId;
        private Integer type;
        private Integer discountRate;
        private Long discountAmount;
        private LocalDateTime validAt;
        private LocalDateTime expiresAt;
        private Long maxDiscountAmount;
        private Long minPaymentAmount;
        private String detail;
        private Integer cnt;    // 발행할 쿠폰수

        /** DTO to Entity **/
        public Coupon toEntity(Long sellerId, Integer type){

                return new Coupon(name, sellerId, type, discountRate,
                    discountAmount, validAt, expiresAt,
                    maxDiscountAmount, minPaymentAmount, detail,
                    cnt);
        }
}
