package restaurant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.MenuItem;
import restaurant.repository.MenuItemRepository;

@Controller
@RequestMapping("/menuItems")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public String getAllMenuItems(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        return "menu/list"; // вернёт шаблон menu/list.html
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        return "menu/create"; // вернёт шаблон menu/create.html
    }

    @PostMapping("/create")
    public String createMenuItem(@ModelAttribute MenuItem menuItem) {
        menuItemRepository.save(menuItem);
        return "redirect:/menuItems";
    }

    @GetMapping("/edit/{id}")
    public String editMenuItem(@PathVariable Integer id, Model model) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        model.addAttribute("menuItem", item);
        return "menu/edit"; // вернёт шаблон menu/edit.html
    }

    @PostMapping("/edit/{id}")
    public String updateMenuItem(@PathVariable Integer id, @ModelAttribute MenuItem updatedItem) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        item.setName(updatedItem.getName());
        item.setPrice(updatedItem.getPrice());
        item.setCategory(updatedItem.getCategory());
        menuItemRepository.save(item);
        return "redirect:/menuItems";
    }

    @GetMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Integer id) {
        menuItemRepository.deleteById(id);
        return "redirect:/menuItems";
    }
}
