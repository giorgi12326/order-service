package org.example.orderservice.feign;

import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    @Retryable(retryFor = feign.RetryableException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @PostMapping("/api/product/reserve")
    List<ReserveProductDTO> getAndReserveProducts(
        @RequestBody List<ReserveProductDTO> reserveProductDTO,
        @RequestHeader("idempotency-key") String idempotencyKey);

    @PostMapping("/api/product/release")
    void compensateReserveProducts(@RequestHeader String idempotencyKey);

}