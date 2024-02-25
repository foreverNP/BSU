package restaurant.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.MenuItem;
import restaurant.repository.MenuItemRepository;

import java.util.List;

@RestController
@RequestMapping("/api/menuItems")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuItemRestController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // GET /api/menuItems
    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return (List<MenuItem>) menuItemRepository.findAll();
    }

    // GET /api/menuItems/{id}
    @GetMapping("/{id}")
    public MenuItem getById(@PathVariable Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
    }

    // POST /api/menuItems
    @PostMapping
    public MenuItem createMenuItem(@RequestBody MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // PUT /api/menuItems/{id}
    @PutMapping("/{id}")
    public MenuItem updateMenuItem(@PathVariable Integer id, @RequestBody MenuItem updated) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        item.setName(updated.getName());
        item.setPrice(updated.getPrice());
        item.setCategory(updated.getCategory());
        return menuItemRepository.save(item);
    }

    // DELETE /api/menuItems/{id}
    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Integer id) {
        menuItemRepository.deleteById(id);
    }
}
