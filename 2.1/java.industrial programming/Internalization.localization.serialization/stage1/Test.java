import Animal.*;

public class Test {
    public static void main(String[] args) {
        Dog dog = new Dog("Bobik", 5, Dog.Breed.LABRADOR);
        Cat cat = new Cat("Archie", 3, Cat.Color.GRAY);
        Parrot parrot = new Parrot("Albert", 2, Parrot.Species.MACAW);

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
    }
}