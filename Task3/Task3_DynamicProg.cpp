#include <iostream>
#include <vector>
#include <queue>

using namespace std;

struct TreeNode 
{
    vector< vector<int> > pegs;
    TreeNode* parent;
};



void printPegs(const vector< vector<int> >& pegs) {
    for (int i = 0; i < pegs.size(); ++i) {
        cout << "Peg " << i + 1 << ": ";
        for (int disk : pegs[i]) cout << disk << " ";
        cout << endl;
    }
}

void printSolution(TreeNode* node) {
    if (!node) return;
    if (!node->parent) {
        cout << "Initial state:" << endl;
        printPegs(node->pegs);
        cout << "--------" << endl;
    } else {
        printSolution(node->parent);
        int from = -1, to = -1, moved = -1;
        for (int i = 0; i < node->pegs.size(); ++i) {
            int diff = node->pegs[i].size() - node->parent->pegs[i].size();
            if (diff == 1) {
                to = i;
                moved = node->pegs[i].back();
            }
            if (diff == -1) {
                from = i;
            }
        }
        cout << "Move disk " << moved << " from peg " << from + 1 << " to peg " << to + 1 << endl;
        printPegs(node->pegs);
        cout << "--------" << endl;
    }
}




string createIdentifier(TreeNode* node, int numPegs)
{
    string id;
    for (int i = 0; i < numPegs; ++i)
    {
        id += '[';
        for (int disk : node->pegs[i]) id += char('A' + disk); // A, B, C,...
        id += ']';
    }
    return id;
}





bool isVisited(string id, const vector<string>& visited) 
{
    for (string node : visited) 
    {
        if (node == id) return true;
    }
    return false;
}

vector<TreeNode*> generateChildren(TreeNode* parent, int numPegs) 
{
    vector<TreeNode*> children;
    for (int from = 0; from < numPegs; ++from) 
    {
        if (parent->pegs[from].empty()) continue;
        int disk = parent->pegs[from].back();
        for (int to = 0; to < numPegs; ++to) 
        {
            if (from == to) continue;
            if (!parent->pegs[to].empty() && parent->pegs[to].back() < disk) continue;

            TreeNode* child = new TreeNode();
            child->pegs = parent->pegs;
            child->pegs[from].pop_back();
            child->pegs[to].push_back(disk);
            child->parent = parent;
            children.push_back(child);
        }
    }
    return children;
}



int bfs(TreeNode* initial, TreeNode* goal, int numPegs) 
{
    queue<TreeNode*> frontier;
    vector<string> visited;

    string goal_id = createIdentifier(goal, numPegs);
    frontier.push(initial);
    visited.push_back(createIdentifier(initial, numPegs));

    int depth = 0;

    while (!frontier.empty()) 
    {
        int levelSize = frontier.size();
        for (int i = 0; i < levelSize; ++i) 
        {
            TreeNode* current = frontier.front();
            frontier.pop();
            string current_id = createIdentifier(current, numPegs);

            if (current_id == goal_id) 
            {
                printSolution(current);
                return depth;
            }
            
            vector<TreeNode*> children = generateChildren(current, numPegs);
            for (TreeNode* child : children) 
            {
                string id = createIdentifier(child, numPegs);
                if (isVisited(id, visited)) 
                {
                    delete child;
                    continue;
                }
                visited.push_back(id);
                frontier.push(child);
            }
        }
        ++depth;
    }
    return -1;
}

int main() {
    int numDisks = 6;
    int numPegs = 4;

    TreeNode* root = new TreeNode();
    root->pegs.resize(numPegs);

    for (int i = numDisks; i >= 1; --i) root->pegs[0].push_back(i);
    root->parent = nullptr;

    TreeNode* goal = new TreeNode();
    goal->pegs.resize(numPegs);

    for (int i = numDisks; i >= 1; --i) goal->pegs[numPegs - 1].push_back(i);

    int result = bfs(root, goal, numPegs);
    if (result != -1) {
        cout << "Shortest path height: " << result << endl;
    } else {
        cout << "Goal not reachable." << endl;
    }

    return 0;
}
