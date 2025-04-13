import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlightReservationSystem {
    static List<Flight> flights = new ArrayList<>();
    static List<User> users = new ArrayList<>();
    static User loggedInUser = null;
    static Scanner scanner = new Scanner(System.in);
    static final String USERS_FILE = "users.txt";
    static final String FLIGHTS_FILE = "flights.txt";
    static List<String> bookingHistory = new ArrayList<>();

    public static void clearScreen() {
        for (int i = 0; i < 25; i++) {
            System.out.println();
        }
    }

    static void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Load users from file or add default users
    static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing users found. Default users will be added.");
            users.add(new User("admin", "admin123", true));
            users.add(new User("user", "user123", false));
        }
    }

    // Save users to the file
    static void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.printf("%s,%s,%b%n", user.username, user.password, user.isAdmin);
            }
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    // Load initial flight data
    static void loadFlights() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    flights.add(new Flight(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                            Integer.parseInt(parts[4]), Double.parseDouble(parts[5])));
                }
            }
        } catch (IOException e) {
            System.out.println("No flight data found. Default flights will be added.");
            flights.add(new Flight(1, "Flight A", "NYC", "LA", 50, 299.99));
            flights.add(new Flight(2, "Flight B", "Chicago", "Miami", 30, 199.99));
        }
    }

    // Save flights to the file
    static void saveFlights() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flights) {
                writer.printf("%d,%s,%s,%s,%d,%.2f%n",
                        flight.id, flight.flightName, flight.departure, flight.destination,
                        flight.availableSeats, flight.price);
            }
        } catch (IOException e) {
            System.out.println("Error saving flights.");
        }
    }

    // Register a new user
    static void registerUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        for (User user : users) {
            if (user.username.equals(username)) {
                System.out.println("Username already exists!");
                return;
            }
        }
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Register as Admin? (yes/no): ");
        boolean isAdmin = scanner.nextLine().equalsIgnoreCase("yes");

        users.add(new User(username, password, isAdmin));
        System.out.println("Registration Successful!");
        saveUsers();
    }

    // Login an existing user
    static void loginUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                loggedInUser = user;
                System.out.println("Login Successful!");
                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    // Change the password for logged-in user
    static void changePassword() {
        if (loggedInUser == null) {
            System.out.println("You are not logged in.");
            waitForEnter();
            return;
        }
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        if (!loggedInUser.password.equals(currentPassword)) {
            System.out.println("Incorrect current password!");
            return;
        }
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match!");
            return;
        }
        loggedInUser.password = newPassword;
        System.out.println("Password changed successfully!");
        saveUsers();
        waitForEnter();
    }

    // Display all available flights
    static void displayFlights() {
        if (flights.isEmpty()) {
            System.out.println("No flights available.");
            waitForEnter();
            return;
        }
        System.out.println("\nAvailable Flights:");
        for (Flight flight : flights) {
            System.out.printf("ID: %d | Name: %s | From: %s | To: %s | Seats: %d | Price: $%.2f\n",
                    flight.id, flight.flightName, flight.departure, flight.destination, flight.availableSeats, flight.price);
        }
        waitForEnter();
    }

    // Add a new flight (Admin-only)
    static void addFlight() {
        System.out.println("Adding a new flight! Please enter the following details.");
        System.out.print("Enter Flight Name: ");
        String flightName = scanner.nextLine();

        System.out.print("Enter Departure Location: ");
        String departure = scanner.nextLine();

        System.out.print("Enter Destination Location: ");
        String destination = scanner.nextLine();

        System.out.print("Enter Available Seats: ");
        int seats = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Ticket Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        int newId = flights.isEmpty() ? 1 : flights.get(flights.size() - 1).id + 1;
        flights.add(new Flight(newId, flightName, departure, destination, seats, price));

        saveFlights();
        System.out.println("Flight added successfully! Flight ID: " + newId);
        waitForEnter();
    }

    static void modifyFlight() {
        System.out.println("Modifying a flight! Here are the available flights:");
        displayFlights();

        System.out.print("Enter the Flight ID you want to modify: ");
        int flightId = Integer.parseInt(scanner.nextLine());
        Flight selectedFlight = null;

        for (Flight flight : flights) {
            if (flight.id == flightId) {
                selectedFlight = flight;
                break;
            }
        }

        if (selectedFlight == null) {
            System.out.println("Invalid Flight ID! Please try again.");
            waitForEnter();
            return;
        }

        System.out.println("Selected Flight: " + selectedFlight.flightName);
        System.out.print("Enter new Flight Name (or press Enter to skip): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) selectedFlight.flightName = newName;

        System.out.print("Enter new Departure Location (or press Enter to skip): ");
        String newDeparture = scanner.nextLine();
        if (!newDeparture.isEmpty()) selectedFlight.departure = newDeparture;

        System.out.print("Enter new Destination Location (or press Enter to skip): ");
        String newDestination = scanner.nextLine();
        if (!newDestination.isEmpty()) selectedFlight.destination = newDestination;

        System.out.print("Enter new Available Seats (or press Enter to skip): ");
        String newSeats = scanner.nextLine();
        if (!newSeats.isEmpty()) selectedFlight.availableSeats = Integer.parseInt(newSeats);

        System.out.print("Enter new Ticket Price (or press Enter to skip): ");
        String newPrice = scanner.nextLine();
        if (!newPrice.isEmpty()) selectedFlight.price = Double.parseDouble(newPrice);

        saveFlights(); // Save updated flight data
        System.out.println("Flight modified successfully!");
        waitForEnter();
    }

    // Book a flight
    static void bookFlight() {
        System.out.println("Booking a flight! Follow the steps below.");
        if (flights.isEmpty()) {
            System.out.println("No flights available for booking at the moment.");
            return;
        }
        displayFlights();

        System.out.print("/nEnter the Flight ID you want to book: ");
        try {
            int flightId = Integer.parseInt(scanner.nextLine());
            Flight selectedFlight = null;
            for (Flight flight : flights) {
                if (flight.id == flightId) {
                    selectedFlight = flight;
                    break;
                }
            }

            if (selectedFlight == null) {
                System.out.println("Invalid Flight ID! Please try again.");
                return;
            }

            System.out.println("You selected: " + selectedFlight.flightName);
            if (selectedFlight.availableSeats <= 0) {
                System.out.println("Sorry, no seats available on this flight.");
                return;
            }

            // Collect passenger details
            System.out.print("Enter your name: ");
            String passengerName = scanner.nextLine();

            // Confirm and finalize booking
            System.out.printf("Flight: %s\nFrom: %s\nTo: %s\nPrice: $%.2f\n",
                    selectedFlight.flightName, selectedFlight.departure, selectedFlight.destination, selectedFlight.price);
            System.out.print("Confirm booking and proceed to payment? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Booking canceled!");
                return;
            }

            if (!processPayment(selectedFlight.price)) {
                System.out.println("Payment failed. Booking not completed.");
                return;
            }

            // Deduct seat and confirm
            selectedFlight.availableSeats -= 1;
            String bookingDetails = String.format("Flight: %s | From: %s | To: %s | Passenger: %s",
                    selectedFlight.flightName, selectedFlight.departure, selectedFlight.destination, passengerName);
            bookingHistory.add(bookingDetails);
            System.out.println("Booking confirmed! Thank you, " + passengerName + ".");
            System.out.printf("Remaining seats on flight: %d\n", selectedFlight.availableSeats);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a numeric Flight ID.");
        }


    }
    static boolean processPayment(double amount) {
        clearScreen();
        System.out.println("Select Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Mobile banking(Bkash/Nagad/Rocket)");
        System.out.println("3. Wallet");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter Credit Card Number: ");
                String cardNumber = scanner.nextLine();
                System.out.print("Enter Expiry Date (MM/YY): ");
                String expiry = scanner.nextLine();
                System.out.print("Enter CVV: ");
                String cvv = scanner.nextLine();
                // You can add validation here if needed
                System.out.println("Processing credit card payment...");
            }
            case "2" -> {
                System.out.print("Enter your mobile number: ");
                String upiId = scanner.nextLine();
                System.out.println("Processing Mobile Banking payment...");
            }
            case "3" -> {
                System.out.print("Enter Wallet ID: ");
                String walletId = scanner.nextLine();
                System.out.println("Processing wallet payment...");
            }
            default -> {
                System.out.println("Invalid payment method selected.");
                return false;
            }
        }

        System.out.printf("Payment of $%.2f successful!%n", amount);
        return true;
    }



    // Cancel booking
    static void cancelBooking() {
        if (bookingHistory.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            waitForEnter();
            return;
        }

        // Display the user's booking history for cancellation selection
        System.out.println("Your Booking History:");
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, bookingHistory.get(i));
        }

        System.out.print("Enter the number of the booking you want to cancel: ");
        try {
            int bookingIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (bookingIndex < 0 || bookingIndex >= bookingHistory.size()) {
                System.out.println("Invalid choice! Please select a valid booking.");
                return;
            }

            // Remove the booking and log cancellation
            String canceledBooking = bookingHistory.remove(bookingIndex);
            System.out.printf("Booking '%s' has been canceled.%n", canceledBooking);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid booking number.");
        }
        waitForEnter();
    }

    static void deleteFlight() {
        System.out.println("Deleting a flight! Here are the available flights:");
        displayFlights();

        System.out.print("Enter the Flight ID you want to delete: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        Flight selectedFlight = null;
        for (Flight flight : flights) {
            if (flight.id == flightId) {
                selectedFlight = flight;
                break;
            }
        }

        if (selectedFlight == null) {
            System.out.println("Invalid Flight ID! Please try again.");
            waitForEnter();
            return;
        }

        flights.remove(selectedFlight);
        saveFlights(); // Save updated flight data
        System.out.println("Flight deleted successfully!");
    }

    static void viewUsers() {
        System.out.println("Here is a list of all registered users:");
        if (users.isEmpty()) {
            System.out.println("No users found.");
            waitForEnter();
            return;
        }
        for (User user : users) {
            System.out.printf("Username: %s | Admin: %s%n", user.username, user.isAdmin ? "Yes" : "No");
        }
        waitForEnter();
    }

    static void searchFlights() {
        System.out.print("Enter Departure Location: ");
        String departure = scanner.nextLine();
        System.out.print("Enter Destination Location: ");
        String destination = scanner.nextLine();

        System.out.println("Searching for flights...");
        boolean found = false;
        for (Flight flight : flights) {
            if (flight.departure.equalsIgnoreCase(departure) && flight.destination.equalsIgnoreCase(destination)) {
                System.out.printf("ID: %d | Name: %s | From: %s | To: %s | Seats: %d | Price: $%.2f%n",
                        flight.id, flight.flightName, flight.departure, flight.destination, flight.availableSeats, flight.price);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No flights found matching the criteria.");
        }
        waitForEnter();
    }
    static void removeUser() {
        viewUsers();
        System.out.print("Enter the username of the user to remove: ");
        String usernameToRemove = scanner.nextLine();

        if (loggedInUser != null && loggedInUser.username.equals(usernameToRemove)) {
            System.out.println("You cannot remove yourself.");
            waitForEnter();
            return;
        }

        User userToRemove = null;
        for (User user : users) {
            if (user.username.equals(usernameToRemove)) {
                userToRemove = user;
                break;
            }
        }

        if (userToRemove == null) {
            System.out.println("User not found.");
            waitForEnter();
            return;
        }

        users.remove(userToRemove);
        saveUsers();
        System.out.println("User '" + usernameToRemove + "' has been removed.");
        waitForEnter();
    }


    // Admin menu
    static void adminMenu() {
        while (loggedInUser != null && loggedInUser.isAdmin) {
            clearScreen();
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Flight\n2. View Flights\n3. Modify Flight\n4. Delete Flight");
            System.out.println("5. View Users\n6. Remove User\n7. Change Password\n8. Logout");
            System.out.print("Enter choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addFlight();         // Add a new flight
                    case 2 -> displayFlights();    // View all available flights
                    case 3 -> modifyFlight();      // Modify an existing flight
                    case 4 -> deleteFlight();      // Delete a flight by ID
                    case 5 -> viewUsers();         // View all registered users
                    case 6 -> removeUser();
                    case 7 -> changePassword();    // Change admin's password
                    case 8 -> {                    // Logout
                        loggedInUser = null;
                        return;
                    }
                    default -> System.out.println("Invalid choice! Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    static void viewBookingHistory() {
        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings found in your history.");
            waitForEnter();
            return;
        }

        System.out.println("Your Booking History:");
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, bookingHistory.get(i));
        }
        waitForEnter();
    }

    static void userMenu() {
        while (loggedInUser != null && !loggedInUser.isAdmin) {
            clearScreen();
            System.out.println("\nUser Menu:");
            System.out.println("1. View Flights\n2. Search Flights\n3. Book Flight\n4. View Booking History");
            System.out.println("5. Cancel Booking\n6. Change Password\n7. Logout");
            System.out.print("Enter choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> displayFlights();
                    case 2 -> searchFlights();
                    case 3 -> bookFlight();
                    case 4 -> viewBookingHistory();
                    case 5 -> cancelBooking();
                    case 6 -> changePassword();
                    case 7 -> {
                        loggedInUser = null;
                        return;
                    }
                    default -> System.out.println("Invalid choice! Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }


    public static void main(String[] args) {
        loadUsers(); // Load user data from file or initialize default users
        loadFlights(); // Load initial flight data

        while (true) {
            clearScreen();
            System.out.println("\n1. Register\n2. Login\n3. Exit\nEnter choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> registerUser(); // Register a new user
                    case 2 -> loginUser(); // Login an existing user
                    case 3 -> {
                        System.out.println("Exiting...");
                        saveUsers(); // Save user data before exiting
                        System.exit(0); // Exit the program
                    }
                    default -> System.out.println("Invalid choice! Please select a valid option.");
                }

                // Once logged in, determine the menu based on user role
                if (loggedInUser != null) {
                    if (loggedInUser.isAdmin)
                        adminMenu(); // Access admin menu for admin users
                    else
                        userMenu(); // Access user menu for regular users
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}
