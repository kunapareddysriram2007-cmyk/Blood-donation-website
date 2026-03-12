import java.util.ArrayList;
import java.util.Scanner;

public class search {

    static class SearchResult {
        register.Donor donor;
        double distance;

        SearchResult(register.Donor donor, double distance) {
            this.donor = donor;
            this.distance = distance;
        }
    }

    public static void searchDonors(Scanner sc, register.Donor currentUser) {
        System.out.print("Enter required blood group: ");
        String bloodGroup = sc.nextLine().trim().toUpperCase();

        ArrayList<SearchResult> resultList = new ArrayList<>();
        register.Donor[] donors = register.getDonorArray();

        for (int i = 0; i < donors.length; i++) {
            register.Donor donor = donors[i];
            if (donor.phone.equals(currentUser.phone)) {
                continue;
            }
            if (donor.bloodGroup.equals(bloodGroup)) {
                double dist = haversine(currentUser.lat, currentUser.lng, donor.lat, donor.lng);
                resultList.add(new SearchResult(donor, dist));
            }
        }

        if (resultList.isEmpty()) {
            System.out.println("No donors found for selected blood group.");
            return;
        }

        SearchResult[] arr = new SearchResult[resultList.size()];
        for (int i = 0; i < resultList.size(); i++) {
            arr[i] = resultList.get(i);
        }

        insertionSortByDistance(arr);
        displayResults(sc, currentUser, arr);
    }

    public static void searchDonorByPhoneMenu(Scanner sc) {
        System.out.print("Enter phone number to search: ");
        String phone = sc.nextLine().trim();

        register.Donor donor = register.binarySearchByPhone(phone);

        if (donor == null) {
            System.out.println("Donor not found.");
        } else {
            System.out.println("\nDonor found:");
            System.out.println("Name       : " + donor.name);
            System.out.println("Age        : " + donor.age);
            System.out.println("Blood Group: " + donor.bloodGroup);
            System.out.println("Phone      : " + donor.phone);
            System.out.println("City       : " + donor.city);
            System.out.println("Area       : " + donor.area);
            System.out.println("Latitude   : " + donor.lat);
            System.out.println("Longitude  : " + donor.lng);
        }
    }

    public static void displayResults(Scanner sc, register.Donor currentUser, SearchResult[] arr) {
        System.out.println("\n---------------- AVAILABLE DONORS ----------------");
        for (int i = 0; i < arr.length; i++) {
            SearchResult sr = arr[i];
            System.out.printf("%d. %s | %s | %s, %s | %.2f km%n",
                    i + 1,
                    sr.donor.name,
                    sr.donor.bloodGroup,
                    sr.donor.city,
                    sr.donor.area,
                    sr.distance);
        }

        System.out.print("Enter donor number to send request (0 to back): ");
        int choice = index.readInt();

        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > arr.length) {
            System.out.println("Invalid donor number.");
            return;
        }

        register.Donor selected = arr[choice - 1].donor;
        System.out.print("Mark as emergency request? (yes/no): ");
        String emergencyInput = sc.nextLine().trim().toLowerCase();
        boolean emergency = emergencyInput.equals("yes") || emergencyInput.equals("y");

        requests.sendRequest(currentUser, selected, emergency);
    }

    private static void insertionSortByDistance(SearchResult[] arr) {
        for (int i = 1; i < arr.length; i++) {
            SearchResult key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].distance > key.distance) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}