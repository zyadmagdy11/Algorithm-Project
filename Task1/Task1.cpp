#include <iostream>
#include <vector>
#include <cstdlib>
#include <ctime>

using namespace std;

int step = 1;
vector<vector<int>> board;


void printBoard(int size) {
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            printf("%3d ", board[i][j]);
        }
        cout << endl;
    }
    cout << endl;
}

void solve(int size, int topX, int topY, int holeX, int holeY) {
    if (size == 2) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (topX + i != holeX || topY + j != holeY) {
                    board[topX + i][topY + j] = step;
                }
            }
        }
        step++;
        return;
    }

    int half = size / 2;
    int midX = topX + half;
    int midY = topY + half;
    int centerX = midX - 1;
    int centerY = midY - 1;

    int quad = 0;
    if      (holeX < midX && holeY < midY) quad = 1;
    else if (holeX < midX && holeY >= midY) quad = 2;
    else if (holeX >= midX && holeY < midY) quad = 3;
    else quad = 4;

    if (quad != 1) board[centerX][centerY] = step;
    if (quad != 2) board[centerX][centerY + 1] = step;
    if (quad != 3) board[centerX + 1][centerY] = step;
    if (quad != 4) board[centerX + 1][centerY + 1] = step;
    step++;

    solve(half, topX, topY,(quad == 1) ? holeX : midX - 1, (quad == 1) ? holeY : midY - 1);
    solve(half, topX, midY,(quad == 2) ? holeX : midX - 1, (quad == 2) ? holeY : midY);
    solve(half, midX, topY,(quad == 3) ? holeX : midX, (quad == 3) ? holeY : midY - 1);
    solve(half, midX, midY,(quad == 4) ? holeX : midX, (quad == 4) ? holeY : midY);
}

int main() {
    srand(time(0));

    int size;
    cout << "Enter board size (power of 2): ";
    cin >> size;

    if ((size & (size - 1)) != 0 || size <= 0) {
        cout << "Size must be a power of 2 and greater than 0." << endl;
        return 1;
    }

    int holeX, holeY;
    cout << "Enter missing square coordinates (row col): ";
    cin >> holeX >> holeY;

    if (holeX < 0 || holeX >= size || holeY < 0 || holeY >= size) {
        cout << "Invalid coordinates for the hole." << endl;
        return 1;
    }

    board.resize(size, vector<int>(size, -1)); // Initialize board with -1
    board[holeX][holeY] = 0; // Mark the hole position as 0

    solve(size, 0, 0, holeX, holeY);
    printBoard(size);

    return 0;
}
