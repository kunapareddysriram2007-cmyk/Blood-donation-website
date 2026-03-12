import java.util.Scanner;

public class contact {

    static class ContactMessage {
        String name;
        String email;
        String message;
        long time;

        ContactMessage(String name, String email, String message) {
            this.name = name;
            this.email = email;
            this.message = message;
            this.time = System.currentTimeMillis();
        }
    }

    static class CharStack {
        char[] arr;
        int top;

        CharStack(int capacity) {
            arr = new char[Math.max(10, capacity)];
            top = -1;
        }

        void push(char ch) {
            if (top + 1 == arr.length) {
                char[] newArr = new char[arr.length * 2];
                System.arraycopy(arr, 0, newArr, 0, arr.length);
                arr = newArr;
            }
            arr[++top] = ch;
        }

        char pop() {
            if (isEmpty()) {
                return '\0';
            }
            return arr[top--];
        }

        boolean isEmpty() {
            return top == -1;
        }
    }

    static class MessageStack {
        ContactMessage[] arr;
        int top;

        MessageStack(int capacity) {
            arr = new ContactMessage[Math.max(10, capacity)];
            top = -1;
        }

        void push(ContactMessage msg) {
            if (top + 1 == arr.length) {
                ContactMessage[] newArr = new ContactMessage[arr.length * 2];
                System.arraycopy(arr, 0, newArr, 0, arr.length);
                arr = newArr;
            }
            arr[++top] = msg;
        }

        ContactMessage pop() {
            if (isEmpty()) {
                return null;
            }
            ContactMessage msg = arr[top];
            arr[top] = null;
            top--;
            return msg;
        }

        boolean isEmpty() {
            return top == -1;
        }
    }

    private static final MessageStack messageHistory = new MessageStack(20);

    public static void contactMenu(Scanner sc, register.Donor currentUser) {
        while (true) {
            System.out.println("\n--------------- CONTACT US ---------------");
            System.out.println("1. Send Message");
            System.out.println("2. View Sent Messages History");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            int choice = index.readInt();

            switch (choice) {
                case 1 -> sendMessage(sc, currentUser);
                case 2 -> displayMessages(currentUser.name);
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void sendMessage(Scanner sc, register.Donor currentUser) {
        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();

        System.out.print("Enter message: ");
        String message = sc.nextLine().trim();

        if (email.isEmpty() || message.isEmpty()) {
            System.out.println("Email and message cannot be empty.");
            return;
        }

        if (!isBalanced(message)) {
            System.out.println("Message rejected because brackets are not balanced.");
            return;
        }

        ContactMessage cm = new ContactMessage(currentUser.name, email, message);
        messageHistory.push(cm);
        System.out.println("Message sent successfully.");
    }

    public static boolean isBalanced(String s) {
        CharStack st = new CharStack(s.length());

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(' || ch == '{' || ch == '[') {
                st.push(ch);
            } else if (ch == ')' || ch == '}' || ch == ']') {
                if (st.isEmpty()) {
                    return false;
                }
                char top = st.pop();
                if ((ch == ')' && top != '(') ||
                    (ch == '}' && top != '{') ||
                    (ch == ']' && top != '[')) {
                    return false;
                }
            }
        }

        return st.isEmpty();
    }

    public static void displayMessages(String userName) {
        if (messageHistory.isEmpty()) {
            System.out.println("No messages found.");
            return;
        }

        MessageStack temp = new MessageStack(20);
        boolean found = false;

        System.out.println("\n----- CONTACT MESSAGES (LATEST FIRST) -----");
        while (!messageHistory.isEmpty()) {
            ContactMessage cm = messageHistory.pop();
            if (cm.name.equals(userName)) {
                found = true;
                System.out.println("Name    : " + cm.name);
                System.out.println("Email   : " + cm.email);
                System.out.println("Message : " + cm.message);
                System.out.println("Time    : " + cm.time);
                System.out.println("------------------------------------------");
            }
            temp.push(cm);
        }

        while (!temp.isEmpty()) {
            messageHistory.push(temp.pop());
        }

        if (!found) {
            System.out.println("No messages found.");
        }
    }
}