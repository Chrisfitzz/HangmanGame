import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class HangmanGame {

    private static final String WORDS_FILE = "words.txt";
    private static final String HIGH_SCORE_FILE = "highscores.txt";
    private static final int MAX_LIVES = 6;

    public static void main(String[] args) {

        // 1. Load words
        List<String> words = loadWords(WORDS_FILE);
        if (words.isEmpty()) {
            System.out.println("No words found in " + WORDS_FILE + ". Exiting game.");
            return;
        }

        Scanner scan = new Scanner(System.in);

        // 2. Get player name
        System.out.println("Welcome to Hangman!");
        System.out.println("Enter your name:");
        String playerName = scan.nextLine();

        // 3. Pick a random word
        String secretWord = chooseRandomWord(words).toLowerCase();

        // 4. Play one round and get score
        int score = playRound(secretWord, scan);

        // 5. High scores
        handleHighScores(playerName, score);

        scan.close();
    }

    // -------- WORD LOADING --------

    private static List<String> loadWords(String fileName) {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading words file: " + e.getMessage());
        }

        return words;
    }

    private static String chooseRandomWord(List<String> words) {
        int randomIndex = (int) (Math.random() * words.size());
        return words.get(randomIndex);
    }

    // -------- GAMEPLAY --------

    private static int playRound(String secretWord, Scanner scan) {
        int lives = MAX_LIVES;
        int score = 0;

        char[] currentProgress = initProgress(secretWord);
        Set<Character> guessedLetters = new HashSet<>();

        while (lives > 0) {

            // Show current state (gallows + word + lives)
            displayGameState(lives, currentProgress, guessedLetters);

            // Get a valid guess
            char guessedLetter = readGuess(scan);

            // Already guessed → no life lost
            if (guessedLetters.contains(guessedLetter)) {
                System.out.println("You've already guessed '" + guessedLetter + "'. Try a different letter.");
                continue;
            }

            // Record this new guess
            guessedLetters.add(guessedLetter);

            // Show guessed letters (now including this one)
            printGuessedLetters(guessedLetters);

            // Apply the guess
            boolean correct = applyGuess(secretWord, guessedLetter, currentProgress);
            if (correct) {
                System.out.println("Correct guess!");
            } else {
                System.out.println("Wrong guess!");
                lives--;
            }

            // Check for win
            if (isWordGuessed(currentProgress, secretWord)) {
                System.out.println("Congratulations! You've guessed the word!");
                score = lives * 10;
                System.out.println("Your score: " + score + " has been added to the leaderboard!");
                break;
            }
        }

        if (lives == 0) {
            Gallows.printGallows(lives);
            System.out.println("Game Over! The secret word was: " + secretWord);
        }

        return score;
    }

    private static char[] initProgress(String secretWord) {
        char[] progress = new char[secretWord.length()];
        for (int i = 0; i < progress.length; i++) {
            progress[i] = '_';
        }
        return progress;
    }

    private static void displayGameState(int lives,
                                         char[] currentProgress,
                                         Set<Character> guessedLetters) {

        Gallows.printGallows(lives);

        // Show current word progress
        for (char c : currentProgress) {
            System.out.print(c + " ");
        }
        System.out.println();

        // Show remaining lives
        System.out.println("Lives remaining: " + lives);
    }

    private static char readGuess(Scanner scan) {
        while (true) {
            System.out.println("\nEnter your guess:");
            String guess = scan.nextLine();
            if (guess.isEmpty()) {
                System.out.println("Please enter at least one character.");
                continue;
            }
            return Character.toLowerCase(guess.charAt(0));
        }
    }

    private static void printGuessedLetters(Set<Character> guessedLetters) {
        System.out.print("Guessed letters: ");
        if (guessedLetters.isEmpty()) {
            System.out.println("none yet");
        } else {
            for (char c : guessedLetters) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    private static boolean applyGuess(String secretWord,
                                      char guessedLetter,
                                      char[] currentProgress) {
        boolean found = false;
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == guessedLetter) {
                currentProgress[i] = guessedLetter;
                found = true;
            }
        }
        return found;
    }

    private static boolean isWordGuessed(char[] currentProgress, String secretWord) {
        return new String(currentProgress).equals(secretWord);
    }

    // -------- HIGH SCORE HANDLING --------

    private static void handleHighScores(String playerName, int score) {
        ArrayList<HighScore> highScores = loadHighScores(HIGH_SCORE_FILE);
        addScoreAndKeepTop5(highScores, playerName, score);
        System.out.println("Saving to: " + new File(HIGH_SCORE_FILE).getAbsolutePath());
        saveHighScores(HIGH_SCORE_FILE, highScores);

        // Print leaderboard
        System.out.println("\n* - - - - TOP 5 LEADERBOARD - - - - *");
        for (int i = 0; i < highScores.size(); i++) {
            HighScore hs = highScores.get(i);
            System.out.println((i + 1) + ". " + hs.getName() + " - " + hs.getScore());
        }
        System.out.println("* - - - - - - - - - *");
    }

    private static ArrayList<HighScore> loadHighScores(String fileName) {
        ArrayList<HighScore> scores = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    scores.add(new HighScore(name, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading highscores: " + e.getMessage());
        }

        return scores;
    }

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

    private static void addScoreAndKeepTop5(ArrayList<HighScore> scores,
                                            String playerName,
                                            int score) {

        scores.add(new HighScore(playerName, score));

        // Sort descending (highest score first)
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // Keep only top 5
        while (scores.size() > 5) {
            scores.remove(scores.size() - 1);
        }
    }
}