package Animal;

public class Cat extends DomesticAnimal {
    public static enum Color {
        GRAY, BLACK, WHITE, OTHER
    }

    private Color color;

    public Cat(String name, int age, Color color) {
        super(name, age);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }

    @Override
    public String toString() {
        return "Cat|    " + super.toString() + ", Color: " + color;
    }
}