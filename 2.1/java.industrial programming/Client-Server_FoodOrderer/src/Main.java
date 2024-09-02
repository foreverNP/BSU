import menu.*;
import order.*;

public class Main {
    public static void main(String[] args) {
        try {
            Menu menu = new Menu();

            Dish pizza = new Dish(10.99, "Pizza", "Delicious pizza with cheese and tomatoes", Dish.Type.MAIN_COURSE);
            Dish burger = new Dish(8.5, "Burger", "Juicy beef burger with lettuce and tomato", Dish.Type.MAIN_COURSE);
            Dish cake = new Dish(5.99, "Cake", "Chocolate cake with frosting", Dish.Type.DESSERT);
            Dish iceCream = new Dish(3.5, "Ice Cream", "Vanilla ice cream with chocolate syrup", Dish.Type.DESSERT);
            Dish cola = new Dish(2.5, "Cola", "Carbonated beverage", Dish.Type.DRINK);
            Dish tea = new Dish(1.99, "Tea", "Hot tea with lemon", Dish.Type.DRINK);

            menu.addDish(pizza);
            menu.addDish(burger);
            menu.addDish(cake);
            menu.addDish(iceCream);
            menu.addDish(cola);
            menu.addDish(tea);

            System.out.println(menu.toString());


            Order order = new Order("123 Main Street");
            order.addDish(pizza, 2);
            order.addDish(cola, 3);

            System.out.println(menu.getDishByNumberInMenu(1));
            System.out.println(menu.getDishByNumberInMenu(2));
            System.out.println(menu.getDishByNumberInMenu(3));
            System.out.println(menu.getDishByNumberInMenu(4));
            System.out.println(menu.getDishByNumberInMenu(5));
            System.out.println(menu.getDishByNumberInMenu(6));

            System.out.println(order.toString());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
