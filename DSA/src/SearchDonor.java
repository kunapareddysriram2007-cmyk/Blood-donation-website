import java.util.*;

public class SearchDonor {

    private Register reg;

    static class Match {
        Register.Donor d;
        double dist;

        Match(Register.Donor d, double dist) {
            this.d = d;
            this.dist = dist;
        }
    }

    public SearchDonor(Register reg) {
        this.reg = reg;
    }

    public void searchDonors(Scanner sc, Register.Donor currentUser) {
        System.out.print("Enter required blood group: ");
        String bg = sc.nextLine().trim().toUpperCase();

        System.out.print("Enter your latitude: ");
        double ulat;
        try {
            ulat = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid latitude.");
            return;
        }

        System.out.print("Enter your longitude: ");
        double ulng;
        try {
            ulng = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid longitude.");
            return;
        }

        ArrayList<Match> ans = new ArrayList<>();

        // Linear Search on linked list
        Register.Node t = reg.getHead();
        while (t != null) {
            if (currentUser != null && t.d.phone.equals(currentUser.phone)) {
                t = t.next;
                continue;
            }

            if (t.d.blood.equalsIgnoreCase(bg)) {
                double ds = distance(ulat, ulng, t.d.lat, t.d.lng);
                ans.add(new Match(t.d, ds));
            }
            t = t.next;
        }

        if (ans.size() == 0) {
            System.out.println("No donors found.");
            return;
        }

        Match[] arr = ans.toArray(new Match[0]);
        mergeSort(arr, 0, arr.length - 1);

        System.out.println("\nMatched Donors (Nearest First)");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%d. %s | %s | %s, %s | %.2f km | Phone: %s%n",
                    i + 1,
                    arr[i].d.name,
                    arr[i].d.blood,
                    arr[i].d.city,
                    arr[i].d.area,
                    arr[i].dist,
                    arr[i].d.phone);
        }
    }

    public ArrayList<Match> getSortedMatches(String bg, double ulat, double ulng) {
        ArrayList<Match> ans = new ArrayList<>();

        Register.Node t = reg.getHead();
        while (t != null) {
            if (t.d.blood.equalsIgnoreCase(bg)) {
                double ds = distance(ulat, ulng, t.d.lat, t.d.lng);
                ans.add(new Match(t.d, ds));
            }
            t = t.next;
        }

        Match[] arr = ans.toArray(new Match[0]);
        if (arr.length > 0) {
            mergeSort(arr, 0, arr.length - 1);
        }

        ArrayList<Match> out = new ArrayList<>();
        for (Match m : arr) {
            out.add(m);
        }
        return out;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy) * 111.0;
    }

    private void mergeSort(Match[] a, int l, int r) {
        if (l >= r) return;
        int m = (l + r) / 2;
        mergeSort(a, l, m);
        mergeSort(a, m + 1, r);
        merge(a, l, m, r);
    }

    private void merge(Match[] a, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        Match[] x = new Match[n1];
        Match[] y = new Match[n2];

        for (int i = 0; i < n1; i++) x[i] = a[l + i];
        for (int j = 0; j < n2; j++) y[j] = a[m + 1 + j];

        int i = 0, j = 0, k = l;

        while (i < n1 && j < n2) {
            if (x[i].dist <= y[j].dist) {
                a[k++] = x[i++];
            } else {
                a[k++] = y[j++];
            }
        }

        while (i < n1) a[k++] = x[i++];
        while (j < n2) a[k++] = y[j++];
    }
}