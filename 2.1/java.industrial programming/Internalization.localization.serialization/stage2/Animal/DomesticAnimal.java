package Animal;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

public abstract class DomesticAnimal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public final Date creationDate = new Date();
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

    public String getCreationDate() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, AppLocale.get());
        return dateFormatter.format(creationDate);
    }

    @Override
    public String toString() {
        return AppLocale.getString(AppLocale.name) + ": " + name + ", " + AppLocale.getString(AppLocale.age) + ": " + age;
    }
}