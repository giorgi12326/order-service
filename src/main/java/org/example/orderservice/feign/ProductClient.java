package org.example.orderservice.feign;

import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @Retryable(retryFor = feign.RetryableException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @PostMapping("/api/products-info")
    List<ReserveResponseDTO> getInfoAboutProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

}