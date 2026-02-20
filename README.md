# Hangman Game (Java)

A simple console-based Hangman game written in Java.
This project started as a practice exercise and grew into a more structured game with cleaner code, separate classes, and a small leaderboard system.

## Features

Random word selection from a words.txt file

ASCII gallows that update as you lose lives

Tracks guessed letters and prevents losing a life on repeats

Shows the player’s progress each turn

Simple scoring system

Stores top 5 high scores in highscores.txt

Refactored code with helper methods and separate classes (e.g., Gallows, HighScore)

## How to Run

Compile the files:

```

javac src/*.java

```

Then run the game:

```

java src/HangmanGame


```

Make sure you have:

words.txt in the project folder

write permission for highscores.txt (it will be created automatically)

## Project Structure

```

src/
  HangmanGame.java
  Gallows.java
  HighScore.java
words.txt
highscores.txt (auto-generated)
Notes

```

highscores.txt is ignored by Git since it’s player-specific and changes every game.

This project is mainly for learning Java fundamentals, file I/O, collections, and cleaner program structure.

## Future Ideas

Add a streak setting to create more variations in scores

Add difficulty settings

Add “play again” without restarting

Maybe switch to a GUI version later
