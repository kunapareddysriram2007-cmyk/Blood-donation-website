import java.util.Random;
import java.util.Scanner;

public class index {
    public static final Scanner sc = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        register.loadDonorsFromFile();

        System.out.println("==================================================");
        System.out.println("        BLOOD DONATION NETWORK - BACKEND");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register New Donor");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> login();
                case 2 -> register.registerDonor(sc);
                case 3 -> {
                    System.out.println("Thank you.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void login() {
        System.out.print("Enter registered phone number: ");
        String phone = sc.nextLine().trim();

        register.Donor donor = register.getDonorByPhone(phone);
        if (donor == null) {
            System.out.println("No donor found with this phone number. Please register first.");
            return;
        }

        String otp = generateOTP();
        System.out.println("OTP sent successfully: " + otp);
        System.out.print("Enter OTP: ");
        String entered = sc.nextLine().trim();

        if (otp.equals(entered)) {
            System.out.println("Login successful. Welcome, " + donor.name + "!");
            home.showHome(sc, donor);
        } else {
            System.out.println("Invalid OTP. Login failed.");
        }
    }

    public static String generateOTP() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid integer: ");
            }
        }
    }

    public static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}