package restaurant.dao;

import jakarta.persistence.*;
import restaurant.entity.Client;
import restaurant.entity.Order;
import restaurant.entity.OrderItem;
import restaurant.exceptions.DAOException;
import restaurant.entity.Order_;
import restaurant.entity.Client_;
import jakarta.persistence.criteria.*;

import java.util.List;

public class OrderDAO {
    private final EntityManagerFactory emf;

    public OrderDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Получение всех заказов клиента с элементами заказа
    public List<Order> getOrdersByClientId(int clientId) throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> orderRoot = cq.from(Order.class);
            cq.select(orderRoot).where(cb.equal(orderRoot.get(Order_.client).get(Client_.id), clientId));
            TypedQuery<Order> query = em.createQuery(cq);
            List<Order> orders = query.getResultList();
            return orders;
        } catch (Exception e) {
            throw new DAOException("Error fetching orders by client ID: " + clientId, e);
        } finally {
            em.close();
        }
    }

    // Получение заказа по ID
    public Order getOrderById(int orderId) throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> orderRoot = cq.from(Order.class);
            cq.select(orderRoot).where(cb.equal(orderRoot.get(Order_.id), orderId));
            TypedQuery<Order> query = em.createQuery(cq);
            Order order = query.getSingleResult();
            return order;
        } catch (Exception e) {
            throw new DAOException("Error fetching order by ID: " + orderId, e);
        } finally {
            em.close();
        }
    }

    // Создание нового заказа с элементами
    public boolean createOrder(Client client, List<OrderItem> items) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Order order = new Order();
            order.setClient(client);
            order.setStatus(Order.pendingStatus);
            em.persist(order);

            for (OrderItem item : items) {
                item.setOrder(order);
                em.persist(item);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error creating order for client ID: " + client.getId(), e);
        } finally {
            em.close();
        }
    }

    // Обновление статуса заказа
    public boolean updateOrderStatus(int orderId, String status) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Order> update = cb.createCriteriaUpdate(Order.class);
            Root<Order> orderRoot = update.from(Order.class);
            update.set(orderRoot.get(Order_.status), status)
                    .where(cb.equal(orderRoot.get(Order_.id), orderId));
            Query query = em.createQuery(update);
            int rowsUpdated = query.executeUpdate();
            transaction.commit();
            return rowsUpdated > 0;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error updating status for order ID: " + orderId, e);
        } finally {
            em.close();
        }
    }

    // Подтверждение заказа
    public boolean confirmOrder(int orderId) throws DAOException {
        return updateOrderStatus(orderId, Order.cookingStatus);
    }

    // Заказ готов к выдаче
    public boolean orderReady(int orderId) throws DAOException {
        return updateOrderStatus(orderId, Order.cookedStatus);
    }

    // Оплатить заказ
    public boolean completeOrder(int orderId) throws DAOException {
        return updateOrderStatus(orderId, Order.completedStatus);
    }
}
