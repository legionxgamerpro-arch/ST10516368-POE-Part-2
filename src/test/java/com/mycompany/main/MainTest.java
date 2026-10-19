package com.mycompany.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//Unit tests for every method in Main.java (JUnit 5)
public class MainTest {

    private PrintStream originalOut;
    private Scanner originalInput;
    private ByteArrayOutputStream capturedOut;

    //Runs before every test: gives each test a clean starting state
    @BeforeEach
    public void setUp() {
        originalOut = System.out;
        originalInput = Main.input;

        //Captures everything the program prints so it can be checked
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));

        //Resets the static variables in Main
        Main.messages.clear();
        Main.storedMessages.clear();
        Main.firstName = null;
        Main.lastName = null;
        Main.username = null;
        Main.password = null;
        Main.cellphone = null;
        Main.registeredUsername = null;
        Main.registeredPassword = null;
        Main.registeredCellPhone = null;
        Main.totalMessagesToEnter = 1;
        Main.messagesCreated = 0;
    }

    //Runs after every test: puts everything back and removes the JSON file
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        Main.input = originalInput;
        new File("messages.json").delete();
    }

    //Helper: gives Main a fake keyboard made from the lines supplied
    private void giveInput(String... lines) {
        Main.input = new Scanner(String.join("\n", lines) + "\n");
    }

    //Helper: returns everything printed so far
    private String output() {
        return capturedOut.toString();
    }

    //Helper: reads the messages.json file as text
    private String readJsonFile() throws Exception {
        return new String(Files.readAllBytes(new File("messages.json").toPath()),
                StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------
    // checkUserName
    // ------------------------------------------------------------------

    @Test
    public void testCheckUserName_valid() {
        assertTrue(Main.checkUserName("kyl_1"));   //exactly 5 characters
        assertTrue(Main.checkUserName("ab_"));
        assertTrue(Main.checkUserName("a_b_c"));
    }

    @Test
    public void testCheckUserName_noUnderscore() {
        assertFalse(Main.checkUserName("kyle1"));
    }

    @Test
    public void testCheckUserName_tooLong() {
        assertFalse(Main.checkUserName("kyle_smith"));
        assertFalse(Main.checkUserName("a_b_cd")); //6 characters
    }

    @Test
    public void testCheckUserName_empty() {
        assertFalse(Main.checkUserName(""));
    }

    // ------------------------------------------------------------------
    // checkPasswordComplexity
    // ------------------------------------------------------------------

    @Test
    public void testCheckPasswordComplexity_valid() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertTrue(Main.checkPasswordComplexity("Passw0rd!"));
    }

    @Test
    public void testCheckPasswordComplexity_tooShort() {
        assertFalse(Main.checkPasswordComplexity("Pa1!"));
    }

    @Test
    public void testCheckPasswordComplexity_noCapital() {
        assertFalse(Main.checkPasswordComplexity("password1!"));
    }

    @Test
    public void testCheckPasswordComplexity_noNumber() {
        assertFalse(Main.checkPasswordComplexity("Password!"));
    }

    @Test
    public void testCheckPasswordComplexity_noSpecialCharacter() {
        assertFalse(Main.checkPasswordComplexity("Password1"));
    }

    // ------------------------------------------------------------------
    // checkCellPhoneNumber
    // ------------------------------------------------------------------

    @Test
    public void testCheckCellPhoneNumber_valid() {
        assertTrue(Main.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneNumber_noInternationalCode() {
        assertFalse(Main.checkCellPhoneNumber("0838968976"));
        assertFalse(Main.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testCheckCellPhoneNumber_wrongLength() {
        assertFalse(Main.checkCellPhoneNumber("+2783896897"));   //8 digits
        assertFalse(Main.checkCellPhoneNumber("+278389689761")); //10 digits
    }

    @Test
    public void testCheckCellPhoneNumber_lettersAndSpaces() {
        assertFalse(Main.checkCellPhoneNumber("+27abcdefghi"));
        assertFalse(Main.checkCellPhoneNumber("+27 838968976"));
    }

    // ------------------------------------------------------------------
    // registerUser
    // ------------------------------------------------------------------

    @Test
    public void testRegisterUser_success() {
        giveInput("John", "Smith", "jo_h", "Passw0rd!", "+27838968976");

        String result = Main.registerUser();

        assertEquals("User registered successfully", result);
        assertEquals("John", Main.firstName);
        assertEquals("Smith", Main.lastName);
        assertEquals("jo_h", Main.registeredUsername);
        assertEquals("Passw0rd!", Main.registeredPassword);
        assertEquals("+27838968976", Main.registeredCellPhone);
    }

    @Test
    public void testRegisterUser_reasksUntilDetailsAreValid() {
        giveInput("John", "Smith",
                "bad", "toolong_user", "jo_h",   //two bad usernames, then good
                "weak", "Passw0rd!",             //one bad password, then good
                "0838968976", "+27838968976");   //one bad cellphone, then good

        String result = Main.registerUser();

        assertEquals("User registered successfully", result);
        assertEquals("jo_h", Main.registeredUsername);
        assertEquals("Passw0rd!", Main.registeredPassword);
        assertEquals("+27838968976", Main.registeredCellPhone);

        String printed = output();
        assertTrue(printed.contains("Username is not correctly formatted"));
        assertTrue(printed.contains("Password is not correctly formatted"));
        assertTrue(printed.contains("Cell phone number incorrectly formatted"));
    }

    // ------------------------------------------------------------------
    // loginUser
    // ------------------------------------------------------------------

    private void registerTestUser() {
        Main.registeredUsername = "jo_h";
        Main.registeredPassword = "Passw0rd!";
    }

    @Test
    public void testLoginUser_correctDetails() {
        registerTestUser();
        assertTrue(Main.loginUser("jo_h", "Passw0rd!"));
    }

    @Test
    public void testLoginUser_wrongPassword() {
        registerTestUser();
        assertFalse(Main.loginUser("jo_h", "WrongPass1!"));
    }

    @Test
    public void testLoginUser_wrongUsername() {
        registerTestUser();
        assertFalse(Main.loginUser("xx_x", "Passw0rd!"));
    }

    @Test
    public void testLoginUser_bothWrong() {
        registerTestUser();
        assertFalse(Main.loginUser("xx_x", "WrongPass1!"));
    }

    @Test
    public void testLoginUser_isCaseSensitive() {
        registerTestUser();
        assertFalse(Main.loginUser("JO_H", "Passw0rd!"));
        assertFalse(Main.loginUser("jo_h", "passw0rd!"));
    }

    @Test
    public void testLoginUser_returnsWithoutAskingForInput() {
        registerTestUser();
        //An empty keyboard would cause an error if loginUser tried to read from it
        giveInput();
        assertFalse(Main.loginUser("wrong", "wrong"));
        assertEquals("", output());
    }

    // ------------------------------------------------------------------
    // returnLoginStatus
    // ------------------------------------------------------------------

    @Test
    public void testReturnLoginStatus_success() {
        Main.firstName = "John";
        Main.lastName = "Smith";
        assertEquals("Welcome John Smith it is great to see you again.",
                Main.returnLoginStatus(true));
    }

    @Test
    public void testReturnLoginStatus_failure() {
        assertEquals("Username or password incorrect, please try again.",
                Main.returnLoginStatus(false));
    }

    // ------------------------------------------------------------------
    // checkMessageLength
    // ------------------------------------------------------------------

    @Test
    public void testCheckMessageLength_withinLimit() {
        assertTrue(Main.checkMessageLength("Hi"));
        assertTrue(Main.checkMessageLength(""));
        assertTrue(Main.checkMessageLength("a".repeat(250)));
    }

    @Test
    public void testCheckMessageLength_overLimit() {
        assertFalse(Main.checkMessageLength("a".repeat(251)));
    }

    // ------------------------------------------------------------------
    // createMessageID
    // ------------------------------------------------------------------

    @Test
    public void testCreateMessageID_isTenDigits() {
        String id = Main.createMessageID(1);
        assertEquals(10, id.length());
        assertTrue(id.matches("\\d{10}"));
    }

    @Test
    public void testCreateMessageID_startsWithMessageNumber() {
        assertTrue(Main.createMessageID(1).startsWith("01"));
        assertTrue(Main.createMessageID(9).startsWith("09"));
        assertTrue(Main.createMessageID(10).startsWith("10"));
        assertTrue(Main.createMessageID(99).startsWith("99"));
    }

    @Test
    public void testCreateMessageID_numberOver99WrapsToTwoDigits() {
        String id = Main.createMessageID(100);
        assertEquals(10, id.length());
        assertTrue(id.startsWith("00"));
    }

    // ------------------------------------------------------------------
    // createRandomMessageID (randomly generated 10-digit ID)
    // ------------------------------------------------------------------

    @Test
    public void testCreateRandomMessageID_isTenDigits() {
        String id = Main.createRandomMessageID();
        assertEquals(10, id.length());
        assertTrue(id.matches("\\d{10}"));
    }

    @Test
    public void testCreateRandomMessageID_isWithinValidRange() {
        for (int i = 0; i < 20; i++) {
            String id = Main.createRandomMessageID();
            long value = Long.parseLong(id);
            assertTrue(value >= 1000000000L && value <= 9999999999L);
        }
    }

    // ------------------------------------------------------------------
    // checkMessageID
    // ------------------------------------------------------------------

    @Test
    public void testCheckMessageID_valid() {
        assertTrue(Main.checkMessageID("0112345678"));  //exactly 10
        assertTrue(Main.checkMessageID("123"));
        assertTrue(Main.checkMessageID(""));
        assertTrue(Main.checkMessageID(Main.createMessageID(1)));
    }

    @Test
    public void testCheckMessageID_tooLong() {
        assertFalse(Main.checkMessageID("01123456789")); //11 characters
    }

    // ------------------------------------------------------------------
    // createMessageHash
    // ------------------------------------------------------------------

    @Test
    public void testCreateMessageHash_basic() {
        assertEquals("00:1:HITONIGHT",
                Main.createMessageHash("0012345678", 1, "Hi tonight"));
    }

    @Test
    public void testCreateMessageHash_removesPunctuation() {
        assertEquals("02:2:HELLOWORLD",
                Main.createMessageHash("0212345678", 2, "Hello, world!"));
    }

    @Test
    public void testCreateMessageHash_singleWordUsedAsFirstAndLast() {
        assertEquals("03:3:THANKSTHANKS",
                Main.createMessageHash("0312345678", 3, "Thanks"));
    }

    @Test
    public void testCreateMessageHash_ignoresExtraSpaces() {
        assertEquals("00:1:HITHERE",
                Main.createMessageHash("0012345678", 1, "  Hi   there  "));
    }

    @Test
    public void testCreateMessageHash_usesLongerMessageFirstAndLastWord() {
        assertEquals("01:1:HELLOTODAY",
                Main.createMessageHash("0112345678", 1, "Hello how are you today"));
    }

    @Test
    public void testCreateMessageHash_twoDigitMessageNumber() {
        assertEquals("10:10:AB",
                Main.createMessageHash("1012345678", 10, "a b"));
    }

    @Test
    public void testCreateMessageHash_emptyMessage() {
        assertEquals("00:1:",
                Main.createMessageHash("0012345678", 1, ""));
    }

    // ------------------------------------------------------------------
    // displayMessageDetails
    // ------------------------------------------------------------------

    @Test
    public void testDisplayMessageDetails() {
        String[] message = {"1", "0112345678", "01:1:HITONIGHT", "+27838968976", "Hi tonight"};

        Main.displayMessageDetails(message);

        String printed = output();
        assertTrue(printed.contains("Message ID: 0112345678"));
        assertTrue(printed.contains("Message Hash: 01:1:HITONIGHT"));
        assertTrue(printed.contains("Recipient: +27838968976"));
        assertTrue(printed.contains("Message: Hi tonight"));
    }

    // ------------------------------------------------------------------
    // escapeJson
    // ------------------------------------------------------------------

    @Test
    public void testEscapeJson_plainTextUnchanged() {
        assertEquals("Hello world", Main.escapeJson("Hello world"));
    }

    @Test
    public void testEscapeJson_escapesQuotes() {
        assertEquals("say \\\"hi\\\"", Main.escapeJson("say \"hi\""));
    }

    @Test
    public void testEscapeJson_escapesBackslashes() {
        assertEquals("a\\\\b", Main.escapeJson("a\\b"));
    }

    // ------------------------------------------------------------------
    // saveMessagesToJson
    // ------------------------------------------------------------------

    @Test
    public void testSaveMessagesToJson_writesMessage() throws Exception {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});

        Main.saveMessagesToJson();

        assertTrue(new File("messages.json").exists());
        assertTrue(output().contains("Messages saved to messages.json"));

        String json = readJsonFile();
        assertTrue(json.contains("\"messageNumber\": \"1\""));
        assertTrue(json.contains("\"messageID\": \"0112345678\""));
        assertTrue(json.contains("\"messageHash\": \"01:1:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27838968976\""));
        assertTrue(json.contains("\"message\": \"Hi tonight\""));
        assertTrue(json.contains("\"status\": \"sent\""));
    }

    @Test
    public void testSaveMessagesToJson_commaBetweenMessagesOnly() throws Exception {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});
        Main.messages.add(new String[]{"2", "0212345678", "02:2:HELLOWORLD",
            "+27838968976", "Hello world"});

        Main.saveMessagesToJson();

        String json = readJsonFile();
        assertTrue(json.contains("  },\n  {"));          //comma between the two
        assertTrue(json.contains("  }\n]"));             //no comma after the last
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void testSaveMessagesToJson_escapesSpecialCharacters() throws Exception {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:SAYHI",
            "+27838968976", "say \"hi\""});

        Main.saveMessagesToJson();

        assertTrue(readJsonFile().contains("\"message\": \"say \\\"hi\\\"\""));
    }

    @Test
    public void testSaveMessagesToJson_noMessages() throws Exception {
        Main.saveMessagesToJson();
        assertEquals("[\n]", readJsonFile());
    }

    @Test
    public void testSaveMessagesToJson_includesStoredMessages() throws Exception {
        Main.storedMessages.add(new String[]{"0", "0012345678", "00:0:HITONIGHT",
            "+27838968976", "Hi tonight"});

        Main.saveMessagesToJson();

        String json = readJsonFile();
        assertTrue(json.contains("\"message\": \"Hi tonight\""));
        assertTrue(json.contains("\"status\": \"stored\""));
    }

    // ------------------------------------------------------------------
    // returnTotalMessages
    // ------------------------------------------------------------------

    @Test
    public void testReturnTotalMessages_empty() {
        assertEquals(0, Main.returnTotalMessages());
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testReturnTotalMessages_afterAdding() {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});
        Main.messages.add(new String[]{"2", "0212345678", "02:2:HELLOWORLD",
            "+27111222333", "Hello world"});

        assertEquals(2, Main.returnTotalMessages());
        assertEquals(2, Message.returnTotalMessages());
    }

    // ------------------------------------------------------------------
    // printMessages 
    // ------------------------------------------------------------------

    @Test
    public void testPrintMessages_empty() {
        assertEquals("No messages have been sent yet.", Main.printMessages());
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }

    @Test
    public void testPrintMessages_containsDetails() {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});

        String result = Main.printMessages();
        assertTrue(result.contains("Message ID: 0112345678"));
        assertTrue(result.contains("Message Hash: 01:1:HITONIGHT"));
        assertTrue(result.contains("Recipient: +27838968976"));
        assertTrue(result.contains("Message: Hi tonight"));
    }

    // ------------------------------------------------------------------
    // Message class methods 
    // ------------------------------------------------------------------

    @Test
    public void testMessage_checkMessageID() {
        assertTrue(Message.checkMessageID("0112345678"));
        assertTrue(Message.checkMessageID("123"));
        assertFalse(Message.checkMessageID("01123456789")); //11 chars
        assertFalse(Message.checkMessageID(null));
    }

    @Test
    public void testMessage_checkRecipientCell_success() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27838968976"));
    }

    @Test
    public void testMessage_checkRecipientCell_failure() {
        String result = Message.checkRecipientCell("0838968976");
        assertTrue(result.contains("incorrectly formatted")
                || result.contains("international code"));
    }

    @Test
    public void testMessage_createMessageHash() {
        assertEquals("00:1:HITONIGHT",
                Message.createMessageHash("0012345678", 1, "Hi tonight"));
    }

    @Test
    public void testMessage_SentMessage_send() {
        String[] message = {"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"};
        giveInput("1"); // choose Send

        String result = Message.SentMessage(message);

        assertEquals("Message successfully sent.", result);
        assertEquals(1, Main.messages.size());
        assertEquals(0, Main.storedMessages.size());
    }

    @Test
    public void testMessage_SentMessage_disregard() {
        String[] message = {"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"};
        giveInput("2"); // choose Disregard

        String result = Message.SentMessage(message);

        assertEquals("Press 0 to delete the message.", result);
        assertEquals(0, Main.messages.size());
        assertEquals(0, Main.storedMessages.size());
    }

    @Test
    public void testMessage_SentMessage_store() {
        String[] message = {"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"};
        giveInput("3"); // choose Store

        String result = Message.SentMessage(message);

        assertEquals("Message successfully stored.", result);
        assertEquals(0, Main.messages.size());
        assertEquals(1, Main.storedMessages.size());
    }

    @Test
    public void testMessage_storeMessage() throws Exception {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});

        Message.storeMessage();

        assertTrue(new File("messages.json").exists());
        assertTrue(readJsonFile().contains("\"message\": \"Hi tonight\""));
    }

    // ------------------------------------------------------------------
    // sendMessages
    // ------------------------------------------------------------------

    @Test
    public void testSendMessages_oneMessage() throws Exception {
        //The quota is set to one in setUp(); provide recipient, message, then choice=1 (Send).
        Main.totalMessagesToEnter = 1;
        giveInput("+27838968976", "Hi tonight", "1");

        Main.sendMessages();

        assertEquals(1, Main.messages.size());
        String[] stored = Main.messages.get(0);
        assertEquals("0", stored[0]);
        assertEquals(10, stored[1].length());
        assertTrue(stored[1].matches("\\d{10}"));
        assertEquals("00:0:HITONIGHT", stored[2]);
        assertEquals("+27838968976", stored[3]);
        assertEquals("Hi tonight", stored[4]);

        String printed = output();
        assertTrue(printed.contains("Message ready to send."));
        assertTrue(printed.contains("Message ID: " + stored[1]));
        assertTrue(printed.contains("Message successfully sent."));
        assertTrue(printed.contains("Total number of messages sent: 1"));
        assertTrue(new File("messages.json").exists());
    }

    @Test
    public void testSendMessages_twoMessagesAreNumberedInOrder() {
        //Set the message quota to two; send two messages in order.
        Main.totalMessagesToEnter = 2;
        giveInput("+27838968976", "Hi tonight", "1",
                "+27111222333", "Hello world", "1");

        Main.sendMessages();

        assertEquals(2, Main.messages.size());
        assertEquals("0", Main.messages.get(0)[0]);
        assertEquals("1", Main.messages.get(1)[0]);
        assertEquals("00:0:HITONIGHT", Main.messages.get(0)[2]);
        assertEquals("01:1:HELLOWORLD", Main.messages.get(1)[2]);
        assertEquals("+27111222333", Main.messages.get(1)[3]);
        assertTrue(output().contains("Total number of messages sent: 2"));
    }

    @Test
    public void testSendMessages_reasksForInvalidInput() {
        //The quota is configured by QuickChat; this test focuses on recipient/message validation.
        Main.totalMessagesToEnter = 1;
        giveInput("0838968976", "+27838968976", //bad recipient, then good
                "",                             //empty message
                "a".repeat(251),                //too long by 1
                "Hi tonight",                   //good message
                "1");                           //Send

        Main.sendMessages();

        String printed = output();
        assertTrue(printed.contains("Cell phone number is incorrectly formatted"));
        assertTrue(printed.contains("Cell phone number successfully captured."));
        assertTrue(printed.contains("The message cannot be empty."));
        assertTrue(printed.contains("Message exceeds 250 characters by 1; please reduce the size."));

        assertEquals(1, Main.messages.size());
        assertEquals("Hi tonight", Main.messages.get(0)[4]);
    }

    @Test
    public void testSendMessages_disregardDoesNotAddToSentList() {
        Main.totalMessagesToEnter = 1;
        giveInput("+27838968976", "Hi tonight", "2"); // Disregard

        Main.sendMessages();

        assertEquals(0, Main.messages.size());
        assertTrue(output().contains("Press 0 to delete the message."));
        assertTrue(output().contains("Total number of messages sent: 0"));
        assertEquals(1, Main.messagesCreated);
    }

    @Test
    public void testSendMessages_storeAddsToStoredList() throws Exception {
        Main.totalMessagesToEnter = 1;
        giveInput("+27838968976", "Hi tonight", "3"); // Store

        Main.sendMessages();

        assertEquals(0, Main.messages.size());
        assertEquals(1, Main.storedMessages.size());
        assertTrue(output().contains("Message successfully stored."));
        assertEquals(1, Main.messagesCreated);
        assertTrue(readJsonFile().contains("\"status\": \"stored\""));
    }

    // ------------------------------------------------------------------
    // showMessages
    // ------------------------------------------------------------------

    @Test
    public void testShowMessages_empty() {
        Main.showMessages();
        assertTrue(output().contains("No messages have been sent yet."));
    }

    @Test
    public void testShowMessages_displaysEveryMessage() {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITONIGHT",
            "+27838968976", "Hi tonight"});
        Main.messages.add(new String[]{"2", "0212345678", "02:2:HELLOWORLD",
            "+27111222333", "Hello world"});

        Main.showMessages();

        String printed = output();
        assertFalse(printed.contains("No messages have been sent yet."));
        assertTrue(printed.contains("Message ID: 0112345678"));
        assertTrue(printed.contains("Message Hash: 02:2:HELLOWORLD"));
        assertTrue(printed.contains("Recipient: +27111222333"));
        assertTrue(printed.contains("Message: Hi tonight"));
    }

    // ------------------------------------------------------------------
    // runQuickChat
    // ------------------------------------------------------------------

    @Test
    public void testRunQuickChat_quitImmediately() {
        giveInput("1", "3"); //quota, then Quit

        Main.runQuickChat();

        String printed = output();
        assertTrue(printed.contains("Welcome to QuickChat."));
        assertTrue(printed.contains("1) Send Messages"));
        assertTrue(printed.contains("2) Show recently sent messages"));
        assertTrue(printed.contains("3) Quit"));
        assertTrue(printed.contains("Thank you for using QuickChat. Goodbye!"));
    }

    @Test
    public void testRunQuickChat_invalidOptionsThenQuit() {
        giveInput("1", "9", "abc", "3"); //quota, invalid menu options, then Quit

        Main.runQuickChat();

        assertTrue(output().contains("Invalid option, please enter 1, 2 or 3."));
        assertTrue(output().contains("Goodbye!"));
    }

    @Test
    public void testRunQuickChat_showMessagesOption_ComingSoon() {
        //Part 2 requirement: option 2 must display "Coming Soon."
        giveInput("1", "2", "3"); //quota, Coming Soon option, then Quit

        Main.runQuickChat();

        assertTrue(output().contains("Coming Soon."));
        //The old "No messages..." text must NOT appear for menu option 2
        assertFalse(output().contains("No messages have been sent yet."));
    }

    @Test
    public void testRunQuickChat_reasksForInvalidMessageCount() {
        giveInput("0", "abc", "1", "3"); //invalid quotas, valid quota, then Quit

        Main.runQuickChat();

        String printed = output();
        assertTrue(printed.contains("Please enter a whole number of 1 or more."));
        assertTrue(printed.contains("How many messages do you want to enter?"));
        assertTrue(printed.contains("Goodbye!"));
    }

    @Test
    public void testRunQuickChat_sendMessagesOption() {
        //1 = quota, 1 = Send Messages, then recipient, message, send, and Quit
        giveInput("1", "1", "+27838968976", "Hello world", "1", "3");

        Main.runQuickChat();

        assertEquals(1, Main.messages.size());
        assertEquals("Hello world", Main.messages.get(0)[4]);
        assertTrue(output().contains("Goodbye!"));
        assertTrue(output().contains("Message successfully sent."));
    }

    // ------------------------------------------------------------------
    // main
    // ------------------------------------------------------------------

    @Test
    public void testMain_registerThenLoginRetryThenQuit() {
        giveInput("John", "Smith", "jo_h", "Passw0rd!", "+27838968976", //registration
                "jo_h", "WrongPass1!",                                   //failed login
                "jo_h", "Passw0rd!",                                     //successful login
                "1", "3");                                               //quota, then quit QuickChat

        Main.main(new String[0]);

        String printed = output();
        assertTrue(printed.contains("User registered successfully"));
        assertTrue(printed.contains("Username or password incorrect, please try again."));
        assertTrue(printed.contains("Welcome John Smith it is great to see you again."));
        assertTrue(printed.contains("Welcome to QuickChat."));
        assertTrue(printed.contains("Goodbye!"));
    }

    // ------------------------------------------------------------------
    // Additional unit tests required by the Part 2 brief (Test Data)
    // ------------------------------------------------------------------

    @Test
    public void testMessageLength_AssertEquals_success() {
        //Success: “Message ready to send.”
        assertTrue(Main.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testMessageLength_AssertEquals_failure() {
        //Failure: message exceeds 250 characters
        String longMessage = "a".repeat(251);
        assertFalse(Main.checkMessageLength(longMessage));
    }

    @Test
    public void testRecipientNumber_correctlyFormatted() {
        //Success: “Cell phone number successfully captured.”
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientNumber_incorrectlyFormatted() {
        //Failure message from the brief
        String result = Message.checkRecipientCell("08575975889");
        assertTrue(result.contains("incorrectly formatted")
                || result.contains("international code"));
    }

    @Test
    public void testMessageHash_correct() {
        //The system should return 00:0:HITONIGHT when supplied with the data
        //from Test Case 1 style (first two of ID, message number, first+last word)
        assertEquals("00:0:HITONIGHT",
                Main.createMessageHash("0012345678", 0, "Hi tonight"));
    }
}
