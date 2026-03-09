import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Register reg = new Register();
        SearchDonor srh = new SearchDonor(reg);
        Requests req = new Requests(reg);
        Login lg = new Login(reg);

        while (true) {
            if (!lg.isLoggedIn()) {
                System.out.println("\n===== BLOOD DONATION SYSTEM =====");
                System.out.println("1. Login");
                System.out.println("2. Register Donor");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");

                int ch;
                try {
                    ch = Integer.parseInt(sc.nextLine());
                } catch (Exception e) {
                    System.out.println("Invalid input.");
                    continue;
                }

                switch (ch) {
                    case 1:
                        lg.loginUser(sc);
                        break;

                    case 2:
                        reg.registerDonor(sc);
                        System.out.print("\nDo you want to login now? (y/n): ");
                        String ans = sc.nextLine().trim().toLowerCase();
                        if (ans.equals("y") || ans.equals("yes")) {
                            lg.loginUser(sc);
                        }
                        break;

                    case 3:
                        System.out.println("Thank you for using Blood Donation System.");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                continue;
            }

            System.out.println("\n===== BLOOD DONATION SYSTEM =====");
            System.out.println("1. Search Donor");
            System.out.println("2. Send Blood Request");
            System.out.println("3. View Requests Dashboard");
            System.out.println("4. View Sent Requests");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            switch (ch) {
                case 1:
                    srh.searchDonors(sc, lg.getLoggedInDonor());
                    break;

                case 2:
                    req.sendRequest(sc, lg.getLoggedInDonor());
                    break;

                case 3:
                    req.viewRequestsDashboard(sc, lg.getLoggedInDonor());
                    break;

                case 4:
                    req.viewSentRequestsDashboard(sc, lg.getLoggedInDonor());
                    break;

                case 5:
                    lg.logoutUser();
                    break;

                case 6:
                    System.out.println("Thank you for using Blood Donation System.");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

