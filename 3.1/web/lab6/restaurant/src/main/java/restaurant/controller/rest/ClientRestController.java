package restaurant.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import restaurant.entity.Client;
import restaurant.repository.ClientRepository;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientRestController {

    @Autowired
    private ClientRepository clientRepository;

    // GET /api/clients
    @GetMapping
    public List<Client> getAllClients() {
        return (List<Client>) clientRepository.findAll();
    }

    // GET /api/clients/{id}
    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    // POST /api/clients
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    // PUT /api/clients/{id}
    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Integer id, @RequestBody Client updated) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setName(updated.getName());
        client.setBalance(updated.getBalance());
        return clientRepository.save(client);
    }

    // DELETE /api/clients/{id}
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Integer id) {
        clientRepository.deleteById(id);
    }
}
