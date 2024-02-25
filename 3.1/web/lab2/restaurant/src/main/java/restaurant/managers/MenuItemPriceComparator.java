package restaurant.managers;

import restaurant.models.Menu;
import java.util.Comparator;

class MenuItemPriceComparator implements Comparator<Menu.MenuItem> {
    @Override
    public int compare(Menu.MenuItem item1, Menu.MenuItem item2) {
        return item1.getPrice().compareTo(item2.getPrice());
    }
}