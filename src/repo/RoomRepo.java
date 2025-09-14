package repo;

import model.Room;
import utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RoomRepo {
    private final Map<String, Room> rooms = new LinkedHashMap<>();

    public void importRoomsFromFile() {
        try {
            String path = Utils.getString("Enter path to Active_Room_List.txt: ", null, "");
            Path p = Path.of(path);
            if (!Files.exists(p)) {
                System.out.println("File not found.");
                return;
            }
            int ok = 0, fail = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\s*;\\s*");
                    if (parts.length != 6) { fail++; continue; }
                    String id = parts[0];
                    if (rooms.containsKey(id)) { fail++; continue; }
                    String name = parts[1];
                    String type = parts[2];
                    double rate;
                    int cap;
                    try {
                        rate = Double.parseDouble(parts[3]);
                        cap = Integer.parseInt(parts[4]);
                    } catch (Exception e) { fail++; continue; }
                    String furn = parts[5];
                    if (rate <= 0 || cap <= 0) { fail++; continue; }
                    Room r = new Room(id, name, type, rate, cap, furn);
                    rooms.put(id, r);
                    ok++;
                }
            }
            System.out.printf("%d rooms successfully loaded.%n", ok);
            System.out.printf("%d entries failed.%n", fail);
        } catch (Exception e) {
            System.out.println("Error importing rooms: " + e.getMessage());
        }
    }

    public void displayAvailableRooms() {
        if (rooms.isEmpty()) {
            System.out.println("Room list is currently empty, not loaded yet.");
            return;
        }
        printHeader();
        rooms.values().forEach(r -> System.out.println(r.toString()));
    }

    public void listVacantRooms() {
        List<Room> free = rooms.values().stream().filter(r -> !r.isOccupied()).collect(Collectors.toList());
        if (free.isEmpty()) {
            System.out.println("All rooms have currently been rented out; no rooms are available");
            return;
        }
        printHeader();
        free.forEach(r -> System.out.println(r.toString()));
    }

    public Room findById(String id) {
        return rooms.get(id);
    }

    public Collection<Room> all() { return rooms.values(); }

    public void markOccupied(String id, boolean occ) {
        Room r = rooms.get(id);
        if (r != null) r.setOccupied(occ);
    }

    private void printHeader() {
        System.out.println("RoomID | RoomName       | Type     |   Rate | Capacity | Furniture");
        System.out.println("-------+----------------+----------+--------+----------+-----------------------");
    }
}