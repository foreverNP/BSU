package Animal;

public class Dog extends DomesticAnimal {
    public static enum  Breed {
        SHEPHERD, LABRADOR, POODLE, OTHER
    }

    private Breed breed;

    public Dog(String name, int age, Breed breed) {
        super(name, age);
        this.breed = breed;
    }

    public Breed getBreed() {
        return breed;
    }

    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }

    @Override
    public String toString() {
        return "Dog|    " + super.toString() + ", Breed: " + breed;
    }
}