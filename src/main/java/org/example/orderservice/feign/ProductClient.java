package org.example.orderservice.feign;

import org.example.orderservice.dtos.ProductDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping("/api/{id}")
    ProductDTO getProductByID(@PathVariable("id") Long id);

    @PostMapping("/api/by-ids")
    List<ProductDTO> getProductsByID(@RequestBody List<Long> ids);

    @PostMapping("/api/reserve")
    List<ReserveResponseDTO> getAndReserveProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

    @GetMapping("/api/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);

}