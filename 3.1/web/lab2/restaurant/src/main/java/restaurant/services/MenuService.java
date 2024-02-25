package restaurant.services;

import restaurant.dao.MenuDAO;
import restaurant.exceptions.DAOException;
import restaurant.exceptions.MenuServiceException;
import restaurant.models.Menu;
import restaurant.models.Menu.MenuItem;

public class MenuService {

    private static MenuService instance;
    private final MenuDAO menuDAO;

    private MenuService() {
        this.menuDAO = new MenuDAO();
    }

    public static synchronized MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
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
    public boolean addMenuItem(String name, double price, int categoryId) throws MenuServiceException {
        try {
            MenuItem menuItem = new MenuItem(0, name, new java.math.BigDecimal(price),
                    new Menu.Category(categoryId, null));
            return menuDAO.addMenuItem(menuItem);
        } catch (DAOException e) {
            throw new MenuServiceException("Failed to add menu item: " + name, e);
        }
    }
}
