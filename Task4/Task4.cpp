#include <iostream>
using namespace std;


class Graph 
{

public:
    int V = 12; 
    char type [12] = {'B','B','B', 'E','E','E', 'E', 'E', 'E', 'W', 'W', 'W'};  
    int Edges [7][2][2] = 
    {
        { {0,7},  {4,9}  },
        { {2,7},  {4,11} },
        { {2,3},  {6,11} }, 
        { {3,8},  {5,6}  },
        { {8,9},  {0,5}  }, 
        { {5,10}, {8,1}  },
        { {10,3}, {6,1}  }
    };


    void printTypeArray() 
    {
        for (int i = 0; i < 12; i++) {
            cout << type[i] << "   ";
            if ((i + 1) % 3 == 0) cout << endl;
        }
        cout <<"==========================================" <<endl;
    }

    void swapTypeElements(int arr[][2],int* j) 
    {
        int index1 = arr[0][0];
        int index2 = arr[0][1];
        int index3 = arr[1][0];
        int index4 = arr[1][1];
        if((type[index1] == 'E' && type[index2] == 'E') || (type[index3] == 'E' && type[index4] == 'E')) return;
        if((type[index1] == 'E' || type[index2] == 'E') && (type[index3] == 'E' || type[index4] == 'E'))
        {      
                
            char temp  = type[index1];
            type[index1] = type[index2];
            type[index2] = temp;
            (*j)++;

            temp  = type[index3];
            type[index3] = type[index4];
            type[index4] = temp;
            (*j)++;
            printTypeArray();
        }

    }


    bool goalState() 
    {
        if (type[0] == 'W' && type[1] == 'W' && type[2] == 'W' && type[9] == 'B' && type[10] == 'B' && type[11] == 'B') 
        {
            return true;
        }
        return false;
    }
        

    
};


void iterativeImprovement(Graph &g) 
{
    int i = 5;
    int steps = 0;
    while (!g.goalState() ) 
    {
        g.swapTypeElements(g.Edges[i], &steps);
        i = (i+1)%7;                   
    }
    cout<<"Puzzle solved successfully in " << steps << " steps!"<<endl;

}



int main() 
{
    Graph g;
    g.printTypeArray();
    iterativeImprovement(g);

    return 0;
}







