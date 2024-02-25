package Animal;

import java.io.*;

public abstract class DomesticAnimal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;

    public DomesticAnimal(String name, int age) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0 || age > 20) {
            throw new IllegalArgumentException("Invalid age");
        }

        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public abstract void makeSound();

    @Override
    public String toString() {
        return "Name: " + name + ", Age: " + age;
    }
}