package menu;

import java.io.Serializable;
import java.util.*;

public class Menu implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final int infoLength = 76; //Base length of info string
    private Map<Dish.Type, List<Dish>> menuMap;
    List<Dish> dishByNumber;

    public Menu() {
        menuMap = new TreeMap<>();
        dishByNumber = new ArrayList<>();

        for (Dish.Type type : Dish.Type.values()) {
            menuMap.put(type, new ArrayList<>());
        }
    }

    public void addDish(Dish dish) {
        if (menuMap.get(dish.getType()) != null) {
            menuMap.get(dish.getType()).add(dish);
        } else {
            return;
        }

        dishByNumber = new ArrayList<>();
        for (Map.Entry<Dish.Type, List<Dish>> entry : menuMap.entrySet()) {
            dishByNumber.addAll(entry.getValue());
        }
    }

    public Dish getDishByNumberInMenu(int numberInMenu) {
        return dishByNumber.get(numberInMenu - 1);
    }

    public List<Dish> getDishesInMenu() {
        return new ArrayList<>(dishByNumber);
    }

    @Override
    public String toString() {
        StringBuilder menuString = new StringBuilder();

        menuString.append("-".repeat((infoLength - 4) / 2)).append("MENU").append("-".repeat((infoLength - 4) / 2)).append("\n");

        int counter = 1;
        for (Map.Entry<Dish.Type, List<Dish>> entry : menuMap.entrySet()) {
            Dish.Type type = entry.getKey();
            List<Dish> dishes = entry.getValue();

            if (!dishes.isEmpty()) {
                int padding = (infoLength - type.toString().length()) / 2;

                menuString.append("-".repeat(padding)).append(type).append("-".repeat(padding)).append("\n")
                        .append("№   Name                  Price    Description           \n\n");

                for (Dish dish : dishes) {
                    menuString.append(String.format("%-4s", counter++)).append(dish.toString()).append("\n");
                }
            }
        }

        menuString.append("-".repeat(infoLength)).append("\n");
        return menuString.toString();
    }
}