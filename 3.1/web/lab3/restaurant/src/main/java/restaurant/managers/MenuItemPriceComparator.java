package restaurant.managers;

import restaurant.models.MenuItem;
import java.util.Comparator;

class MenuItemPriceComparator implements Comparator<MenuItem> {
    @Override
    public int compare(MenuItem item1, MenuItem item2) {
        return item1.getPrice().compareTo(item2.getPrice());
    }
}