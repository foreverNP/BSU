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
        System.out.println(AppLocale.getString(AppLocale.parrot_voice));
    }

    @Override
    public String toString() {
        return AppLocale.getString(AppLocale.parrot) + "|" + super.toString() + ", "
                + AppLocale.getString(AppLocale.species) + ": " + AppLocale.getString(species.toString()) + ", "
                + AppLocale.getString(AppLocale.creation) + ": " + getCreationDate();
    }
}
