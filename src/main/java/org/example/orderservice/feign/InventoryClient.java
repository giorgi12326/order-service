package org.example.orderservice.feign;

import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    @PostMapping("/api/product/reserve")
    List<ReserveResponseDTO> getAndReserveProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

    @PostMapping("/api/product/release")
    void compensateReserveProducts(@RequestBody List<ReserveProductDTO> reserveProductDTO);

}