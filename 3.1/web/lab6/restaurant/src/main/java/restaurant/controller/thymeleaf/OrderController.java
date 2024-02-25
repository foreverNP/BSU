package restaurant.controller.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.Client;
import restaurant.entity.MenuItem;
import restaurant.entity.Order;
import restaurant.entity.OrderItem;
import restaurant.repository.ClientRepository;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.OrderItemRepository;
import restaurant.repository.OrderRepository;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping
    public String getAllOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "order/list"; // шаблон order/list.html
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("clients", clientRepository.findAll());
        // Здесь можно передавать список MenuItem для будущего добавления в заказ
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "order/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute Order order, @RequestParam Integer clientId) {
        // Привязка к клиенту
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        order.setClient(client);
        order.setStatus(Order.pendingStatus);

        // Сохраняем заказ
        orderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Integer id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "order/details"; // order/details.html
    }

    @GetMapping("/addItem/{orderId}")
    public String showAddItemForm(@PathVariable Integer orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "order/add-item"; // order/add-item.html
    }

    @PostMapping("/addItem/{orderId}")
    public String addItemToOrder(@PathVariable Integer orderId,
            @RequestParam Integer menuItemId,
            @RequestParam Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        OrderItem orderItem = new OrderItem(order, menuItem, quantity);
        order.addOrderItem(orderItem);

        orderRepository.save(order);

        return "redirect:/orders/" + orderId;
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);

        return "order/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Integer id, @RequestParam String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }
}
