public class Gallows {
    public static void printGallows(int lives) {

        // base drawing
        char[][] board = { // use char because it's editable
                "   +---+  ".toCharArray(), // .toCharArray() converts string to char array
                "   |   |  ".toCharArray(), // [' ', ' ', ' ', '|', ' ', ' ', ' ']
                "       |  ".toCharArray(),
                "       |  ".toCharArray(),
                "       |  ".toCharArray(),
                "       |  ".toCharArray(),
                "========== ".toCharArray()
        };
        if (lives <= 5) board[2][3] = 'O';
        if (lives <= 4) board[3][3] = '|';
        if (lives <= 3) board[3][4] = '/';        // right arm
        if (lives <= 2) board[3][2] = '\\';
        if (lives <= 1) board[4][4] = '\\';        // left leg
        if (lives <= 0) board[4][2] = '/';       // right leg


        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col]);
            }
            System.out.println();
        }
        System.out.println();
    }
}












