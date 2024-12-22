package restaurant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

    // Найти все позиции заказа по ID заказа.
    List<OrderItem> findAllByOrderId(Integer orderId);

    // Найти все позиции заказа, в которых использован конкретный пункт меню.
    List<OrderItem> findAllByMenuItemId(Integer menuItemId);
}
