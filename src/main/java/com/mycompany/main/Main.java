package com.mycompany.main;

//Imports Scanner so we can receive input
import java.util.Scanner;

//Part 2: ArrayList stores the messages
import java.util.ArrayList;

//Part 2: FileWriter and IOException are used to save the messages as a JSON file
import java.io.FileWriter;
import java.io.IOException;

//Part 2: Random is used to generate a unique ten-digit Message ID
import java.util.Random;

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

    //Part 2: Stores messages that the user chose to store for later
    static ArrayList<String[]> storedMessages = new ArrayList<>();

    //The number of messages the user chose at the start of QuickChat.
    static int totalMessagesToEnter = 0;

    //Counts every message entered, including messages that are sent, stored, or disregarded.
    static int messagesCreated = 0;

    //Creates the username checking method
    public static boolean checkUserName(String username) {

        //The username must contain an underscore
        //The username must be no more than five characters long.
        return username != null && username.length() <= 5 && username.contains("_");
    }

    //Creates the password checking method
    //The password must have at least eight characters
    //It must contain a capital letter, a number and a special character.    
    public static boolean checkPasswordComplexity(String password) {

        return password != null && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    //Creates the cellphone checking method
    //The number must start with +27
    //followed by exacly 9 digits
    public static boolean checkCellPhoneNumber(String cellphone) {

        return cellphone != null && cellphone.matches("^\\+27[0-9]{9}$");
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
                        + "ensure that the password contains at least eight characters, "
                        + "a capital letter, a number, and a special character.");
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
        return username.equals(registeredUsername)
                && password.equals(registeredPassword);
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

        return message != null && message.length() <= 250;
    }

    //Creates a ten-digit Message ID using the loop counter and random digits.
    //The first two digits match the message counter, and the last eight are random.
    public static String createMessageID(int messageNumber) {

        String randomID = createRandomMessageID();
        String firstTwoDigits = String.format("%02d", messageNumber % 100);

        //substring keeps the random ID's last eight digits.
        return firstTwoDigits + randomID.substring(2);
    }

    //Creates a randomly generated ten-digit Message ID (required by the brief)
    public static String createRandomMessageID() {
        Random random = new Random();
        //Generate a number between 1000000000 and 9999999999 (10 digits)
        long id = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

        //Creates the message ID checking method
    //The message ID may not be longer than 10 characters.
    public static boolean checkMessageID(String messageID) {

        return messageID != null && messageID.length() <= 10;
    }

    //Creates the message hash method
    //Format: first two digits of the ID : message number : FIRSTWORDLASTWORD
    //Example from the test data: 00:0:HITONIGHT
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

    //Escapes characters that JSON strings cannot contain unescaped.
    public static String escapeJson(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            switch (character) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                    break;
            }
        }
        return escaped.toString();
    }

    //Adds one message as a JSON object. The status distinguishes sent and stored messages.
    private static void appendMessageJson(StringBuilder json, String[] message, String status) {
        json.append("  {\n");
        json.append("    \"messageNumber\": \"").append(escapeJson(message[0])).append("\",\n");
        json.append("    \"messageID\": \"").append(escapeJson(message[1])).append("\",\n");
        json.append("    \"messageHash\": \"").append(escapeJson(message[2])).append("\",\n");
        json.append("    \"recipient\": \"").append(escapeJson(message[3])).append("\",\n");
        json.append("    \"message\": \"").append(escapeJson(message[4])).append("\",\n");
        json.append("    \"status\": \"").append(status).append("\"\n");
        json.append("  }");
    }

    //Saves the sent and stored messages to messages.json.
    public static void saveMessagesToJson() {
        StringBuilder json = new StringBuilder("[\n");
        boolean firstMessage = true;

        for (String[] message : messages) {
            if (!firstMessage) {
                json.append(",\n");
            }
            appendMessageJson(json, message, "sent");
            firstMessage = false;
        }

        for (String[] message : storedMessages) {
            if (!firstMessage) {
                json.append(",\n");
            }
            appendMessageJson(json, message, "stored");
            firstMessage = false;
        }

        if (!firstMessage) {
            json.append("\n");
        }
        json.append("]");

        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json.toString());
            System.out.println("Messages saved to messages.json");
        } catch (IOException e) {
            System.out.println("The messages could not be saved to the JSON file.");
        }
    }

    //Part 2: Returns the total number of messages that have been sent
    public static int returnTotalMessages() {
        return messages.size();
    }

    //Part 2: Prints all messages that have been sent
    public static String printMessages() {
        if (messages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (String[] message : messages) {
            sb.append("Message ID: ").append(message[1]).append("\n");
            sb.append("Message Hash: ").append(message[2]).append("\n");
            sb.append("Recipient: ").append(message[3]).append("\n");
            sb.append("Message: ").append(message[4]).append("\n\n");
        }
        return sb.toString().trim();
    }

    //Enters the remaining messages from the total the user selected at startup.
    public static void sendMessages() {

        int howMany = totalMessagesToEnter - messagesCreated;
        if (howMany <= 0) {
            System.out.println("You have already entered the set number of messages.");
            return;
        }

        //The loop counter starts at zero to match the supplied hash test data.
        for (int i = 0; i < howMany; i++) {
            int messageNumber = messagesCreated;

            System.out.println("");
            System.out.println("Message " + (messageNumber + 1) + " of " + totalMessagesToEnter);

            //Ask for the recipient's cellphone number and use the Message class method.
            String recipient;
            while (true) {
                System.out.print("Enter the recipient's cellphone number (+27): ");
                recipient = input.nextLine();
                String recipientStatus = Message.checkRecipientCell(recipient);
                System.out.println(recipientStatus);
                if (recipientStatus.equals("Cell phone number successfully captured.")) {
                    break;
                }
            }

            //Ask for the message (maximum 250 characters).
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
                            + "; please reduce the size.");
                }
            }

            //Create the Message ID and Message Hash using the zero-based loop counter.
            String messageID = createMessageID(messageNumber);
            String messageHash = createMessageHash(messageID, messageNumber, messageText);

            //Store the message temporarily while the user chooses an action.
            String[] newMessage = {String.valueOf(messageNumber), messageID, messageHash, recipient, messageText};

            //Ask whether to send, disregard, or store this message.
            String actionResult = Message.SentMessage(newMessage);
            System.out.println(actionResult);

            //Full details displayed after a message has been sent.
            if (actionResult.equals("Message successfully sent.")) {
                System.out.println("");
                displayMessageDetails(newMessage);
            }

            //Every entered message uses one place in the fixed quota, whichever action was chosen.
            messagesCreated++;
        }

        System.out.println("");
        saveMessagesToJson();
        System.out.println("Total number of messages sent: " + returnTotalMessages());
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

    //Shows the welcome message, asks for the message quota once, and runs the menu until Quit.
    public static void runQuickChat() {

        System.out.println("");
        System.out.println("Welcome to QuickChat.");

        messagesCreated = 0;
        totalMessagesToEnter = 0;
        while (totalMessagesToEnter < 1) {
            System.out.print("How many messages do you want to enter? ");
            try {
                totalMessagesToEnter = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                totalMessagesToEnter = 0;
            }

            if (totalMessagesToEnter < 1) {
                System.out.println("Please enter a whole number of 1 or more.");
            }
        }

        boolean running = true;
        while (running) {
            System.out.println("");
            System.out.println("Please choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter your choice (1-3): ");

            int choice;
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }

            switch (choice) {
                case 1:
                    sendMessages();
                    break;
                case 2:
                    //This feature is still in development and must display "Coming Soon."
                    System.out.println("");
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("");
                    System.out.println("Thank you for using QuickChat. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please enter 1, 2 or 3.");
                    break;
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

/**
 * Part 2 : Message Class
 * Contains the methods that handle message validation, hashing,
 * sending / storing / discarding, and reporting totals.
 */
class Message {

    //Boolean: checkMessageID()
    //Ensures that the message ID is not more than ten characters.
    public static boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10;
    }

    //String: checkRecipientCell()
    //Ensures that the recipient cell number is correctly formatted
    //(starts with the international code and has the correct length).
    //Returns a success or failure message.
    public static String checkRecipientCell(String cellNumber) {
        if (Main.checkCellPhoneNumber(cellNumber)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does "
                    + "not contain an international code. Please correct "
                    + "the number and try again.";
        }
    }

    //String: createMessageHash()
    //Creates and returns the Message Hash.
    public static String createMessageHash(String messageID, int messageNumber, String message) {
        return Main.createMessageHash(messageID, messageNumber, message);
    }

    /**
     * String: SentMessage()
     * Allows the user to choose if they want to:
     *   1) Send Message
     *   2) Disregard Message
     *   3) Store Message to send later
     * Returns the appropriate confirmation string.
     */
    public static String SentMessage(String[] message) {
        System.out.println("");
        System.out.println("Please choose an action for this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter your choice (1-3): ");

        int choice;
        try {
            choice = Integer.parseInt(Main.input.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = 0;
        }

        switch (choice) {
            case 1:
                //Add the message to the sent messages list
                Main.messages.add(message);
                return "Message successfully sent.";
            case 2:
                //Disregard / delete the message
                return "Press 0 to delete the message.";
            case 3:
                //Store the message for later
                Main.storedMessages.add(message);
                return "Message successfully stored.";
            default:
                //If the user enters an invalid option we treat it as disregard
                return "Press 0 to delete the message.";
        }
    }

    //String: printMessages()
    //Returns all the messages that have been sent while the program is running.
    public static String printMessages() {
        return Main.printMessages();
    }

    //Int: returnTotalMessages()
    //Returns the total number of messages that have been sent.
    public static int returnTotalMessages() {
        return Main.returnTotalMessages();
    }
    
    //This method writes the currently sent messages to messages.json.
    public static void storeMessage() {
        Main.saveMessagesToJson();
    }
}
