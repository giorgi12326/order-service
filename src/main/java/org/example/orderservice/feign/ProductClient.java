package org.example.orderservice.feign;

import org.example.orderservice.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping("/api/{id}")
    ProductDTO getProductByID(@PathVariable("id") Long id);

    @GetMapping("/api/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

}