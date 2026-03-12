import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class register {

    public static class Donor {
        String name;
        int age;
        String bloodGroup;
        String phone;
        String city;
        String area;
        double lat;
        double lng;

        Donor next;
        Donor prev;

        Donor(String name, int age, String bloodGroup, String phone, String city, String area, double lat, double lng) {
            this.name = name;
            this.age = age;
            this.bloodGroup = bloodGroup;
            this.phone = phone;
            this.city = city;
            this.area = area;
            this.lat = lat;
            this.lng = lng;
            this.next = null;
            this.prev = null;
        }
    }

    static class HashNode {
        String key;
        Donor value;
        HashNode next;

        HashNode(String key, Donor value) {
            this.key = key;
            this.value = value;
        }
    }

    static class DonorHashTable {
        private final HashNode[] table;

        DonorHashTable(int size) {
            table = new HashNode[size];
        }

        private int hash(String key) {
            int hashValue = 0;
            for (int i = 0; i < key.length(); i++) {
                hashValue = (hashValue * 31 + key.charAt(i)) % table.length;
            }
            return hashValue;
        }

        void insert(String key, Donor value) {
            int index = hash(key);
            HashNode head = table[index];
            HashNode temp = head;
            while (temp != null) {
                if (temp.key.equals(key)) {
                    temp.value = value;
                    return;
                }
                temp = temp.next;
            }
            HashNode node = new HashNode(key, value);
            node.next = head;
            table[index] = node;
        }

        Donor search(String key) {
            int index = hash(key);
            HashNode temp = table[index];
            while (temp != null) {
                if (temp.key.equals(key)) {
                    return temp.value;
                }
                temp = temp.next;
            }
            return null;
        }

        boolean delete(String key) {
            int index = hash(key);
            HashNode temp = table[index];
            HashNode prev = null;

            while (temp != null) {
                if (temp.key.equals(key)) {
                    if (prev == null) {
                        table[index] = temp.next;
                    } else {
                        prev.next = temp.next;
                    }
                    return true;
                }
                prev = temp;
                temp = temp.next;
            }
            return false;
        }

        void clear() {
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
        }
    }

    private static Donor head = null;
    private static Donor tail = null;
    private static final DonorHashTable donorTable = new DonorHashTable(101);
    private static final String FILE_NAME = "donors.txt";

    public static void loadDonorsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        head = null;
        tail = null;
        donorTable.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 8) {
                    continue;
                }

                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                String bloodGroup = parts[2].trim();
                String phone = parts[3].trim();
                String city = parts[4].trim();
                String area = parts[5].trim();
                double lat = Double.parseDouble(parts[6].trim());
                double lng = Double.parseDouble(parts[7].trim());

                if (donorTable.search(phone) == null) {
                    Donor donor = new Donor(name, age, bloodGroup, phone, city, area, lat, lng);
                    insertAtEnd(donor);
                    donorTable.insert(phone, donor);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading donor data from file.");
        }
    }

    public static void saveAllDonorsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            Donor temp = head;
            while (temp != null) {
                pw.println(temp.name + "," + temp.age + "," + temp.bloodGroup + "," + temp.phone + "," +
                        temp.city + "," + temp.area + "," + temp.lat + "," + temp.lng);
                temp = temp.next;
            }
        } catch (Exception e) {
            System.out.println("Error saving donor data to file.");
        }
    }

    public static void registerDonor(Scanner sc) {
        System.out.println("\n--------------- DONOR REGISTRATION ---------------");
        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter age: ");
        int age = index.readInt();
        if (age < 18 || age > 65) {
            System.out.println("Age must be between 18 and 65.");
            return;
        }

        System.out.print("Enter blood group (O+, O-, A+, A-, B+, B-, AB+, AB-): ");
        String bloodGroup = sc.nextLine().trim().toUpperCase();
        if (!isValidBloodGroup(bloodGroup)) {
            System.out.println("Invalid blood group.");
            return;
        }

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine().trim();
        if (phone.length() != 10 || !isDigits(phone)) {
            System.out.println("Phone number must be exactly 10 digits.");
            return;
        }

        if (donorTable.search(phone) != null) {
            System.out.println("Donor with this phone number already exists.");
            return;
        }

        System.out.print("Enter city: ");
        String city = sc.nextLine().trim();

        System.out.print("Enter area/locality: ");
        String area = sc.nextLine().trim();

        System.out.print("Enter latitude: ");
        double lat = index.readDouble();

        System.out.print("Enter longitude: ");
        double lng = index.readDouble();

        Donor donor = new Donor(name, age, bloodGroup, phone, city, area, lat, lng);
        insertAtEnd(donor);
        donorTable.insert(phone, donor);
        saveAllDonorsToFile();

        System.out.println("Donor registered successfully.");
    }

    public static void insertAtEnd(Donor donor) {
        if (head == null) {
            head = tail = donor;
        } else {
            tail.next = donor;
            donor.prev = tail;
            tail = donor;
        }
    }

    public static boolean deleteDonorByPhone(String phone) {
        Donor node = donorTable.search(phone);
        if (node == null) {
            return false;
        }

        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = head.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (node == tail) {
            tail = tail.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        donorTable.delete(phone);
        saveAllDonorsToFile();
        return true;
    }

    public static Donor getDonorByPhone(String phone) {
        return donorTable.search(phone);
    }

    public static Donor linearSearchByPhone(String phone) {
        Donor temp = head;
        while (temp != null) {
            if (temp.phone.equals(phone)) {
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    public static Donor binarySearchByPhone(String phone) {
        Donor[] arr = getDonorArray();
        if (arr.length == 0) {
            return null;
        }
        mergeSortByPhone(arr, 0, arr.length - 1);

        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = arr[mid].phone.compareTo(phone);
            if (cmp == 0) {
                return arr[mid];
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    private static void mergeSortByPhone(Donor[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = (left + right) / 2;
        mergeSortByPhone(arr, left, mid);
        mergeSortByPhone(arr, mid + 1, right);
        mergeByPhone(arr, left, mid, right);
    }

    private static void mergeByPhone(Donor[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Donor[] L = new Donor[n1];
        Donor[] R = new Donor[n2];

        for (int i = 0; i < n1; i++) {
            L[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            R[j] = arr[mid + 1 + j];
        }

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].phone.compareTo(R[j].phone) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    public static Donor[] getDonorArray() {
        ArrayList<Donor> list = new ArrayList<>();
        Donor temp = head;
        while (temp != null) {
            list.add(temp);
            temp = temp.next;
        }

        Donor[] arr = new Donor[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static void displayAllDonors() {
        if (head == null) {
            System.out.println("No donors registered.");
            return;
        }

        System.out.println("\n---------------- REGISTERED DONORS ----------------");
        Donor temp = head;
        int c = 1;
        while (temp != null) {
            System.out.println("Donor " + c++);
            System.out.println("Name       : " + temp.name);
            System.out.println("Age        : " + temp.age);
            System.out.println("Blood Group: " + temp.bloodGroup);
            System.out.println("Phone      : " + temp.phone);
            System.out.println("City       : " + temp.city);
            System.out.println("Area       : " + temp.area);
            System.out.println("Latitude   : " + temp.lat);
            System.out.println("Longitude  : " + temp.lng);
            System.out.println("----------------------------------------------");
            temp = temp.next;
        }
    }

    public static void reverseDonorList() {
        Donor current = head;
        Donor temp = null;

        while (current != null) {
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;
            current = current.prev;
        }

        if (temp != null) {
            tail = head;
            head = temp.prev;
        }
    }

    private static boolean isValidBloodGroup(String bg) {
        return bg.equals("O+") || bg.equals("O-") || bg.equals("A+") || bg.equals("A-") ||
               bg.equals("B+") || bg.equals("B-") || bg.equals("AB+") || bg.equals("AB-");
    }

    private static boolean isDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}