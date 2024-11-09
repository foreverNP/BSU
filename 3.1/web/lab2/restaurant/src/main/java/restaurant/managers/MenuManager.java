package restaurant.managers;

import restaurant.models.Menu;
import java.util.List;
import java.util.ArrayList;

public class MenuManager {
    private Menu menu;

    public MenuManager(Menu menu) {
        this.menu = menu;
    }

    public void addMenuItem(Menu.MenuItem item) {
        menu.addMenuItem(item);
    }

    public void sortMenuItemsByPrice() {
        menu.getMenuItems().sort(new MenuItemPriceComparator());
    }

    public List<Menu.MenuItem> searchMenuItemsByName(String name) {
        List<Menu.MenuItem> result = new ArrayList<>();
        for (Menu.MenuItem item : menu.getMenuItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                result.add(item);
            }
        }
        return result;
    }

    public Menu.MenuItem searchMenuItemById(int id) {
        for (Menu.MenuItem item : menu.getMenuItems()) {
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