package org.example.orderservice.feign;

import org.example.orderservice.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping("/api/{id}")
    ProductDTO getProductByID(@PathVariable("id") Long id);

    @PostMapping("/api/by-ids")
    List<ProductDTO> getProductsByID(@RequestBody List<Long> ids);

    @GetMapping("/api/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

}