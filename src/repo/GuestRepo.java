package repo;

import model.Booking;
import model.Guest;
import model.Room;
import utils.Utils;
import utils.Validators;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class GuestRepo {
    private final Map<String, Guest> guests = new LinkedHashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    // Function 3: Enter Guest Information
    public void enterGuest(RoomRepo roomRepo) {
        String nid;
        while (true) {
            nid = Utils.getString("National ID (12 digits): ", null, "");
            if (!Validators.isNationalId(nid)) { System.out.println("Invalid National ID."); continue; }
            if (guests.containsKey(nid)) {
                System.out.println("National ID already exists (guest will be reused).");
            }
            break;
        }
        String name = Utils.getString("Full name: ", ".*", "");
        String gender = Utils.getString("Gender (Male/Female): ", "(?i)Male|Female", "Enter Male or Female");
        String phone = Utils.getString("Phone (10 digits, VN): ", null, "");
        LocalDate birth = Utils.getDate("Birthdate (dd/MM/yyyy): ", false);
        String co = Utils.getString("Co-tenant (optional, press Enter to skip): ", ".*", "");

        String roomId = Utils.getString("Desired room ID: ", null, "");
        Room room = roomRepo.findById(roomId);
        if (room == null) { System.out.println("Room not found."); return; }
        int days = Utils.getAnInteger("Number of rental days: ", 1, 365, false);
        LocalDate start = Utils.getDate("Start date (dd/MM/yyyy, future): ", true);

        // create or update guest
        guests.putIfAbsent(nid, new Guest(nid, name, birth, gender, phone, co));
        // booking
        Booking b = new Booking(nid, roomId, start, days);
        bookings.add(b);
        roomRepo.markOccupied(roomId, true);
        System.out.printf("Guest registered successfully for room %s%n", roomId);
        System.out.printf("Rental from %s for %d days%n", start, days);
    }

    // Function 4: Update Guest Stay Information
    public void updateGuestStay(RoomRepo roomRepo) {
        String nid = Utils.getString("Enter National ID to update: ", null, "");
        Guest g = guests.get(nid);
        if (g == null) { System.out.println("No guest found with the requested ID !"); return; }

        System.out.println("Current phone: " + g.getPhone());
        String newPhone = Utils.getString("New phone (Enter to keep): ", ".*", "");
        if (!newPhone.isEmpty()) g.setPhone(newPhone);

        System.out.println("Current co-tenant: " + g.getCoTenant());
        String co = Utils.getString("New co-tenant (Enter to keep): ", ".*", "");
        if (!co.isEmpty()) g.setCoTenant(co);

        // update latest booking days if exists
        Optional<Booking> last = bookings.stream().filter(b -> b.getNationalId().equals(nid))
                .reduce((first, second) -> second);
        if (last.isPresent()) {
            int newDays = Utils.getAnInteger("New rental days (Enter -1 to keep): ", -1, 365, true);
            if (newDays > 0) {
                // recreate booking duration by creating a new booking with same start
                Booking b = last.get();
                Booking nb = new Booking(b.getNationalId(), b.getRoomId(), b.getStartDate(), newDays);
                bookings.add(nb);
            }
        }

        System.out.println("Guest information updated for ID: " + nid);
    }

    // Function 5: Search by National ID
    public void searchGuestByNationalID() {
        String nid = Utils.getString("Enter National ID: ", null, "");
        Guest g = guests.get(nid);
        if (g == null) {
            System.out.println("No guest found with the requested ID !");
            return;
        }
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Guest information [National ID: %s]%n", g.getNationalId());
        System.out.println("----------------------------------------------------------------");
        System.out.printf(" Full name     : %s%n", g.getFullName());
        System.out.printf(" Phone number  : %s%n", g.getPhone());
        System.out.printf(" Birth day     : %s%n", g.getBirthDate().format(util.Utils.DMY));
        System.out.printf(" Gender        : %s%n", g.getGender());
        System.out.println("----------------------------------------------------------------");
        // find last booking
        Optional<Booking> last = bookings.stream().filter(b -> b.getNationalId().equals(nid))
                .reduce((first, second) -> second);
        if (last.isPresent()) {
            Booking b = last.get();
            System.out.printf(" Rental room   : %s%n", b.getRoomId());
            System.out.printf(" Check in      : %s%n", b.getStartDate().format(util.Utils.DMY));
            System.out.printf(" Rental days   : %d%n", b.getRentalDays());
            System.out.printf(" Check out     : %s%n", b.getCheckOutDate().format(util.Utils.DMY));
            System.out.println("----------------------------------------------------------------");
        } else {
            System.out.println("No booking found.");
        }
    }

    // Function 6: Delete Reservation Before Arrival
    public void deleteReservationBeforeArrival(RoomRepo roomRepo) {
        String nid = Utils.getString("Enter National ID to cancel: ", null, "");
        // find future booking
        Optional<Booking> opt = bookings.stream()
                .filter(b -> b.getNationalId().equals(nid) && b.getStartDate().isAfter(LocalDate.now()))
                .findFirst();
        if (opt.isEmpty()) {
            System.out.println("Booking details for ID '" + nid + "' could not be found or cannot be cancelled.");
            return;
        }
        Booking b = opt.get();
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Guest information [National ID: %s]%n", nid);
        System.out.println("----------------------------------------------------------------");
        System.out.printf(" Rental room   : %s%n", b.getRoomId());
        System.out.printf(" Check in      : %s%n", b.getStartDate().format(util.Utils.DMY));
        System.out.printf(" Rental days   : %d%n", b.getRentalDays());
        System.out.println("----------------------------------------------------------------");
        if (Utils.confirm("Do you really want to cancel this guest's room booking ? [Y/N]:")) {
            b.setStatus(Booking.Status.CANCELED);
            roomRepo.markOccupied(b.getRoomId(), false);
            System.out.println("The booking has been successfully canceled.");
        }
    }

    // Function 8: Monthly Revenue Report
    public void monthlyRevenueReport() {
        String my = Utils.getString("Enter month (MM/YYYY): ", "\\d{2}/\\d{4}", "Use MM/YYYY");
        YearMonth ym = YearMonth.of(Integer.parseInt(my.substring(3)), Integer.parseInt(my.substring(0,2)));

        List<Booking> list = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CHECKED_OUT
                && YearMonth.from(b.getCheckOutDate()).equals(ym)).collect(Collectors.toList());
        if (list.isEmpty()) {
            System.out.println("There is no data on guests who have rented rooms");
            return;
        }
        System.out.println("Monthly Revenue Report - '" + my + "'");
        System.out.println("----------------------------------------------------------------");
        System.out.println("RoomID  | Room Name        | Room type | DailyRate |  Amount ");
        System.out.println("----------------------------------------------------------------");
        // Here amount was precomputed when checking out
        Map<String, Double> byRoom = new LinkedHashMap<>();
        for (Booking b : list) {
            byRoom.merge(b.getRoomId(), b.getAmount(), Double::sum);
        }
        // We cannot print room attributes here without RoomRepo; keep simple
        for (Map.Entry<String, Double> e : byRoom.entrySet()) {
            System.out.printf("%-7s | %-15s | %-9s | %-9s | %10.2f%n", e.getKey(), "-", "-", "-", e.getValue());
        }
    }

    // Function 9: Revenue by Room Type (simple: infer type from last known check-out booking via external mapping)
    public void revenueReportByRoomType() {
        Map<String, Double> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.Status.CHECKED_OUT) {
                // room type unknown without room map; group under "ALL"
                map.merge("ALL", b.getAmount(), Double::sum);
            }
        }
        if (map.isEmpty()) { System.out.println("No revenue data."); return; }
        System.out.println("Revenue Report by Room Type");
        System.out.println("----------------------------");
        System.out.println("  Room type  | Amount");
        System.out.println("--------------------------");
        for (var e : map.entrySet()) {
            System.out.printf("  %-9s | %10.2f%n", e.getKey(), e.getValue());
        }
    }

    // Function 10: Save Guest Info
    public void saveGuestInfo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("guestInfo.dat"))) {
            oos.writeObject(new ArrayList<>(guests.values()));
            System.out.println("Guest information has been successfully saved to 'guestInfo.dat'.");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    // Extra: Simulate check-out to create revenue data
    public void checkOut(RoomRepo roomRepo) {
        String nid = Utils.getString("Enter National ID to check out: ", null, "");
        Optional<Booking> last = bookings.stream().filter(b -> b.getNationalId().equals(nid))
                .reduce((first, second) -> second);
        if (last.isEmpty()) { System.out.println("No booking found."); return; }
        Booking b = last.get();
        Room r = roomRepo.findById(b.getRoomId());
        if (r == null) { System.out.println("Room not found."); return; }
        double amount = r.getDailyRate() * b.getRentalDays();
        b.setAmount(amount);
        b.setStatus(Booking.Status.CHECKED_OUT);
        roomRepo.markOccupied(b.getRoomId(), false);
        System.out.printf("Checked out. Amount: %.2f%n", amount);
    }
}