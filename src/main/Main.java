package main;

import repo.RoomRepo;
import repo.GuestRepo;
import utils.Utils;

public class Main {
    public static void main(String[] args) {
        RoomRepo roomRepo = new RoomRepo();
        GuestRepo guestRepo = new GuestRepo();

        while (true) {
            System.out.println("\n=== Room Management Module (J1.L.P0030) ===");
            System.out.println("1. Import Room Data from Text File");
            System.out.println("2. Display Available Room List");
            System.out.println("3. Enter Guest Information");
            System.out.println("4. Update Guest Stay Information");
            System.out.println("5. Search Guest by National ID");
            System.out.println("6. Delete Guest Reservation Before Arrival");
            System.out.println("7. List Vacant Rooms");
            System.out.println("8. Monthly Revenue Report");
            System.out.println("9. Revenue Report by Room Type");
            System.out.println("10. Save Guest Information");
            System.out.println("0. Quit");
            int choice = Utils.getAnInteger("Choose [0-10]: ", 0, 10, false);
            switch (choice) {
                case 1 -> roomRepo.importRoomsFromFile();
                case 2 -> roomRepo.displayAvailableRooms();
                case 3 -> guestRepo.enterGuest(roomRepo);
                case 4 -> guestRepo.updateGuestStay(roomRepo);
                case 5 -> guestRepo.searchGuestByNationalID();
                case 6 -> guestRepo.deleteReservationBeforeArrival(roomRepo);
                case 7 -> roomRepo.listVacantRooms();
                case 8 -> guestRepo.monthlyRevenueReport();
                case 9 -> guestRepo.revenueReportByRoomType();
                case 10 -> guestRepo.saveGuestInfo();
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
            }
        }
    }
}