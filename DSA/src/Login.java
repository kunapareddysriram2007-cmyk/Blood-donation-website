import java.util.Scanner;

public class Login {

    private Register reg;
    private Register.Donor loggedInDonor;

    public Login(Register reg) {
        this.reg = reg;
        this.loggedInDonor = null;
    }

    public void loginUser(Scanner sc) {
        System.out.println("\n==== LOGIN ====");

        System.out.print("Enter phone number: ");
        String ph = sc.nextLine().trim();

        Register.Donor d = reg.getByPhone(ph);

        if (d == null) {
            System.out.println("User not found. Please register first.");
            return;
        }

        System.out.print("Enter password: ");
        String pw = sc.nextLine().trim();

        if (d.password.equals(pw)) {
            loggedInDonor = d;

            System.out.println("\nLogin successful.");
            System.out.println("Welcome, " + d.name + "!");
            System.out.println("Blood Group : " + d.blood);
            System.out.println("Phone       : " + d.phone);
            System.out.println("Location    : " + d.city + ", " + d.area);
        } else {
            System.out.println("Invalid password.");
        }
    }

    public void logoutUser() {
        if (loggedInDonor == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        System.out.println("Logged out successfully: " + loggedInDonor.name);
        loggedInDonor = null;
    }

    public boolean isLoggedIn() {
        return loggedInDonor != null;
    }

    public Register.Donor getLoggedInDonor() {
        return loggedInDonor;
    }

    public void showCurrentUser() {
        if (loggedInDonor == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        System.out.println("\n==== CURRENT LOGGED IN USER ====");
        System.out.println("Name        : " + loggedInDonor.name);
        System.out.println("Blood Group : " + loggedInDonor.blood);
        System.out.println("Phone       : " + loggedInDonor.phone);
        System.out.println("Location    : " + loggedInDonor.city + ", " + loggedInDonor.area);
    }
}