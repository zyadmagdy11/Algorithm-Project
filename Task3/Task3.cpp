#include <iostream>
#include <vector>
using namespace std;

//Tower of Hanoi: Frame-Stewart algorithm


int moveCount = 0;
void printPegs(const vector< vector<int> >& pegs, int n) 
{
    for (int i = 0; i < 4; i++) {
        cout << "Peg " << i + 1 << ": ";
        if (pegs[i].empty()) {
            cout << "empty";
        } else {
            for (int disk : pegs[i]) {
                cout << disk << " ";
            }
        }
        cout << endl;
    }
    cout << "----------------------------" << endl;
}

// Function to move `k` disks from `src` to `dest` using `aux1` and `aux2` pegs
void moveDisks(int k, int src, int dest, int aux1, int aux2, vector< vector<int> > & pegs) 
{
    if (k == 0) return;

    // Move top k-1 disks from src to aux2 using aux1 and dest as auxiliary
    moveDisks(k - 1, src, aux2, aux1, dest, pegs);

    cout << "Move disk " << k << " from Peg " << src + 1 << " to Peg " << dest + 1 << endl;


    pegs[dest].push_back(pegs[src].back());                         
    pegs[src].pop_back();
    moveCount++;                                                    
    printPegs(pegs, k);                                            


    // Move the k-1 disks from aux2 to dest using src and aux1 as auxiliary
    moveDisks(k - 1, aux2, dest, src, aux1, pegs);
}

void solveHanoi(int n, vector< vector<int> > & pegs) 
{

    // Initialize the first peg with all disks
    for (int i = n; i > 0; --i) 
    {
        pegs[0].push_back(i);
    }

    printPegs(pegs, n);

    // Move top k disks from peg 0 to peg 3, using pegs 1 and 2 as auxiliary pegs
    moveDisks(n, 0, 3, 1, 2, pegs);

    cout << "Total number of moves: " << moveCount << endl;
}

int main() 
{
    int n = 8;
    vector< vector<int> > pegs(4);
    solveHanoi(n, pegs);

    return 0;
}
