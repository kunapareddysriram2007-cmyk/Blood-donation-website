import java.util.*;

public class Requests {

    private Register reg;

    static class Req {
        String requesterName;
        String requesterCity;
        String requesterPhone;
        String donorPhone;
        long time;
        boolean emergency;
        String status;

        Req(String requesterName, String requesterCity, String requesterPhone, String donorPhone, boolean emergency) {
            this.requesterName = requesterName;
            this.requesterCity = requesterCity;
            this.requesterPhone = requesterPhone;
            this.donorPhone = donorPhone;
            this.emergency = emergency;
            this.time = System.currentTimeMillis();
            this.status = "PENDING";
        }
    }

    // normal requests
    private Queue<Req> q;

    // emergency requests
    private PriorityQueue<Req> pq;

    // donorPhone -> list of all requests received by that donor
    private HashMap<String, ArrayList<Req>> mp;

    // requesterPhone -> list of all requests sent by that requester
    private HashMap<String, ArrayList<Req>> sentMap;

    public Requests(Register reg) {
        this.reg = reg;
        q = new LinkedList<>();

        pq = new PriorityQueue<>(new Comparator<Req>() {
            public int compare(Req a, Req b) {
                return Long.compare(a.time, b.time);
            }
        });

        mp = new HashMap<>();
        sentMap = new HashMap<>();
    }

    public void sendRequest(Scanner sc, Register.Donor currentUser) {
        System.out.println("\n=== SEND BLOOD REQUEST ===");

        // Step 1: Select blood group (or exit)
        String bg;
        while (true) {
            System.out.print("Enter required blood group (or type 'exit' to cancel): ");
            bg = sc.nextLine().trim();
            if (bg.equalsIgnoreCase("exit")) {
                System.out.println("Cancelling request.");
                return;
            }
            if (!bg.isEmpty()) break;
            System.out.println("Blood group cannot be empty.");
        }

        // Step 2: Get requestor location (lat/lng) for distance sorting
        double ulat;
        while (true) {
            System.out.print("Enter your latitude (or type 'exit' to cancel): ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Cancelling request.");
                return;
            }
            try {
                ulat = Double.parseDouble(line);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid latitude. Please enter a valid number.");
            }
        }

