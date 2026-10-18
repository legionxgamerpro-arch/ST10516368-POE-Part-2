package com.mycompany.main;

//Imports Scanner so we can receive input
import java.util.Scanner;

//Part 2: ArrayList stores the messages
import java.util.ArrayList;

//Part 2: FileWriter and IOException are used to save the messages as a JSON file
import java.io.FileWriter;
import java.io.IOException;

//Creates the Main class
public class Main {

    //Creates Scanner to receive input from the user
    static Scanner input = new Scanner(System.in);

    //Stores the user's entered details
    static String username;
    static String password;
    static String cellphone;
    static String firstName;
    static String lastName;

    //Variables to store the user's registration details
    //Stores the registered username
    static String registeredUsername;

    //Stores the registered password
    static String registeredPassword;
    
    //Stores the registered cellphone number
    static String registeredCellPhone;

    //Part 2: Stores every message the user sends.
    //Each message is saved as: {message number, message ID, message hash, recipient, message text}
    static ArrayList<String[]> messages = new ArrayList<>();

    //Creates the username checking method
    public static boolean checkUserName(String username) {

        //The username must contain an underscore
        //The username must be no more than five characters long.
        if (username.length() <= 5 && username.contains("_")) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the password checking method
    //The password must have at least eight characters
    //It must contain a capital letter, a number and a special character.    
    public static boolean checkPasswordComplexity(String password) {

        if (password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*")) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the cellphone checking method
    //The number must start with +27
    //followed by exacly 9 digits
    public static boolean checkCellPhoneNumber(String cellphone) {

        if (cellphone.matches("^\\+27[0-9]{9}$")) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the registration method
    //It collects the user's details and checks that they are valid.
    public static String registerUser() {
        
            //Ask for first name
            System.out.print("Enter your first name: ");
            firstName = input.nextLine();
            
             //Ask for last name
            System.out.print("Enter your last name: ");
            lastName = input.nextLine();
        
                while (true) {
            //Ask for username
            System.out.print("Enter your username: ");
            username = input.nextLine();
            // Check username
            boolean usernameCorrect = checkUserName(username);
            //Check if username is correct
            if (usernameCorrect) {
                break;
            } else {
                System.out.println("Username is not correctly formatted; please "
                        + "ensure that your username contains an underscore and "
                        + "is no more than five characters in length.");
            }
        }
        
        while (true) {
            //Ask for password
            System.out.print("Enter your password: ");
            password = input.nextLine();
            //Check password
            boolean passwordCorrect = checkPasswordComplexity(password);
            if (passwordCorrect) {
                break;
            } else {
                System.out.println("Password is not correctly formatted; please "
                        + " ensure that the password contains a capital letter, "
                        + " a number, and a special character.");
            }
        }
        
        while (true) {
            // Ask for cellphone number
            System.out.print("Enter your cellphone number (+27): ");
            cellphone = input.nextLine();
            //Check cellphone number
            boolean cellphoneCorrect = checkCellPhoneNumber(cellphone);
            if (cellphoneCorrect) {
                break;
            } else {
                System.out.println("Cell phone number incorrectly formatted or "
                    + "does not contain the international code.");
            }
        }
        
        //Store the user's details after successful registration
        registeredUsername = username;
        registeredPassword = password;
        registeredCellPhone = cellphone;
        
        //Return a successful registration message
        return "User registered successfully";
    }
    
    //Creates the login method
    //Checks whether the username and password -
    //entered during login match the registered details.
    //Returns true or false only; any re-prompting is handled in main.
    public static boolean loginUser(String username, String password) {
        if (username.equals(registeredUsername)
                && password.equals(registeredPassword)) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the login status method
    //It displays a welcome message when login is successful.
    //Otherwise, it displays an error message.
    public static String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Welcome" + " " + firstName + " " + lastName + " " + 
                    "it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    //PART 2 - QUICKCHAT

    //Creates the message length checking method
    //A message may not be longer than 250 characters.
    public static boolean checkMessageLength(String message) {

        if (message.length() <= 250) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the message ID method
    //The ID is 10 digits: the loop counter (2 digits) + the last 8 digits of the time.
    public static String createMessageID(int messageNumber) {

        //Gets the current time (in milliseconds) as text
        String time = String.valueOf(System.currentTimeMillis());

        //Uses substring to take the last 8 digits of the time
        String lastEightDigits = time.substring(time.length() - 8);

        //Uses the loop counter for the first two digits (1 becomes 01)
        String firstTwoDigits = String.format("%02d", messageNumber % 100);

        return firstTwoDigits + lastEightDigits;
    }

    //Creates the message ID checking method
    //The message ID may not be longer than 10 characters.
    public static boolean checkMessageID(String messageID) {

        if (messageID.length() <= 10) {
            return true;
        } else {
            return false;
        }
    }

    //Creates the message hash method
    //Format: first two digits of the ID : message number : FIRSTWORDLASTWORD
    //Example: 01:1:HITONIGHT
    public static String createMessageHash(String messageID, int messageNumber, String message) {

        //Uses substring to take the first two digits of the message ID
        String firstTwoDigits = messageID.substring(0, 2);

        //Splits the message into words
        String[] words = message.trim().split("\\s+");

        //Gets the first and last word (punctuation is removed)
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        //Joins everything together and changes it to capital letters
        String hash = firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    //Creates the method that displays the details of one message
    public static void displayMessageDetails(String[] message) {

        System.out.println("Message ID: " + message[1]);
        System.out.println("Message Hash: " + message[2]);
        System.out.println("Recipient: " + message[3]);
        System.out.println("Message: " + message[4]);
    }

    //Creates the method that makes text safe to place inside a JSON file
    public static String escapeJson(String text) {

        //Backslashes and quotation marks must be escaped in JSON
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    //Creates the method that saves all the messages in a JSON file
    public static void saveMessagesToJson() {

        //Builds the JSON text
        String json = "[\n";
        int position = 0;

        //For-each loop goes through every stored message
        for (String[] message : messages) {
            position++;

            json += "  {\n";
            json += "    \"messageNumber\": \"" + message[0] + "\",\n";
            json += "    \"messageID\": \"" + message[1] + "\",\n";
            json += "    \"messageHash\": \"" + message[2] + "\",\n";
            json += "    \"recipient\": \"" + message[3] + "\",\n";
            json += "    \"message\": \"" + escapeJson(message[4]) + "\"\n";
            json += "  }";

            //No comma after the last message
            if (position < messages.size()) {
                json += ",";
            }
            json += "\n";
        }
        json += "]";

        //Writes the JSON text to a file called messages.json
        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json);
            System.out.println("Messages saved to messages.json");
        } catch (IOException e) {
            System.out.println("The messages could not be saved to the JSON file.");
        }
    }

    //Creates the send messages method
    //The user says how many messages to enter, then a for loop collects them.
    public static void sendMessages() {

        //Ask how many messages the user wants to enter
        int howMany = 0;
        while (howMany < 1) {
            System.out.print("How many messages do you want to enter? ");
            try {
                howMany = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                howMany = 0;
            }

            if (howMany < 1) {
                System.out.println("Please enter a whole number of 1 or more.");
            }
        }

        //For loop runs once for each message; i is the message number
        for (int i = 1; i <= howMany; i++) {

            System.out.println("");
            System.out.println("Message " + i + " of " + howMany);

            //Ask for the recipient's cellphone number (uses the Part 1 check)
            String recipient;
            while (true) {
                System.out.print("Enter the recipient's cellphone number (+27): ");
                recipient = input.nextLine();
                if (checkCellPhoneNumber(recipient)) {
                    break;
                } else {
                    System.out.println("Cell phone number incorrectly formatted or "
                            + "does not contain the international code.");
                }
            }

            //Ask for the message (maximum 250 characters)
            String messageText;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                messageText = input.nextLine();

                if (messageText.trim().isEmpty()) {
                    System.out.println("The message cannot be empty.");
                } else if (checkMessageLength(messageText)) {
                    System.out.println("Message ready to send.");
                    break;
                } else {
                    int tooMany = messageText.length() - 250;
                    System.out.println("Message exceeds 250 characters by " + tooMany
                            + ", please reduce size.");
                }
            }

            //Create the message ID and the message hash
            String messageID = createMessageID(i);
            String messageHash = createMessageHash(messageID, i, messageText);

            //Store the message in the ArrayList
            String[] newMessage = {String.valueOf(i), messageID, messageHash, recipient, messageText};
            messages.add(newMessage);

            //Show the message details
            System.out.println("");
            displayMessageDetails(newMessage);
        }

        //Save all the messages in the JSON file
        System.out.println("");
        saveMessagesToJson();
    }

    //Creates the show recently sent messages method
    public static void showMessages() {

        if (messages.isEmpty()) {
            System.out.println("No messages have been sent yet.");
        } else {
            //For-each loop goes through every stored message
            for (String[] message : messages) {
                System.out.println("");
                displayMessageDetails(message);
            }
        }
    }

    //Creates the QuickChat method
    //Shows the welcome message and the menu, and keeps running until the user quits.
    public static void runQuickChat() {
        
        System.out.println("");
        System.out.println("Welcome to QuickChat.");

        //While loop keeps the application running until the user chooses 3
        boolean running = true;
        while (running) {

            //Display the menu
            System.out.println("");
            System.out.println("Please choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter your choice (1-3): ");

            //Read the choice (letters or symbols will not crash the program)
            int choice;
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }

            if (choice == 1) {
                sendMessages();
            } else if (choice == 2) {
                showMessages();
            } else if (choice == 3) {
                System.out.println("Thank you for using QuickChat. Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid option, please enter 1, 2 or 3.");
            }
        }
    }

    //The main method runs the registration and login process
    public static void main(String[] args) {
        
        //Call the registration method
        String registrationMessage = registerUser();
        System.out.println(registrationMessage);
        
        //Continue to login if registration was successful
        if (registrationMessage.equals("User registered successfully")) {
        
        //Adds a blank line for neatness
        System.out.println("");
        
        //Loop in main keeps asking for login details until they are correct
        boolean loginSuccessful = false;
        while (!loginSuccessful) {
            
            //Call the login method
            System.out.print("Enter username to login: ");
            String loginUsername = input.nextLine();
            
            System.out.print("Enter password to login: ");
            String loginPassword = input.nextLine();
            
            //Checks whether login details are correct
            loginSuccessful = loginUser(loginUsername, loginPassword);
            
            //Call the method that displays the login result
            System.out.println(returnLoginStatus(loginSuccessful));
        }

        //Part 2: Start QuickChat after a successful login
        if (loginSuccessful) {
            runQuickChat();
        }
    }
  }
}
