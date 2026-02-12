import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class HangmanGame {
    public static void main(String[] args) {
        int counter = 0;
        //Would be better to use an Arraylist, but using an array to learn
        // 1. -- First pass to count the number of lines (words) to determine array size
        try (BufferedReader reader = new BufferedReader(new FileReader("words.txt"))) { // Opens the file
            // New FileReader("/Users/chrisfitzgerald/IdeaProjectso/HangmanGame/words.txt");
            // -- Absolute path, but not portable. Using relative path instead.

            String line;
            while ((line = reader.readLine()) != null) { //Read line by line until end of file
                counter++;
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        String [] wordList = new String[counter]; // Array to hold filewords -- size is number of lines in file
        // Check if list is empty before proceeding to fill the array
        if (counter == 0) {
            System.out.println("No words found in words.txt. Exiting game.");
            return;
        }


        // 2. -- Second pass to fill the array with words
        try (BufferedReader reader = new BufferedReader(new FileReader("words.txt"))) {
            int index = 0;
            String line;
            while ((line = reader.readLine()) != null) { // Read line by line until end of file
                wordList[index] = line; // Fill the array with words from the file
                index++;
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        int randomIndex = (int)(Math.random() * counter); // Random * array length to get random index
        String secretWord = wordList[randomIndex].toLowerCase(); // Select a random word from the array

        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Hangman!");
        System.out.println("Enter your name:");
        String playerName = scan.nextLine();
        int score = 0; // Initialize score variable

        char[] currentProgress = new char[secretWord.length()];
        for (int i = 0; i < currentProgress.length; i++) {
            currentProgress[i] = '_';
        }


        int lives = 6;//Track lives
        while (lives > 0) {
            // Print the progress
            for (char c : currentProgress) { // Print each character in current progress
                System.out.print(c + " ");
            }
            System.out.println();
            System.out.println("Lives remaining: " + lives);

            // Ask for a guess
            System.out.println("\nEnter your guess:");
            String guess = scan.nextLine();
            if (guess.isEmpty()) {
                System.out.println("Please enter at least one character.");
                continue; // skip to next loop iteration
            }
            char guessedLetter = guess.toLowerCase().charAt(0);
            // not case sensitive, take first character


            // Check if the letter is in the secret word
            if (secretWord.indexOf(guessedLetter) >= 0) { // Letter is in the word
                System.out.println("Correct guess!");
                for (int i = 0; i < secretWord.length(); i++) {
                    if (secretWord.charAt(i) == guessedLetter) {
                        currentProgress[i] = guessedLetter; // Update current progress
                    }
                }
            } else {
                System.out.println("Wrong guess!");
                lives--;
            }

            // Check if the user has guessed the full word
            if (new String(currentProgress).equals(secretWord)) {
                System.out.println("Congratulations! You've guessed the word!");
                System.out.println(("Your score: " + score + "has been added to the leaderboard!"));
                break;  // Exit the while loop
            }
        }

        // After loop finishes
        if (lives == 0) {
            System.out.println("Game Over! The secret word was: " + secretWord);
        }
        scan.close();
    }
    // Method converts Text file → Objects in memory
    private static ArrayList<HighScore> loadHighScores(String fileName) {
        ArrayList<HighScore> scores = new ArrayList<>(); // Holds all score objects loaded from file

        //Checks if file exists
        File file = new File(fileName);
        if (!file.exists()) {
            return scores; // No file yet -> no scores
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) { // Opens the file
            String line;
            while ((line = reader.readLine()) != null) { // Read line by line until end of file
                String[] parts = line.split(","); // Splits line into 2 parts: name and score
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim()); //Converts score from String to int
                    scores.add(new HighScore(name, score)); // Creates new HighScore object
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading highscores: " + e.getMessage());
        }

        return scores; // Returns the list of HighScore objects loaded from the file
    }


    // Saves the current list of HighScore objects to highscores.txt (overwrites file)
    private static void saveHighScores(String fileName, ArrayList<HighScore> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (HighScore hs : scores) {
                writer.write(hs.getName() + "," + hs.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving highscores: " + e.getMessage());
        }
    }
    // loadHighScores() = disk → memory
    // saveHighScores() = memory → disk
    // * This is the data flow


    private static void addScoreAndKeepTop5(ArrayList<HighScore> scores,
                                            String playerName,
                                            int score) {

        // Add the new score
        scores.add(new HighScore(playerName, score));

        // Sort descending (highest score first)
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        // (a, b) -> Integer - lambda expression (setting up comparison)
        // Integer.compare.. - compares two integers and returns a value indicating their order
        // b then a = descending | a then b = ascending

        while (scores.size() > 5) {
        // while loop because sometimes 6 entries → remove 1 | 7 entries → remove 2

            scores.remove(scores.size() - 1);
            // Removes the last element in the list (lowest score)
        }
    }


}

