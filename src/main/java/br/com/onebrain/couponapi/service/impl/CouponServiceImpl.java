package br.com.onebrain.couponapi.service.impl;

import br.com.onebrain.couponapi.dto.CouponResponse;
import br.com.onebrain.couponapi.dto.CreateCouponRequest;
import br.com.onebrain.couponapi.dto.UpdateCouponRequest;
import br.com.onebrain.couponapi.exception.ResourceNotFoundException;
import br.com.onebrain.couponapi.model.Coupon;
import br.com.onebrain.couponapi.repository.CouponRepository;
import br.com.onebrain.couponapi.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;

    public CouponServiceImpl(CouponRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CouponResponse create(CreateCouponRequest request) {

        String normalized = request.getCode().replaceAll("[^A-Za-z0-9]", "").toUpperCase();

        if (normalized.length() != 6) {
            throw new IllegalArgumentException("Coupon code must have exactly 6 alphanumeric characters after removing special characters");
        }

        LocalDate today = LocalDate.now();
        if (request.getExpirationDate().isBefore(today)) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }

        if (repository.existsByCode(normalized)) {
            throw new IllegalArgumentException("Coupon with code already exists");
        }

        Coupon entity = new Coupon();
        entity.setCode(normalized);
        entity.setDescription(request.getDescription());
        entity.setDiscountValue(request.getDiscountValue());
        entity.setExpirationDate(request.getExpirationDate());
        entity.setPublished(request.isPublished());

        Coupon saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteByCode(String code) {
        String normalized = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        Optional<Coupon> found = repository.findByCode(normalized);
        if (found.isEmpty()) {
            throw new ResourceNotFoundException("Coupon not found");
        }
        Coupon coupon = found.get();
        if (coupon.isDeleted()) {
            throw new IllegalStateException("Coupon already deleted");
        }
        coupon.setDeleted(true);
        coupon.setDeletedAt(OffsetDateTime.now());
        repository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse findByCode(String code) {
        String normalized = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        Coupon coupon = repository.findByCode(normalized).orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        return toResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponse> list(Pageable pageable) {
        var page = repository.findAllByDeletedFalse(pageable);
        List<CouponResponse> content = page.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public CouponResponse update(String code, UpdateCouponRequest request) {
        String normalized = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        Coupon coupon = repository.findByCode(normalized).orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        LocalDate today = LocalDate.now();
        if (request.getExpirationDate().isBefore(today)) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }

        coupon.setDescription(request.getDescription());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setPublished(request.isPublished());

        Coupon saved = repository.save(coupon);
        return toResponse(saved);
    }

    private CouponResponse toResponse(Coupon c) {
        CouponResponse r = new CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setDescription(c.getDescription());
        r.setDiscountValue(c.getDiscountValue());
        r.setExpirationDate(c.getExpirationDate());
        r.setPublished(c.isPublished());
        r.setDeleted(c.isDeleted());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}

