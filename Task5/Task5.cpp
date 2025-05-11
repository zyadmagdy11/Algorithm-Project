#include <iostream>
#include <vector>
#include <cstdlib>
#include <ctime>

using namespace std;

// Function to perform shots on hiding spots and guarantee hitting the target
bool shootAtTarget(vector<int>& spot, int* target, int start, int end, int* i)
{
    if (*i == -1) 
    {
        return true;
    }
    else if (start == end)
    {
        return false;
    }

    int mid = (start + end) / 2;
    cout << "Shoot at hiding spot " << mid << endl;


    if (spot[mid] == *target) 
    {
        cout << "Shooter shot target at spot: " << *target << endl;
        *i = -1;
        return true;
    }
    *i = *i + 1;
    if(*i % 2 == 0) 
    {
        bool flag;
        flag = rand() % 2;
        if (flag)
        {
            cout << "Target moved: " << *target <<" -> " << (*target + 1) % spot.size() << endl;
            *target = (*target + 1) % spot.size();
        }
        else
        {
            cout << "Target moved: " << *target <<" -> " << (*target - 1) % spot.size() << endl;
            *target = (*target - 1) % spot.size();
        }
        
    }

    return shootAtTarget(spot, target, start, mid , i) || shootAtTarget(spot, target, mid + 1, end, i);

 
}

int main() 
{
    srand(time(0)); // seed random generator
    int n;
    int target;
    int consecutive_shot = 0;
    cout << "Enter the number of hiding spots: ";
    cin >> n;

    cout << "Enter Target spot: ";
    cin >> target;

    vector<int> spots(n);  

    for (int i = 0; i < n; ++i) 
    {
        spots[i] = i;
    }

    
    cout<<"============================================================="<<endl;

    // Call the shoot function
    while(!shootAtTarget(spots, &target, 0, n - 1, &consecutive_shot)) 
    {
        cout<<"Failed Try again"<<endl;
        cout<<"Target: "<<target<<endl;
        cout<<"============================================================="<<endl;

    }

    return 0;
}

