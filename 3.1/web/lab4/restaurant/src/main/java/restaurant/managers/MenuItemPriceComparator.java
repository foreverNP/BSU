package restaurant.managers;

import java.util.Comparator;

import restaurant.entity.MenuItem;

class MenuItemPriceComparator implements Comparator<MenuItem> {
    @Override
    public int compare(MenuItem item1, MenuItem item2) {
        return item1.getPrice().compareTo(item2.getPrice());
    }
}