        double ulng;
        while (true) {
            System.out.print("Enter your longitude (or type 'exit' to cancel): ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Cancelling request.");
                return;
            }
            try {
                ulng = Double.parseDouble(line);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid longitude. Please enter a valid number.");
            }
        }

        // Step 3: Show sorted list of matching donors
        SearchDonor sd = new SearchDonor(reg);
        ArrayList<SearchDonor.Match> matches = sd.getSortedMatches(bg, ulat, ulng);
        if (currentUser != null) {
            matches.removeIf(m -> m.d.phone.equals(currentUser.phone));
        }

        if (matches.isEmpty()) {
            System.out.println("No donors found for blood group " + bg + ".");
            return;
        }

        System.out.println("\nMatching donors (nearest first):");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < matches.size(); i++) {
            Register.Donor d = matches.get(i).d;
            double dist = matches.get(i).dist;
            System.out.printf("%d. %s | %s | %s, %s | %.2f km | Phone: %s%n",
                    i + 1, d.name, d.blood, d.city, d.area, dist, d.phone);
        }

        int selectedIndex;
        while (true) {
            System.out.print("Enter donor number to send request (0 to cancel): ");
            String line = sc.nextLine().trim();
            try {
                selectedIndex = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (selectedIndex == 0) {
                System.out.println("Cancelling request.");
                return;
            }

            if (selectedIndex < 1 || selectedIndex > matches.size()) {
                System.out.println("Invalid donor number. Please try again.");
                continue;
            }
            break;
        }

        Register.Donor selectedDonor = matches.get(selectedIndex - 1).d;

        // Step 4: Emergency flag
        boolean em;
        while (true) {
            System.out.print("Emergency request? (yes/no, or type 'exit' to cancel): ");
            String x = sc.nextLine().trim().toLowerCase();
            if (x.equals("exit")) {
                System.out.println("Cancelling request.");
                return;
            }
            if (x.equals("yes") || x.equals("y")) {
                em = true;
                break;
            }
            if (x.equals("no") || x.equals("n")) {
                em = false;
                break;
            }
            System.out.println("Please answer 'yes' or 'no'.");
        }

        String rn;
        String rc;
        String rp = "";
        if (currentUser != null) {
            rn = currentUser.name;
            rc = currentUser.city;
            rp = currentUser.phone;
        } else {
            System.out.print("Enter requester name: ");
            rn = sc.nextLine().trim();
            System.out.print("Enter requester city: ");
            rc = sc.nextLine().trim();
        }

        Req r = new Req(rn, rc, rp, selectedDonor.phone, em);

        if (em) {
            pq.offer(r);
        } else {
            q.offer(r);
        }

        mp.putIfAbsent(selectedDonor.phone, new ArrayList<Req>());
        mp.get(selectedDonor.phone).add(r);

        if (!rp.isEmpty()) {
            sentMap.putIfAbsent(rp, new ArrayList<Req>());
            sentMap.get(rp).add(r);
        }

        System.out.println("Request sent successfully to donor " + selectedDonor.name + ".");

        // Show sender dashboard for this donor
        if (!rp.isEmpty()) {
            showSentRequestsForDonor(rp, selectedDonor.phone);
        }
    }

    private void showSentRequestsForDonor(String requesterPhone, String donorPhone) {
        ArrayList<Req> sent = sentMap.get(requesterPhone);
        if (sent == null || sent.isEmpty()) return;

        System.out.println("\n=== REQUESTS SENT TO " + donorPhone + " ===");
        System.out.println("(Most recent first)");

        // Show only requests sent to this donor
        sent.stream()
                .filter(r -> r.donorPhone.equals(donorPhone))
                .sorted((a, b) -> Long.compare(b.time, a.time))
                .forEach(r -> {
                    System.out.println("- " + r.requesterName + " (" + r.requesterCity + ") " +
                            "| Emergency: " + (r.emergency ? "YES" : "NO") +
                            " | Status: " + r.status +
                            " | Sent: " + new Date(r.time));
                });
        System.out.println();
    }

    public void viewRequestsDashboard(Scanner sc, Register.Donor currentDonor) {
        if (currentDonor == null) {
            System.out.println("No donor is currently logged in.");
            return;
        }

        String ph = currentDonor.phone;
        Register.Donor d = currentDonor;

        ArrayList<Req> ls = mp.get(ph);
        if (ls == null || ls.isEmpty()) {
            System.out.println("No requests for this donor.");
            return;
        }

        // Auto-deny emergency requests older than 15 minutes if donor hasn't processed them
        applyEmergencyTimeouts();

        while (true) {
            System.out.println("\n=== REQUESTS FOR " + d.name + " ===");

            // Sort requests in chronological order (oldest first)
            ls.sort(Comparator.comparingLong(r -> r.time));

            boolean hasAny = false;
            for (int i = 0; i < ls.size(); i++) {
                Req r = ls.get(i);
                System.out.println((i + 1) + ". " +
                        "Requester: " + r.requesterName +
                        " | City: " + r.requesterCity +
                        " | Emergency: " + (r.emergency ? "YES" : "NO") +
                        " | Status: " + r.status +
                        " | Sent: " + new Date(r.time));
                hasAny = true;
            }

            if (!hasAny) {
                System.out.println("No requests for this donor.");
                return;
            }

            System.out.println("\nOptions:");
            System.out.println("1. Accept a request");
            System.out.println("2. Reject a request");
            System.out.println("3. Exit to main menu");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (choice == 3) {
                System.out.println("Returning to main menu...");
                return;
            }

            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice.");
                continue;
            }

            System.out.print("Enter request number: ");
            int k;
            try {
                k = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (k < 1 || k > ls.size()) {
                System.out.println("Invalid request number.");
                continue;
            }

            Req r = ls.get(k - 1);

            if (!r.status.equals("PENDING")) {
                System.out.println("This request is already processed.");
                continue;
            }

            if (choice == 1) {
                r.status = "ACCEPTED";
                removeFromStructures(r);
                System.out.println("Request accepted.");
            } else {
                r.status = "REJECTED";
                removeFromStructures(r);
                System.out.println("Request rejected.");
            }
        }
    }

    private void applyEmergencyTimeouts() {
        long now = System.currentTimeMillis();
        long threshold = 15 * 60 * 1000; // 15 minutes

        for (ArrayList<Req> list : mp.values()) {
            for (Req r : list) {
                if (r.status.equals("PENDING") && r.emergency && (now - r.time) > threshold) {
                    r.status = "DENIED (TIMEOUT)";
                    removeFromStructures(r);
                }
            }
        }
    }

    public void viewSentRequestsDashboard(Scanner sc, Register.Donor currentUser) {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        applyEmergencyTimeouts();

        ArrayList<Req> sent = sentMap.get(currentUser.phone);
        if (sent == null || sent.isEmpty()) {
            System.out.println("No sent requests found.");
            return;
        }

        System.out.println("\n=== REQUESTS YOU SENT ===");
        System.out.println("(Most recent first)");
        sent.stream()
                .sorted((a, b) -> Long.compare(b.time, a.time))
                .forEach(r -> {
                    Register.Donor d = reg.getByPhone(r.donorPhone);
                    String donorName = (d != null) ? d.name : r.donorPhone;
                    System.out.println("- To: " + donorName + " (" + r.donorPhone + ") " +
                            "| Emergency: " + (r.emergency ? "YES" : "NO") +
                            " | Status: " + r.status +
                            " | Sent: " + new Date(r.time));
                });
    }

    public void processNextGlobalRequest() {
        Req r = null;

        while (!pq.isEmpty()) {
            Req t = pq.poll();
            if (t.status.equals("PENDING")) {
                r = t;
                break;
            }
        }

        if (r == null) {
            while (!q.isEmpty()) {
                Req t = q.poll();
                if (t.status.equals("PENDING")) {
                    r = t;
                    break;
                }
            }
        }

        if (r == null) {
            System.out.println("No pending requests.");
            return;
        }

        System.out.println("Next request:");
        System.out.println("Requester: " + r.requesterName +
                ", City: " + r.requesterCity +
                ", Emergency: " + r.emergency +
                ", Status: " + r.status);
    }

    private void removeFromStructures(Req r) {
        if (r.emergency) {
            pq.remove(r);
        } else {
            q.remove(r);
        }
    }
}