package restaurant.managers;

import java.util.List;

import restaurant.entity.Menu;
import restaurant.entity.MenuItem;

import java.util.ArrayList;

public class MenuManager {
    private Menu menu;

    public MenuManager(Menu menu) {
        this.menu = menu;
    }

    public void addMenuItem(MenuItem item) {
        menu.addMenuItem(item);
    }

    public void sortMenuItemsByPrice() {
        menu.getMenuItems().sort(new MenuItemPriceComparator());
    }

    public List<MenuItem> searchMenuItemsByName(String name) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : menu.getMenuItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                result.add(item);
            }
        }
        return result;
    }

    public MenuItem searchMenuItemById(int id) {
        for (MenuItem item : menu.getMenuItems()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public String getMenuAsString() {
        return menu.toString();
    }
}