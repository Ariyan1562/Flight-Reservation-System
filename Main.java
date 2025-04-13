import java.io.*;
import java.util.*;

class Flight {
    int id;
    String flightName, departure, destination;
    int availableSeats;
    double price;

    public Flight(int id, String flightName, String departure, String destination, int availableSeats, double price) {
        this.id = id;
        this.flightName = flightName;
        this.departure = departure;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.price = price;
    }
}

class User {
    String username, password;
    boolean isAdmin;

    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}


public class Main {
    public static void main(String[] args) {
        FlightReservationSystem.main(args);
    }
}
