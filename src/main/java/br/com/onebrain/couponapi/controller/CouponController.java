package br.com.onebrain.couponapi.controller;

import br.com.onebrain.couponapi.dto.CouponResponse;
import br.com.onebrain.couponapi.dto.CreateCouponRequest;
import br.com.onebrain.couponapi.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import br.com.onebrain.couponapi.dto.UpdateCouponRequest;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        CouponResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<CouponResponse> result = service.list(p);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{code}")
    public ResponseEntity<CouponResponse> update(@PathVariable String code, @Valid @RequestBody UpdateCouponRequest request) {
        CouponResponse response = service.update(code, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getByCode(@PathVariable String code) {
        CouponResponse response = service.findByCode(code);
        return ResponseEntity.ok(response);
    }
}

