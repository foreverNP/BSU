import Animal.*;

import java.util.*;
import java.io.*;

public class Test {
    static Locale createLocale(String[] args) {
        if (args.length == 2) {
            return new Locale(args[0], args[1]);
        } else if (args.length == 4) {
            return new Locale(args[2], args[3]);
        }
        return null;
    }

    static void setupConsole(String[] args) {
        if (args.length >= 2) {
            if (args[0].equals("-encoding")) {
                try {
                    System.setOut(new PrintStream(System.out, true, args[1]));
                } catch (UnsupportedEncodingException ex) {
                    System.err.println("Unsupported encoding: " + args[1]);
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            setupConsole(args);
            Locale loc = createLocale(args);
            if (loc == null) {
                System.err.println("Invalid argument(s)\n"
                        + "Syntax: [-encoding ENCODING_ID] language country\n"
                        + "Example: -encoding Cp855 be BY");
                System.exit(1);


            }
            AppLocale.set(loc);

            Dog dog = new Dog(AppLocale.getString(AppLocale.dog_default_name), 5, Dog.Breed.LABRADOR);
            Cat cat = new Cat(AppLocale.getString(AppLocale.cat_default_name), 3, Cat.Color.GRAY);
            Parrot parrot = new Parrot(AppLocale.getString(AppLocale.parrot_default_name), 2, Parrot.Species.MACAW);

            DomesticAnimalConnector.saveAnimal(dog, "dog.dat");
            DomesticAnimalConnector.saveAnimal(cat, "cat.dat");
            DomesticAnimalConnector.saveAnimal(parrot, "parrot.dat");

            DomesticAnimal loadedDog = DomesticAnimalConnector.loadAnimal("dog.dat");
            DomesticAnimal loadedCat = DomesticAnimalConnector.loadAnimal("cat.dat");
            DomesticAnimal loadedParrot = DomesticAnimalConnector.loadAnimal("parrot.dat");

            System.out.println(loadedDog);
            loadedDog.makeSound();

            System.out.println(loadedCat);
            loadedCat.makeSound();

            System.out.println(loadedParrot);
            loadedParrot.makeSound();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}