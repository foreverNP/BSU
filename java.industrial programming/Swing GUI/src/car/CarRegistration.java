package car;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class CarRegistration implements Serializable {
    private static final long serialVersionUID = 123L;

    public enum Brand {
        TOYOTA, HONDA, BMW, AUDI, MERCEDES
    }

    public enum Color {
        RED, BLUE, GREEN, BLACK, WHITE
    }

    private Brand brand;
    private Color color;
    private String model;
    private int year;
    private double price;
    private String registrationNumber;

    public CarRegistration(Brand brand, Color color, String model, int year, double price, String registrationNumber) {
        this.brand = brand;
        this.color = color;
        this.model = model;

        if (!validateYear(year)) {
            throw new IllegalArgumentException("Invalid year of manufacture");
        }
        this.year = year;

        if (!validatePrice(price)) {
            throw new IllegalArgumentException("Invalid price");
        }
        this.price = price;

        if (!validateRegistrationNumber(registrationNumber)) {
            throw new IllegalArgumentException("Invalid registration number");
        }
        this.registrationNumber = registrationNumber;
    }

    public CarRegistration() {
    }

    private boolean validateYear(int year) {
        return !(year < 1886 || year > (new GregorianCalendar()).get(Calendar.YEAR));
    }

    private boolean validatePrice(double price) {
        return price > 0;
    }

    private boolean validateRegistrationNumber(String registrationNumber) {
        return registrationNumber.matches("\\d{4}[A-Z]{2}");
    }

    public String getBrand() {
        return brand.toString();
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color.toString();
    }

    public double getPrice() {
        return price;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public String toString() {
        return brand + "\n" +
                model + "\n" +
                year + "\n" +
                color + "\n" +
                price + "\n" +
                registrationNumber;
    }

    public static CarRegistration parseFromString(String input) {
        String[] lines = input.split("\n");

        Brand brand = Brand.valueOf(lines[0].split(": ")[1]);
        String model = lines[1].split(": ")[1];
        int year = Integer.parseInt(lines[2].split(": ")[1]);
        Color color = Color.valueOf(lines[3].split(": ")[1]);
        double price = Double.parseDouble(lines[4].split(": ")[1]);
        String registrationNumber = lines[5].split(": ")[1];

        return new CarRegistration(brand, color, model, year, price, registrationNumber);
    }

    public static boolean nextRead(Scanner fin, PrintStream out) {
        return nextRead("Brand", fin, out);
    }

    public static boolean nextRead(final String prompt, Scanner fin, PrintStream out) {
        out.print(prompt);
        out.print(": ");
        return fin.hasNextLine();
    }

    public static CarRegistration read(Scanner fin, PrintStream out) throws IOException, NumberFormatException {
        String str;
        CarRegistration carRegistration = new CarRegistration();

        str = fin.nextLine();
        carRegistration.brand = str.split(": ").length > 1 ? Brand.valueOf(str.split(": ")[1])
                : Brand.valueOf(str);

        if (!nextRead("Model", fin, out)) {
            return null;
        }
        str = fin.nextLine();
        carRegistration.model = str.split(": ").length > 1 ? str.split(": ")[1]
                : str;


        if (!nextRead("Year of manufacture", fin, out)) {
            return null;
        }
        str = fin.nextLine();
        carRegistration.year = str.split(": ").length > 1 ? Integer.parseInt(str.split(": ")[1])
                : Integer.parseInt(str);
        if (!carRegistration.validateYear(carRegistration.year)) {
            return null;
        }


        if (!nextRead("Color", fin, out)) {
            return null;
        }
        str = fin.nextLine();
        carRegistration.color = str.split(": ").length > 1 ? Color.valueOf(str.split(": ")[1])
                : Color.valueOf(str);

        if (!nextRead("Price", fin, out)) {
            return null;
        }
        str = fin.nextLine();
        carRegistration.price = str.split(": ").length > 1 ? Double.parseDouble(str.split(": ")[1])
                : Double.parseDouble(str);
        if (!carRegistration.validatePrice(carRegistration.price)) {
            return null;
        }

        if (!nextRead("Registration number", fin, out)) {
            return null;
        }
        str = fin.nextLine();
        carRegistration.registrationNumber = str.split(": ").length > 1 ? str.split(": ")[1]
                : str;
        if (!carRegistration.validateRegistrationNumber(carRegistration.registrationNumber)) {
            return null;
        }

        return carRegistration;
    }
}
