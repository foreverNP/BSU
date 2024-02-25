package restaurant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import restaurant.entity.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, Integer> {

    // Найти клиента по имени (возвращает Optional, если нужен только 1 результат).
    Optional<Client> findByName(String name);
}
