import java.util.Scanner;

public class home {

    public static void showHome(Scanner sc, register.Donor currentUser) {
        while (true) {
            requests.autoDenyExpiredEmergencyRequests();

            System.out.println("\n==================================================");
            System.out.println("HOME DASHBOARD");
            System.out.println("Logged in as: " + currentUser.name + " (" + currentUser.phone + ")");
            System.out.println("==================================================");
            System.out.println("1. View My Profile");
            System.out.println("2. Search Donors By Blood Group");
            System.out.println("3. View Incoming Requests");
            System.out.println("4. View My Sent Requests");
            System.out.println("5. Contact Us");
            System.out.println("6. View All Registered Donors");
            System.out.println("7. Search Donor By Phone");
            System.out.println("8. Reverse Donor Linked List");
            System.out.println("9. Delete My Account");
            System.out.println("10. Logout");
            System.out.print("Enter choice: ");

            int choice = index.readInt();

            switch (choice) {
                case 1 -> displayProfile(currentUser);
                case 2 -> search.searchDonors(sc, currentUser);
                case 3 -> requests.viewAndManageIncomingRequests(sc, currentUser.phone);
                case 4 -> requests.viewSentRequests(currentUser.phone);
                case 5 -> contact.contactMenu(sc, currentUser);
                case 6 -> register.displayAllDonors();
                case 7 -> search.searchDonorByPhoneMenu(sc);
                case 8 -> {
                    register.reverseDonorList();
                    System.out.println("Donor linked list reversed successfully.");
                }
                case 9 -> {
                    boolean deleted = register.deleteDonorByPhone(currentUser.phone);
                    if (deleted) {
                        requests.removeAllRequestsOfUser(currentUser.phone);
                        System.out.println("Your account has been deleted.");
                        return;
                    } else {
                        System.out.println("Account deletion failed.");
                    }
                }
                case 10 -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void displayProfile(register.Donor donor) {
        System.out.println("\n--------------- MY PROFILE ---------------");
        System.out.println("Name       : " + donor.name);
        System.out.println("Age        : " + donor.age);
        System.out.println("Blood Group: " + donor.bloodGroup);
        System.out.println("Phone      : " + donor.phone);
        System.out.println("City       : " + donor.city);
        System.out.println("Area       : " + donor.area);
        System.out.println("Latitude   : " + donor.lat);
        System.out.println("Longitude  : " + donor.lng);
        System.out.println("------------------------------------------");
    }
}