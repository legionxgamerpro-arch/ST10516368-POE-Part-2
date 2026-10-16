package com.mycompany.main;

//Imports the JUnit 5 tools used to write and run the tests
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Imports the tools used to read and delete the JSON file
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//Unit tests for the methods in the Main class
public class MainTest {

    //Clears the stored messages before every test
    @BeforeEach
    public void setUp() {
        Main.messages.clear();
    }

    //Removes the JSON file and the stored messages after every test
    @AfterEach
    public void cleanUp() {
        Main.messages.clear();
        new File("messages.json").delete();
    }

    //PART 1 TESTS

    //checkUserName tests
    @Test
    public void testCheckUserNameCorrect() {
        assertTrue(Main.checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserNameNoUnderscore() {
        assertFalse(Main.checkUserName("kyle"));
    }

    @Test
    public void testCheckUserNameTooLong() {
        assertFalse(Main.checkUserName("kyle_!!!"));
    }

    //checkPasswordComplexity tests
    @Test
    public void testCheckPasswordCorrect() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testCheckPasswordTooShort() {
        assertFalse(Main.checkPasswordComplexity("Pass1!"));
    }

    @Test
    public void testCheckPasswordNoCapital() {
        assertFalse(Main.checkPasswordComplexity("password1!"));
    }

    @Test
    public void testCheckPasswordNoNumber() {
        assertFalse(Main.checkPasswordComplexity("Password!"));
    }

    @Test
    public void testCheckPasswordNoSpecialCharacter() {
        assertFalse(Main.checkPasswordComplexity("Passw0rd"));
    }

    //checkCellPhoneNumber tests
    @Test
    public void testCheckCellPhoneCorrect() {
        assertTrue(Main.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneNoInternationalCode() {
        assertFalse(Main.checkCellPhoneNumber("0838968976"));
    }

    @Test
    public void testCheckCellPhoneTooShort() {
        assertFalse(Main.checkCellPhoneNumber("+2783896897"));
    }

    @Test
    public void testCheckCellPhoneTooLong() {
        assertFalse(Main.checkCellPhoneNumber("+278389689761"));
    }

    //loginUser test (correct details return true straight away, so no input is needed)
    @Test
    public void testLoginUserCorrectDetails() {
        Main.registeredUsername = "kyl_1";
        Main.registeredPassword = "Ch&&sec@ke99!";
        assertTrue(Main.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    //returnLoginStatus tests
    @Test
    public void testReturnLoginStatusSuccessful() {
        Main.firstName = "John";
        Main.lastName = "Smith";
        assertEquals("Welcome John Smith it is great to see you again.",
                Main.returnLoginStatus(true));
    }

    @Test
    public void testReturnLoginStatusFailed() {
        assertEquals("Username or password incorrect, please try again.",
                Main.returnLoginStatus(false));
    }

    //PART 2 TESTS

    //checkMessageLength tests
    @Test
    public void testCheckMessageLengthShortMessage() {
        assertTrue(Main.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testCheckMessageLengthExactly250() {
        String message = "a".repeat(250);
        assertTrue(Main.checkMessageLength(message));
    }

    @Test
    public void testCheckMessageLengthTooLong() {
        String message = "a".repeat(251);
        assertFalse(Main.checkMessageLength(message));
    }

    //createMessageID tests
    @Test
    public void testCreateMessageIDIsTenDigits() {
        String id = Main.createMessageID(1);
        assertEquals(10, id.length());
        assertTrue(id.matches("[0-9]{10}"));
    }

    @Test
    public void testCreateMessageIDStartsWithLoopCounter() {
        assertTrue(Main.createMessageID(1).startsWith("01"));
        assertTrue(Main.createMessageID(2).startsWith("02"));
        assertTrue(Main.createMessageID(12).startsWith("12"));
    }

    //checkMessageID tests
    @Test
    public void testCheckMessageIDCorrect() {
        assertTrue(Main.checkMessageID("0112345678"));
    }

    @Test
    public void testCheckMessageIDTooLong() {
        assertFalse(Main.checkMessageID("01123456789"));
    }

    //createMessageHash tests
    @Test
    public void testCreateMessageHashFirstMessage() {
        assertEquals("01:1:HITONIGHT",
                Main.createMessageHash("0112345678", 1,
                        "Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testCreateMessageHashSecondMessage() {
        assertEquals("02:2:HITHANKS",
                Main.createMessageHash("0212345678", 2, "Hi, thanks!"));
    }

    @Test
    public void testCreateMessageHashSingleWord() {
        assertEquals("03:3:HELLOHELLO",
                Main.createMessageHash("0312345678", 3, "Hello"));
    }

    @Test
    public void testCreateMessageHashIgnoresExtraSpaces() {
        assertEquals("01:1:HITHANKS",
                Main.createMessageHash("0112345678", 1, "   Hi   there   thanks  "));
    }

    //escapeJson tests
    @Test
    public void testEscapeJsonQuotationMarks() {
        assertEquals("She said \\\"ok\\\"", Main.escapeJson("She said \"ok\""));
    }

    @Test
    public void testEscapeJsonBackslash() {
        assertEquals("a\\\\b", Main.escapeJson("a\\b"));
    }

    @Test
    public void testEscapeJsonPlainText() {
        assertEquals("Hello", Main.escapeJson("Hello"));
    }

    //saveMessagesToJson test
    //Stores one message, saves it, then checks the JSON file contains the details
    @Test
    public void testSaveMessagesToJson() throws IOException {
        String[] message = {"1", "0112345678", "01:1:HITHANKS", "+27838968976", "Hi, thanks!"};
        Main.messages.add(message);

        Main.saveMessagesToJson();

        File file = new File("messages.json");
        assertTrue(file.exists());

        String json = Files.readString(file.toPath());
        assertTrue(json.contains("\"messageID\": \"0112345678\""));
        assertTrue(json.contains("\"messageHash\": \"01:1:HITHANKS\""));
        assertTrue(json.contains("\"recipient\": \"+27838968976\""));
        assertTrue(json.contains("\"message\": \"Hi, thanks!\""));
    }

    //Checks that two messages are both saved in the file
    @Test
    public void testSaveMessagesToJsonTwoMessages() throws IOException {
        Main.messages.add(new String[]{"1", "0112345678", "01:1:HITHANKS", "+27838968976", "Hi, thanks!"});
        Main.messages.add(new String[]{"2", "0212345678", "02:2:OKBYE", "+27721112222", "Ok bye"});

        Main.saveMessagesToJson();

        String json = Files.readString(new File("messages.json").toPath());
        assertTrue(json.contains("01:1:HITHANKS"));
        assertTrue(json.contains("02:2:OKBYE"));
    }
}
