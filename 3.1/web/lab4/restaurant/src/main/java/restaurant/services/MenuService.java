package restaurant.services;

import restaurant.dao.MenuDAO;
import restaurant.entity.Menu;
import restaurant.entity.MenuItem;
import restaurant.exceptions.DAOException;
import restaurant.exceptions.MenuServiceException;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;

public class MenuService {
    private final MenuDAO menuDAO;

    public MenuService(EntityManagerFactory emf) {
        this.menuDAO = new MenuDAO(emf);
    }

    // Получить всё меню
    public Menu getMenu() throws MenuServiceException {
        try {
            return menuDAO.getMenu();
        } catch (DAOException e) {
            throw new MenuServiceException("Failed to retrieve menu.", e);
        }
    }

    // Получить элемент меню по ID
    public MenuItem getMenuItemById(int id) throws MenuServiceException {
        try {
            return menuDAO.getMenuItemById(id);
        } catch (DAOException e) {
            throw new MenuServiceException("Failed to retrieve menu item by ID: " + id, e);
        }
    }

    // Добавить элемент меню
    public boolean addMenuItem(String name, double price, String category) throws MenuServiceException {
        try {
            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setPrice(BigDecimal.valueOf(price));
            menuItem.setCategory(category);
            return menuDAO.addMenuItem(menuItem);
        } catch (DAOException e) {
            throw new MenuServiceException("Failed to add menu item: " + name, e);
        }
    }
}
