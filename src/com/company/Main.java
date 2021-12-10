package com.company;

// Imports all libraries needed for the program to run
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static ArrayList<String> bookDetails = new ArrayList<>(); // Inits a new arraylist to store file details
    private static File bookDetailsFile = new File("bookDetails.csv"); // Inits a new file object which stores all file data
    private static File userLogins = new File("userLogins.csv");

    public static void main(String[] args) {
        boolean loggedIn = loginAndSignIn();
        if (loggedIn) {
            boolean continueBookLoop = true;
            while (continueBookLoop) { // Allows a loop that continues until the user manually stops it
                System.out.println("Enter 'W' write to the file of book details\nEnter 'R' to read the file with book details\nEnter 'E' to edit a book's details\nEnter 'D' to delete files\nEnter 'L' to leave the system");
                String userOption = userInput().toUpperCase(); // Asks for user input and makes it uppercase so that it even if the user inputs the correct charater but in lowercase it gets recognised by the system
                switch (userOption) { // A switch case menu to call methods correlating to what the user has inputted
                    case "W":
                        writeToBookFile(true, true, 0, false, null); // Calls the method where the file is to be appended, a book is to be added, bookDetailsIndex is not needed, a book does not need to be deleted, there is no current book data to be parsed
                        break;
                    case "R":
                        readBookFile();
                        break;
                    case "E":
                        editBookDetails();
                        break;
                    case "D":
                        deleteBookFiles();
                        break;
                    case "L":
                        continueBookLoop = false;
                        break;
                    default:
                        System.out.println("Invalid Input");
                }
                System.out.print("\n");
            }
        }
    }

    public static String userInput() { // Short and simple user input method that returns input as a string
        Scanner userIn = new Scanner(System.in);
        return userIn.nextLine();
    }

    public static boolean loginAndSignIn() {
        boolean loggedIn = false;
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Enter 'L' to login or 'S' to signup:");
            String loginOrSignIn = userInput().toUpperCase();
            switch (loginOrSignIn) {
                case "S":
                    signUp();
                case "L":
                    loggedIn = login();
                    validInput = true;
                    break;

                default:
                    System.out.println("Not a valid Input!");
            }
        }
        return loggedIn;
    }

    public static boolean login() {
        int userDetailsIndex = -1;
        String[] userLogins = readFile(false);
        System.out.println("Enter your email address:");
        String userEmailAddress = userInput();
        System.out.println("Enter your password");
        String userPassword = userInput();
        for (int i = 0; i < userLogins.length; i++) {
            if (Objects.equals(userLogins[i], userEmailAddress)) {
                userDetailsIndex = i;
            }
        }
        if (userDetailsIndex == -1 || !userPassword.equals(userLogins[userDetailsIndex + 1])) {
            System.out.println("One or more of the inputs is invalid");
            return false;
        }
        else {
            return true;
        }
    }

    public static void signUp() {
        int userDetailsIndex = 0;
        String userEmailAddress;
        String userPassword;
        String[] userLogins = readFile(false);
        while (true) {
            System.out.println("Enter your email address:");
            userEmailAddress = userInput();
            for (int i = 0; i < userLogins.length; i++) {
                if (Objects.equals(userLogins[i], userEmailAddress)) {
                    userDetailsIndex = i;
                }
            }
            if (userDetailsIndex > -1) {
                break;
            }
            else if (userDetailsIndex == -1) {
                System.out.println("Email address already exists in the system");
                break;
            }
            else if (!userEmailAddress.contains("@") && !userEmailAddress.substring(userEmailAddress.length() - 6, userEmailAddress.length()).contains(".")) {
                System.out.println("Inputted value is invalid");
                break;
            }
        }
        while (true) {
            boolean passContainsLowerCaseValue = false;
            boolean passContainsUpperCaseValue = false;
            System.out.println("Password must be 8+ characters, and have upper and lower case characters\nEnter your password:");
            userPassword = userInput();
            for (int i = 0; i < userPassword.length(); i++) {
                if (Character.isLowerCase(userPassword.charAt(i))) {
                    passContainsLowerCaseValue = true;
                }
                else if (Character.isUpperCase(userPassword.charAt(i))) {
                    passContainsUpperCaseValue = true;
                }
            }
            if (userPassword.length() > 7 && passContainsUpperCaseValue && passContainsLowerCaseValue) {
                break;
            }
            else {
                System.out.println("Not a valid input");
            }
        }
        writeToUserLogins(userEmailAddress, userPassword);
    }

    public static void writeToUserLogins(String userEmail, String userPassword) {
        try {
            FileWriter myWriter = new FileWriter(userLogins.getName(), true);
            myWriter.write(userEmail + "," + userPassword + ",");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An Error Occurred");
        }
    }

    public static void editBookDetails() {
        String editedValue;
        boolean detailToEditValid = true; // Unless this becomes false the details will be edited
        String[] readableBookDetails = readFile(true); // Gathers all book data and gets put in a format the system can use
        int bookDetailToEditIndex = ISBNExists(readableBookDetails); // Ensures the inputted value is valid and then returns the first index postion of the book's details
        if (bookDetailToEditIndex > -1) { // Runs if the book exists
            System.out.println("Enter the detail of the book you want to edit - title (T); ISBN (I); author (A); genre (G)");
            String detailToEdit = userInput().toUpperCase(); // Asks for user input and makes it uppercase so that it even if the user inputs the correct charater but in lowercase it gets recognised by the system
            switch (detailToEdit) { // Switch case that changes index postion to be edited to the correct index position
                case "T":
                    break; // Ensures that input 'T' does not get put to default
                case "I":
                    bookDetailToEditIndex += 1;
                    break;
                case "A":
                    bookDetailToEditIndex += 2;
                    break;
                case "G":
                    bookDetailToEditIndex += 3;
                    break;
                default:
                    System.out.println("Invalid Input");
                    detailToEditValid = false;
            }
            if (detailToEditValid) {
                if (detailToEdit.equals("I")){
                    editedValue = enterISBN(); // If the ISBN is to be edited it must be validated
                }
                else {
                    System.out.println("Enter the new value:");
                    editedValue = userInput();
                }
                readableBookDetails[bookDetailToEditIndex] = editedValue; // Changes the original value in the array to the new value
                writeToBookFile(false, false, 0, false,readableBookDetails); // Calls the method to write to the book file, it will not append, it will not add a new book, the bookDetailsIndex is not needed, the book does not need to be deleted, the new book data is parsed
                System.out.println("Book details edited");
            }
        }
    }

    public static void deleteBookFiles() {
        String[] readableBookDetails = readFile(true); // Gathers all book data and gets put in a format the system can use
        int bookDetailsIndex = ISBNExists(readableBookDetails); // Ensures the inputted value is valid and then returns the first index postion of the book's details
        if (bookDetailsIndex > -1) {
            String doubleCheckDelete;
            System.out.println("Book found!\nAre you sure you want to delete " + readableBookDetails[bookDetailsIndex] + " by " + readableBookDetails[bookDetailsIndex + 2] + "?");
            System.out.println("Enter Y/N:");
            doubleCheckDelete = userInput(); // Prevents accidental book deletion
            if (doubleCheckDelete.equalsIgnoreCase("Y")) {
                writeToBookFile(false, false, bookDetailsIndex, true, null); // Calls the method where the file is not be appended, a new book isn't added, the book details index is parsed, a book needs to be deleted, there is no current book data to be parsed\
                System.out.println("Book deleted!");
            }
        }
    }

    public static int ISBNExists(String[] readableBookDetails) {
        String ISBNToRemove = enterISBN();
        for (int i = 0; i < readableBookDetails.length; i++) {
            if (readableBookDetails[i].equals(ISBNToRemove)) {
                return (i - 1);
            }
        }
        return -1;
    }

    public static void readBookFile() {
        String[] readableBookDetails = readFile(true);
        if (readableBookDetails.length < 4) {
            System.out.println("No books in the system!");
        } else {
            System.out.println("Here are our books:");
            for (int i = 0; i < readableBookDetails.length; i++) {
                System.out.println("Title: " + readableBookDetails[i] + "  ISBN: " + readableBookDetails[i + 1] + "  Author: " + readableBookDetails[i + 2] + "  Genre: " + readableBookDetails[i + 3]);
                i += 3;
            }
        }
    }

    public static String[] readFile(boolean accessBookDetails) {
        String[] readableDetails = new String[0];
        String data = "";
        Scanner fileReadInput;
        try {
            if (accessBookDetails) {
                fileReadInput = new Scanner(bookDetailsFile);
            }
            else {
                fileReadInput = new Scanner(userLogins);
            }
            if (fileReadInput.hasNextLine()) {
                data = fileReadInput.nextLine();
            }
            bookDetails.add(data);
            for (String currBookDetail : bookDetails) {
                readableDetails = currBookDetail.split(",");
            }
            return readableDetails;
        } catch (FileNotFoundException e) {
            System.out.println("Error occurred");
            return new String[]{""};
        }
    }

    public static void writeToBookFile(boolean append, boolean addNewBook, int bookDetailsIndex, boolean bookNeedsDeletion, String[] readableBookDetails) {
        if (readableBookDetails == null) {
            readableBookDetails = readFile(true);
        }
        try {
            FileWriter myWriter = new FileWriter(bookDetailsFile.getName(), append);
            if (addNewBook) {
                appendToBookFile(readableBookDetails, myWriter);
            } else {
                overwriteBookDetails(readableBookDetails, bookDetailsIndex, myWriter, bookNeedsDeletion);
            }
        } catch (IOException e) {
            System.out.println("Error Occurred");
        }
    }

    public static void appendToBookFile(String[] readableBookDetails, FileWriter myWriter) {
        String ISBN;
        boolean bookAlreadyExists = false;
        String title = enterTitle();
        ISBN = enterISBN();
        for (int i = 0; i < readableBookDetails.length; i++) {
            if (readableBookDetails[i].equals(ISBN)) {
                System.out.println("A book with this ISBN already exists on the system");
                bookAlreadyExists = true;
            }
        }
        if (!bookAlreadyExists) {
            String author = enterAuthor();
            String genre = enterGenre();
            try {
                myWriter.write(title + "," + ISBN + "," + author + "," + genre + ",");
                myWriter.close();
            } catch (IOException e) {
                System.out.println("Error Occurred");
            }
        }
    }

    public static void overwriteBookDetails(String[] readableBookDetails, int bookDetailsIndex, FileWriter myWriter, boolean bookNeedsDeletion) {
        bookDetails.clear();
        Collections.addAll(bookDetails, readableBookDetails);
        if (bookNeedsDeletion) {
            ArrayList<String> itemsToRemove = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                itemsToRemove.add(bookDetails.get(bookDetailsIndex + i));
            }
            bookDetails.removeAll(itemsToRemove);
        }
        try {
            for (String currBookDetail : bookDetails) {
                myWriter.write(currBookDetail + ",");
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error Occurred");
        }
    }

    public static String enterTitle() {
        String bookTitle;
        System.out.println("Enter the book title:");
        while (true) {
            bookTitle = userInput();
            if (bookTitle.isEmpty()) {
                System.out.println("Inputted value is invalid");
            } else {
                break;
            }
        }
        return bookTitle;
    }

    public static String enterISBN() {
        String ISBN;
        while (true) {
            try {
                System.out.println("Enter the book ISBN:");
                ISBN = userInput();
                boolean ISBNCorrect = checkDigit(ISBN);
                if (ISBN.length() != 13 || !ISBNCorrect) {
                    System.out.println("Inputted value is invalid");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Inputted value is invalid");
            }
        }
        return ISBN;
    }

    public static boolean checkDigit(String ISBN) {
        int total = findCheckDigitTotal(ISBN);
        if (total == 0) {
            total = 10;
        }
        if (ISBN.endsWith((Integer.toString(10 - total)))) {
            return true;
        }
        return false;
    }

    public static int findCheckDigitTotal(String ISBN) {
        int total = 0;
        for (int i = 0; i < (ISBN.length() - 1); i++) {
            if (i % 2 == 1) {
                total += Integer.parseInt(String.valueOf(ISBN.charAt(i))) * 3;
            } else {
                total += Integer.parseInt(String.valueOf(ISBN.charAt(i)));
            }
        }
        return (total % 10);
    }

    public static String enterAuthor() {
        String author;
        System.out.println("Enter the book's author:");
        while (true) {
            author = userInput();
            if (author.isEmpty()) {
                System.out.println("Inputted value is invalid");
            } else {
                break;
            }
        }
        return author;
    }

    public static String enterGenre() {
        String genre;
        System.out.println("Enter the book genre:");
        while (true) {
            genre = userInput();
            if (genre.isEmpty()) {
                System.out.println("Inputted value is invalid");
            } else {
                break;
            }
        }
        return genre;
    }
}