package Animal;

import java.io.*;

public class DomesticAnimalConnector {
    public static void saveAnimal(DomesticAnimal animal, String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(animal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DomesticAnimal loadAnimal(String filePath) {
        DomesticAnimal animal = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            animal = (DomesticAnimal) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return animal;
    }
}