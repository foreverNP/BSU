package restaurant.controller.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.Client;
import restaurant.repository.ClientRepository;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public String getAllClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "client/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("client", new Client());
        return "client/create";
    }

    @PostMapping("/create")
    public String createClient(@ModelAttribute Client client) {
        clientRepository.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Integer id, Model model) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "client/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateClient(@PathVariable Integer id, @ModelAttribute Client updatedClient) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
        client.setName(updatedClient.getName());
        client.setBalance(updatedClient.getBalance());
        clientRepository.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Integer id) {
        clientRepository.deleteById(id);
        return "redirect:/clients";
    }
}
