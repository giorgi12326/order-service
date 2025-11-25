package org.example.orderservice.feign;

import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @PostMapping("/api/reserve")
    List<ReserveResponseDTO> getAndReserveProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

    @PostMapping("/api/reserve/compensate")
    void compensateReserveProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

}