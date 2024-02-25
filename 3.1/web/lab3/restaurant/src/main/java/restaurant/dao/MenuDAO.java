package restaurant.dao;

import jakarta.persistence.*;
import restaurant.exceptions.DAOException;
import jakarta.persistence.criteria.*;
import restaurant.models.Menu;
import restaurant.models.MenuItem;
import restaurant.models.MenuItem_;

import java.util.List;

public class MenuDAO {
    private final EntityManagerFactory emf;

    public MenuDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Метод для получения всего меню
    public Menu getMenu() throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> cq = cb.createQuery(MenuItem.class);
            Root<MenuItem> menuItemRoot = cq.from(MenuItem.class);
            cq.select(menuItemRoot);
            TypedQuery<MenuItem> query = em.createQuery(cq);
            List<MenuItem> menuItems = query.getResultList();

            Menu menu = new Menu();
            menu.setMenuItems(menuItems);
            return menu;
        } catch (Exception e) {
            throw new DAOException("Error fetching menu", e);
        } finally {
            em.close();
        }
    }

    // Метод для получения элемента меню по ID
    public MenuItem getMenuItemById(int id) throws DAOException {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> cq = cb.createQuery(MenuItem.class);
            Root<MenuItem> menuItemRoot = cq.from(MenuItem.class);
            cq.select(menuItemRoot).where(cb.equal(menuItemRoot.get(MenuItem_.id), id));
            TypedQuery<MenuItem> query = em.createQuery(cq);
            MenuItem menuItem = query.getSingleResult();
            return menuItem;
        } catch (Exception e) {
            throw new DAOException("Error fetching menu item by ID: " + id, e);
        } finally {
            em.close();
        }
    }

    // Метод для добавления нового элемента меню
    public boolean addMenuItem(MenuItem menuItem) throws DAOException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(menuItem);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DAOException("Error adding new menu item: " + menuItem.getName(), e);
        } finally {
            em.close();
        }
    }
}
