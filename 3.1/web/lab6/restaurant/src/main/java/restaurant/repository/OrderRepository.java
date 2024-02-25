package restaurant.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.Order;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    // Найти все заказы конкретного клиента по его ID.
    List<Order> findAllByClientId(Integer clientId);

    // Найти все заказы по статусу (pending, cooking, cooked, completed)
    List<Order> findAllByStatus(String status);

    // Обновить статус заказа (используется JPQL-запрос).
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
