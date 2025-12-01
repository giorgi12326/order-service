package org.example.orderservice.controller;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.CancelOrderDTO;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.service.OrderService;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@EnableCaching
public class OrderController {

    public final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody CancelOrderDTO order) {
        orderService.cancelOrder(order);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDTO> payForOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.payForOrder(id));
    }

    @PostMapping("/{id}/unpay")
    public ResponseEntity<OrderDTO> unpayForOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.unpayForOrder(id));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @RequestBody OrderDTO order) {
//        return ResponseEntity.status(HttpStatus.OK).body(orderService.update(order, id));
//    }
}
