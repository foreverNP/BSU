package restaurant.services;

import restaurant.dao.ClientDAO;
import restaurant.exceptions.ClientServiceException;
import restaurant.exceptions.DAOException;
import restaurant.models.Client;

import java.util.List;

public class ClientService {
    private static ClientService instance;
    private final ClientDAO clientDAO;

    private ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    // Получение всех клиентов
    public List<Client> getAllClients() throws ClientServiceException {
        try {
            return clientDAO.getAllClients();
        } catch (DAOException e) {
            throw new ClientServiceException("Failed to retrieve clients.", e);
        }
    }

    // Получение клиента по ID
    public Client getClientById(int clientId) throws ClientServiceException {
        try {
            return clientDAO.getClientById(clientId);
        } catch (DAOException e) {
            throw new ClientServiceException("Failed to retrieve client by ID: " + clientId, e);
        }
    }

    // Создание нового клиента
    public boolean addClient(String name, double balance) throws ClientServiceException {
        try {
            Client client = new Client(0, name, new java.math.BigDecimal(balance));
            return clientDAO.addClient(client);
        } catch (DAOException e) {
            throw new ClientServiceException("Failed to add new client: " + name, e);
        }
    }

    // Обновление баланса клиента
    public boolean updateClientBalance(int clientId, double newBalance) throws ClientServiceException {
        try {
            return clientDAO.updateClientBalance(clientId, newBalance);
        } catch (DAOException e) {
            throw new ClientServiceException("Failed to update balance for client ID: " + clientId, e);
        }
    }
}
