package restaurant.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.Order;
import restaurant.repository.OrderRepository;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderRestController {

    @Autowired
    private OrderRepository orderRepository;

    // GET /api/orders
    @GetMapping
    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // POST /api/orders
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // Можно дополнительно проверять поля, статус и т.д.
        return orderRepository.save(order);
    }

    // PUT /api/orders/{id}
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Integer id, @RequestBody Order updated) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(updated.getStatus());
        order.setClient(updated.getClient());
        order.setOrderItems(updated.getOrderItems());
        return orderRepository.save(order);
    }

    // DELETE /api/orders/{id}
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderRepository.deleteById(id);
    }
}
