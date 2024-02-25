package restaurant.dao;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import restaurant.exceptions.DAOException;
import restaurant.models.Client;
import restaurant.models.Client_;

import java.math.BigDecimal;
import java.util.List;

public class ClientDAO {
    private final EntityManagerFactory emf;

    public ClientDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Получение всех клиентов
    public List<Client> getAllClients() throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Client> cq = cb.createQuery(Client.class);
            Root<Client> clientRoot = cq.from(Client.class);
            cq.select(clientRoot);
            TypedQuery<Client> query = em.createQuery(cq);
            List<Client> clients = query.getResultList();
            return clients;
        } catch (Exception e) {
            throw new DAOException("Error getting all clients", e);
        } finally {
            em.close();
        }
    }

    // Получение клиента по ID
    public Client getClientById(int clientId) throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Client> cq = cb.createQuery(Client.class);
            Root<Client> clientRoot = cq.from(Client.class);
            cq.select(clientRoot).where(cb.equal(clientRoot.get(Client_.id), clientId));
            TypedQuery<Client> query = em.createQuery(cq);
            Client client = query.getSingleResult();
            return client;
        } catch (Exception e) {
            throw new DAOException("Error getting client by ID: " + clientId, e);
        } finally {
            em.close();
        }
    }

    // Обновление баланса клиента
    public boolean updateClientBalance(int clientId, double newBalance) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Client> update = cb.createCriteriaUpdate(Client.class);
            Root<Client> clientRoot = update.from(Client.class);
            update.set(clientRoot.get(Client_.balance), BigDecimal.valueOf(newBalance))
                    .where(cb.equal(clientRoot.get(Client_.id), clientId));
            Query query = em.createQuery(update);
            int rowsUpdated = query.executeUpdate();
            transaction.commit();
            return rowsUpdated > 0;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error updating client balance for ID: " + clientId, e);
        } finally {
            em.close();
        }
    }

    // Добавление нового клиента
    public boolean addClient(Client client) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(client);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error adding new client: " + client.getName(), e);
        } finally {
            em.close();
        }
    }
}
