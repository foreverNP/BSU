package Animal;

public class Parrot extends DomesticAnimal {
    public static enum Species {
        MACAW, COCKATIEL, PARAKEET, OTHER
    }

    private Species species;

    public Parrot(String name, int age, Species species) {
        super(name, age);
        this.species = species;
    }

    public Species getSpecies() {
        return species;
    }

    @Override
    public void makeSound() {
        System.out.println("Hello, I'm a parrot!");
    }

    @Override
    public String toString() {
        return "Parrot| " + super.toString() + ", Species: " + species;
    }
}
