#include <iostream>
#include <vector>

using namespace std;

vector<vector<int>> drawLine(vector<vector<int>>& array, int x1, int y1, int x2, int y2, int value) 
{
    if (x1 == x2) 
    {
        //Horizontal Line
        if (y1 > y2) swap(y1, y2);
        for(int i = y1 ; i<=y2 ; i++)
        {
            array[x1][i] = value;
        } 
    } 
    else if (y1 == y2) 
    { 
        //Vertical Line
        if (x1 > x2) swap(x1, x2);
        for(int i = x1 ; i<=x2 ; i++)
        {
            array[i][y1] = value;
        } 
    } 
    return array;
    
}

vector<pair<int, int>> CornerPoints(vector<vector<int>>& array ,int n) 
{
    vector<pair<int, int>> Point;
    Point.push_back({0,0});

    int line = 99;

    int layers = (n + 1) / 2;
    int x1, y1, x2, y2;

    for (int i = 0; i < layers * 4 - 1; ++i) 
    {
        int layer = i / 4;

        switch (i % 4) 
        {
            case 0:
                x1 = layer;
                y1 = layer;
                x2 = layer;
                y2 = n - 1 - layer;
                break;
            case 1:
                x1 = layer;
                y1 = n - 1 - layer;
                x2 = n - 1 - layer;
                y2 = n - 1 - layer;
                break;
            case 2:
                x1 = n - 1 - layer;
                y1 = n - 1 - layer;
                x2 = n - 1 - layer;
                y2 = layer;
                break;
            case 3:
                x1 = n - 1 - layer;
                y1 = layer;
                x2 = layer + 1;
                y2 = layer;
                break;
        }
        if(x1 == x2 && y1 == y2) break;    
        Point.push_back({x2,y2});

    }
    return Point;

    
}

void solenoidPattern(vector<pair<int, int>> Point, vector<vector<int>>& array, int n)
{
    int line = 99;
    pair<int, int> P1;
    pair<int, int> P2;
    for (int i = 0; i < Point.size() - 1; i++) 
    {
        P1 = Point[i];
        P2 = Point[i + 1];
        array = drawLine(array, P1.first, P1.second, P2.first, P2.second, ++line);
    }

    if (n%2 == 0) 
    {
        array = drawLine(array, P1.first, P1.second,  P1.first, 0, 777);
    }
    else          
    {
        array = drawLine(array, P2.first, P2.second,  P2.first, n - 1, 777);
    }
    

}

int main() 
{
    int n = 8;
    vector<vector<int>> array(n, vector<int>(n, 0));
    vector<pair<int, int>> Point;

    Point = CornerPoints(array,n);
    solenoidPattern(Point,array,n);

    for (int i = 0; i < n; ++i) 
    {
        for (int j = 0; j < n; ++j) 
        {
            cout << array[i][j] << " ";
        }
        cout << endl;
    }

    

    return 0;
}
