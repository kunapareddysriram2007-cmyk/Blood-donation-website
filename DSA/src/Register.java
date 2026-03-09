import java.io.*;
import java.util.*;

public class Register {

    static class Donor {
        String name;
        int age;
        String blood;
        String phone;
        String password;
        String city;
        String area;
        double lat;
        double lng;

        Donor(String name, int age, String blood, String phone, String password, String city, String area, double lat, double lng) {
            this.name = name;
            this.age = age;
            this.blood = blood;
            this.phone = phone;
            this.password = password;
            this.city = city;
            this.area = area;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String toString() {
            return name + " | " + blood + " | " + city + ", " + area + " | " + phone;
        }
    }

    static class Node {
        Donor d;
        Node next;

        Node(Donor d) {
            this.d = d;
        }
    }

    // singly linked list head
    private Node head;

    // hashing - separate chaining
    private LinkedList<Donor>[] tab;
    private int sz;
    private int cnt;

    // Persist donor login/registration data on disk so it survives program restarts.
    // Stored in the current working directory (where the JVM is started).
    private static final String DATA_FILE = System.getProperty("user.dir") + File.separator + "donors.txt";

    @SuppressWarnings("unchecked")
    public Register() {
        sz = 11;
        tab = new LinkedList[sz];
        for (int i = 0; i < sz; i++) {
            tab[i] = new LinkedList<>();
        }
        cnt = 0;
        loadFromFile();
    }

    private void loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length != 9) continue;

                try {
                    String n = parts[0];
                    int a = Integer.parseInt(parts[1]);
                    String b = parts[2];
                    String p = parts[3];
                    String pw = parts[4];
                    String c = parts[5];
                    String ar = parts[6];
                    double la = Double.parseDouble(parts[7]);
                    double ln = Double.parseDouble(parts[8]);

                    Donor d = new Donor(n, a, b, p, pw, c, ar, la, ln);
                    addDonorDirect(d);
                } catch (NumberFormatException ignored) {
                    // skip malformed line
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Unable to load donors file: " + e.getMessage());
        }
    }

    private void saveDonorToFile(Donor d) {
        try (FileWriter fw = new FileWriter(DATA_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.printf("%s|%d|%s|%s|%s|%s|%s|%f|%f%n",
                    d.name,
                    d.age,
                    d.blood,
                    d.phone,
                    d.password,
                    d.city,
                    d.area,
                    d.lat,
                    d.lng);
        } catch (IOException e) {
            System.out.println("Warning: Unable to save donor: " + e.getMessage());
        }
    }

    private int hf(String ph) {
        long v = 0;
        for (int i = 0; i < ph.length(); i++) {
            char c = ph.charAt(i);
            if (c >= '0' && c <= '9') {
                v = (v * 31 + (c - '0')) % sz;
            }
        }
        return (int) v;
    }

    private void rehash() {
        LinkedList<Donor>[] old = tab;
        sz = sz * 2 + 1;

        @SuppressWarnings("unchecked")
        LinkedList<Donor>[] nt = new LinkedList[sz];
        for (int i = 0; i < sz; i++) {
            nt[i] = new LinkedList<>();
        }

        tab = nt;
        cnt = 0;

        for (LinkedList<Donor> b : old) {
            for (Donor d : b) {
                putHash(d);
            }
        }
    }

    private void putHash(Donor d) {
        if ((double) cnt / sz > 0.75) {
            rehash();
        }
        int id = hf(d.phone);
        tab[id].add(d);
        cnt++;
    }

    public boolean existsPhone(String ph) {
        int id = hf(ph);
        for (Donor d : tab[id]) {
            if (d.phone.equals(ph)) {
                return true;
            }
        }
        return false;
    }

    public Donor getByPhone(String ph) {
        int id = hf(ph);
        for (Donor d : tab[id]) {
            if (d.phone.equals(ph)) {
                return d;
            }
        }
        return null;
    }

    public void registerDonor(Scanner sc) {
        System.out.print("Enter name: ");
        String n = sc.nextLine().trim();

        int a;
        while (true) {
            System.out.print("Enter age: ");
            try {
                a = Integer.parseInt(sc.nextLine());
                if (a <= 0) {
                    System.out.println("Age must be a positive number.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid age. Please enter a numeric value.");
            }
        }

        System.out.print("Enter blood group: ");
        String b = sc.nextLine().trim().toUpperCase();

        String p;
        while (true) {
            System.out.print("Enter phone number: ");
            p = sc.nextLine().trim();
            if (!p.matches("\\d{10}")) {
                System.out.println("Invalid 10-digit phone. Please try again.");
                continue;
            }
            if (existsPhone(p)) {
                System.out.println("This phone is already registered. Please login or use a different number.");
                continue;
            }
            break;
        }

        String pw;
        while (true) {
            System.out.print("Enter password: ");
            pw = sc.nextLine().trim();
            if (pw.isEmpty()) {
                System.out.println("Password cannot be empty. Please enter a password.");
                continue;
            }
            break;
        }

        System.out.print("Enter city: ");
        String c = sc.nextLine().trim();

        System.out.print("Enter area: ");
        String ar = sc.nextLine().trim();

        double la;
        while (true) {
            System.out.print("Enter latitude: ");
            try {
                la = Double.parseDouble(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid latitude. Please enter a valid number.");
            }
        }

        double ln;
        while (true) {
            System.out.print("Enter longitude: ");
            try {
                ln = Double.parseDouble(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid longitude. Please enter a valid number.");
            }
        }

        Donor d = new Donor(n, a, b, p, pw, c, ar, la, ln);
        addDonorDirect(d);
        saveDonorToFile(d);

        System.out.println("Donor registered successfully.");
    }

    public void addDonorDirect(Donor d) {
        if (existsPhone(d.phone)) return;

        Node nn = new Node(d);
        if (head == null) {
            head = nn;
        } else {
            Node t = head;
            while (t.next != null) {
                t = t.next;
            }
            t.next = nn;
        }

        putHash(d);
    }

    public Node getHead() {
        return head;
    }

    public void displayAll() {
        if (head == null) {
            System.out.println("No donors found.");
            return;
        }

        Node t = head;
        while (t != null) {
            System.out.println(t.d);
            t = t.next;
        }
    }
}