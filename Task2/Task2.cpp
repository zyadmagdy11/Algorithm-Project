#include <iostream>
#include <climits>
using namespace std;

// Possible knight moves
int dx[] = {2, 1, -1, -2, -2, -1, 1, 2};
int dy[] = {1, 2, 2, 1, -1, -2, -2, -1};



// Utility function to check if the current position is inside the board
bool isSafe(int x, int y, char** board, int n) 
{
    return (x >= 0 && x < n && y >= 0 && y < n && board[x][y] == 'X');
}

// Function to count the number of available moves from a given position
int countAvailableMoves(int x, int y, char** board, int n) 
{
    int count = 0;
    for (int i = 0; i < 8; i++) 
    {
        int newX = x + dx[i];
        int newY = y + dy[i];
        if (isSafe(newX, newY, board, n)) 
        {
            count++;
        }
    }
    return count;
}

bool getNextMove(int* x, int* y, char** board, int n) 
{
    int minDegIdx = -1;
    int minDeg = INT_MAX;
    int newX, newY;

    // Check all 8 possible moves and apply Warnsdorff's rule
    for (int i = 0; i < 8; i++) 
    {
        newX = *x + dx[i];
        newY = *y + dy[i];

        if (isSafe(newX, newY, board, n)) 
        {
            int degree = countAvailableMoves(newX, newY, board, n);
            if (degree < minDeg) 
            {
                minDegIdx = i;
                minDeg = degree;
            }
        }
    }

    // If no valid move is found, return false
    if (minDegIdx == -1) return false;

    // Make the best move (the one with the least onward moves)
    *x = *x + dx[minDegIdx];
    *y = *y + dy[minDegIdx];
    board[*x][*y] = '1';  // Mark the square as visited
    return true;
}

// Function to solve the Knight's Tour problem using Warnsdorff's rule
bool solveKnightTour(int startX, int startY, int n) 
{
    // Dynamically allocate memory for the board
    char** board = new char*[n];
    for (int i = 0; i < n; i++) {
        board[i] = new char[n];
        for (int j = 0; j < n; j++) {
            board[i][j] = 'X';  // 'X' marks unvisited cells
        }
    }
    bool flag = false;

    // Starting position of the knight
    board[startX][startY] = '0';  // '0' marks the starting position

    // Try to move the knight in a way that covers all squares
    int x = startX;
    int y = startY;
    for (int moveCount = 1; moveCount < n * n; moveCount++) 
    {
        if (!getNextMove(&x, &y, board, n)) 
        {
            flag = true;
            break;
        }
    }

    // Print the tour if found
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            cout << board[i][j] << "\t";  // Print '0', '1', or 'X'
        }
        cout << endl;
    }

    // Free dynamically allocated memory
    for (int i = 0; i < n; i++) {
        delete[] board[i];
    }
    delete[] board;

    return !flag;
}


void tryAllStartPositions(int n) 
{
    bool found = false;

    for (int startX = 0; startX < n; startX++) {
        for (int startY = 0; startY < n; startY++) {
            cout << "Trying start position: (" << startX << ", " << startY << ")" << endl;
            if (solveKnightTour(startX, startY, n)) 
            {
                cout << "Knight's Tour is possible from (" << startX << ", " << startY << ")" << endl;
                found = true;
            } 
            else 
            {
                cout << "No solution from (" << startX << ", " << startY << ")." << endl;
            }
            cout<<"============================================================="<<endl;
        }
        
    }

    if (!found) 
    {
        cout << "No Knight's Tour possible from any starting position on a " << n << "x" << n << " board." << endl;
    }
}


int main() 
{
    int n, startX, startY;

    // Get user input for the size of the board
    cout << "Enter the size of the board (n x n): ";
    cin >> n;

    // Get user input for the starting position
    cout << "Enter the starting position (row and column): \n";
    cin >> startX >> startY;



    cout<<"      0: The starting position. \n      1: A visited position. \n      X: An unvisited position."<<endl;
    cout<<"============================================================="<<endl;

    // Check if the starting position is valid
    if (startX < 0 || startX >= n || startY < 0 || startY >= n) {
        cout << "Invalid starting position!" << endl;
        return 0;
    }

    // Start solving the knight's tour
    if (solveKnightTour(startX, startY, n)) {
        cout << "Knight's Tour is possible!" << endl;
    } else {
        cout << "No solution found!" << endl;
    }
    
    // tryAllStartPositions(n);         //Try All possible start positions Given board size

    cout<<"============================================================="<<endl;


    return 0;
}
