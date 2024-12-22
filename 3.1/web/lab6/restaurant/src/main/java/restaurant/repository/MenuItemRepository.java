package restaurant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.MenuItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends CrudRepository<MenuItem, Integer> {

    // Поиск по названию (если предполагается, что названия уникальны, можно вернуть
    // Optional<MenuItem>).
    Optional<MenuItem> findByName(String name);

    // Поиск всех элементов по категории (например, "main course", "dessert",
    // "drink").
    List<MenuItem> findByCategory(String category);

    // Поиск элементов в заданном ценовом диапазоне.
    List<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
