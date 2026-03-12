import java.util.ArrayList;
import java.util.Scanner;

public class requests {

    public static class Request {
        String requesterPhone;
        String requesterName;
        String requesterCity;
        String donorPhone;
        String donorName;
        String donorCity;
        boolean emergency;
        String status;
        long requestTime;

        Request(String requesterPhone, String requesterName, String requesterCity,
                String donorPhone, String donorName, String donorCity, boolean emergency) {
            this.requesterPhone = requesterPhone;
            this.requesterName = requesterName;
            this.requesterCity = requesterCity;
            this.donorPhone = donorPhone;
            this.donorName = donorName;
            this.donorCity = donorCity;
            this.emergency = emergency;
            this.status = "PENDING";
            this.requestTime = System.currentTimeMillis();
        }
    }

    static class RequestNode {
        Request data;
        RequestNode next;

        RequestNode(Request data) {
            this.data = data;
        }
    }

    static class LinkedQueue {
        RequestNode front;
        RequestNode rear;

        void enqueue(Request request) {
            RequestNode node = new RequestNode(request);
            if (rear == null) {
                front = rear = node;
            } else {
                rear.next = node;
                rear = node;
            }
        }

        Request dequeue() {
            if (front == null) {
                return null;
            }
            Request result = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            return result;
        }

        boolean isEmpty() {
            return front == null;
        }
    }

    static class CircularQueue {
        String[] arr;
        int front;
        int rear;
        int size;
        int capacity;

        CircularQueue(int capacity) {
            this.capacity = capacity;
            arr = new String[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }

        void enqueue(String value) {
            if (size == capacity) {
                front = (front + 1) % capacity;
                size--;
            }
            rear = (rear + 1) % capacity;
            arr[rear] = value;
            size++;
        }

        void display() {
            if (size == 0) {
                System.out.println("No recent request activity.");
                return;
            }

            System.out.println("\nRecent Request Activity:");
            for (int i = 0; i < size; i++) {
                System.out.println((i + 1) + ". " + arr[(front + i) % capacity]);
            }
        }
    }

    private static final LinkedQueue normalQueue = new LinkedQueue();
    private static final CircularQueue recentActivity = new CircularQueue(10);
    private static final ArrayList<Request> allRequests = new ArrayList<>();

    public static void sendRequest(register.Donor requester, register.Donor donor, boolean emergency) {
        autoDenyExpiredEmergencyRequests();

        if (requester == null || donor == null) {
            System.out.println("Invalid requester or donor.");
            return;
        }

        if (requester.phone.equals(donor.phone)) {
            System.out.println("You cannot send a request to yourself.");
            return;
        }

        for (int i = 0; i < allRequests.size(); i++) {
            Request r = allRequests.get(i);
            if (r.requesterPhone.equals(requester.phone)
                    && r.donorPhone.equals(donor.phone)
                    && r.status.equals("PENDING")) {
                System.out.println("A pending request already exists for this donor.");
                return;
            }
        }

        Request req = new Request(
                requester.phone, requester.name, requester.city,
                donor.phone, donor.name, donor.city, emergency
        );

        allRequests.add(req);

        if (!emergency) {
            normalQueue.enqueue(req);
        }

        recentActivity.enqueue("Request sent from " + requester.name + " to " + donor.name
                + (emergency ? " [EMERGENCY]" : " [NORMAL]"));

        System.out.println("Request sent successfully.");
    }

    public static void autoDenyExpiredEmergencyRequests() {
        long now = System.currentTimeMillis();
        long limit = 15 * 60 * 1000L;

        for (int i = 0; i < allRequests.size(); i++) {
            Request r = allRequests.get(i);
            if (r.emergency && r.status.equals("PENDING") && now - r.requestTime > limit) {
                r.status = "AUTO-DENIED";
            }
        }
    }

    public static void viewAndManageIncomingRequests(Scanner sc, String donorPhone) {
        autoDenyExpiredEmergencyRequests();

        ArrayList<Request> incoming = new ArrayList<>();

        for (int i = 0; i < allRequests.size(); i++) {
            Request r = allRequests.get(i);

            // Show only requests sent TO this donor
            if (r.donorPhone.equals(donorPhone) && !r.requesterPhone.equals(donorPhone)) {
                incoming.add(r);
            }
        }

        if (incoming.isEmpty()) {
            System.out.println("No incoming requests.");
            recentActivity.display();
            return;
        }

        System.out.println("\n---------------- INCOMING REQUESTS ----------------");
        for (int i = 0; i < incoming.size(); i++) {
            Request r = incoming.get(i);
            System.out.println((i + 1) + ". Requester Name : " + r.requesterName);
            System.out.println("   Requester Phone: " + r.requesterPhone);
            System.out.println("   Requester City : " + r.requesterCity);
            System.out.println("   Type           : " + (r.emergency ? "EMERGENCY" : "NORMAL"));
            System.out.println("   Status         : " + r.status);
            System.out.println("   Time           : " + r.requestTime);
            System.out.println("-----------------------------------------------");
        }

        System.out.print("Enter request number to respond (0 to back): ");
        int num = index.readInt();

        if (num == 0) {
            recentActivity.display();
            return;
        }

        if (num < 1 || num > incoming.size()) {
            System.out.println("Invalid request number.");
            return;
        }

        Request selected = incoming.get(num - 1);

        if (!selected.status.equals("PENDING")) {
            System.out.println("This request is already processed.");
            return;
        }

        System.out.print("Enter 1 to ACCEPT or 2 to REJECT: ");
        int action = index.readInt();

        if (action == 1) {
            selected.status = "ACCEPTED";
            if (!normalQueue.isEmpty()) {
                normalQueue.dequeue();
            }
            recentActivity.enqueue("Request accepted by donor " + selected.donorName);
            System.out.println("Request accepted.");
        } else if (action == 2) {
            selected.status = "REJECTED";
            if (!normalQueue.isEmpty()) {
                normalQueue.dequeue();
            }
            recentActivity.enqueue("Request rejected by donor " + selected.donorName);
            System.out.println("Request rejected.");
        } else {
            System.out.println("Invalid action.");
        }

        recentActivity.display();
    }

    public static void viewSentRequests(String requesterPhone) {
        autoDenyExpiredEmergencyRequests();

        boolean found = false;
        System.out.println("\n---------------- MY SENT REQUESTS ----------------");

        for (int i = 0; i < allRequests.size(); i++) {
            Request r = allRequests.get(i);
            if (r.requesterPhone.equals(requesterPhone)) {
                found = true;
                System.out.println((i + 1) + ". Donor Name   : " + r.donorName);
                System.out.println("   Donor Phone  : " + r.donorPhone);
                System.out.println("   Donor City   : " + r.donorCity);
                System.out.println("   Type         : " + (r.emergency ? "EMERGENCY" : "NORMAL"));
                System.out.println("   Status       : " + r.status);
                System.out.println("   Requested At : " + r.requestTime);
                System.out.println("-----------------------------------------------");
            }
        }

        if (!found) {
            System.out.println("No sent requests.");
        }
    }

    public static void removeAllRequestsOfUser(String phone) {
        for (int i = allRequests.size() - 1; i >= 0; i--) {
            Request r = allRequests.get(i);
            if (r.requesterPhone.equals(phone) || r.donorPhone.equals(phone)) {
                allRequests.remove(i);
            }
        }
    }
}