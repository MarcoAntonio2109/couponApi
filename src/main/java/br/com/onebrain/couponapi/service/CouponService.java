package br.com.onebrain.couponapi.service;

import br.com.onebrain.couponapi.dto.CouponResponse;
import br.com.onebrain.couponapi.dto.CreateCouponRequest;
import br.com.onebrain.couponapi.dto.UpdateCouponRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponResponse create(CreateCouponRequest request);
    void deleteByCode(String code);
    CouponResponse findByCode(String code);
    Page<CouponResponse> list(Pageable pageable);
    CouponResponse update(String code, UpdateCouponRequest request);
}

