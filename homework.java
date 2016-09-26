package homeworks;

import java.io.*;
import java.util.*;

public class homework {

    private Node root;
    private Node goal;
    private int[][] edges;
    private ArrayList<Node> nodes;
    private Map<Node, Integer> sundays;
    private Map<Node, ArrayList<Node>> prefer;
    //construct graph
    public homework() {
        nodes = new ArrayList<>();
        sundays = new HashMap<>();
        prefer = new HashMap<>();
    }

    public void setRootNGoal(String s1, String s2) {
        root = new Node(s1);
        goal = new Node(s2);
    }

    //add nodes and sunday traffic data
    public void addNodes(Node n, int i) {
        nodes.add(n);
        n.sunday = i;
        sundays.put(n, i);
        prefer.put(n, new ArrayList<Node>());
    }

    public void setEdgesSize(int i) {
        edges = new int[i][i];
    }

    public void setEdges(Node a, Node b, int cost) {
        edges[nodes.indexOf(a)][nodes.indexOf(b)] = cost;
        prefer.get(a).add(b);
        prefer.get(a).get(prefer.get(a).indexOf(b)).edge.put(a, cost);
        nodes.get(nodes.indexOf(b)).edge.put(a, cost);
    }

    public Node getChildNode(Node n) {

        ArrayList<Node> children = prefer.get(n);
        for (int j = 0; j < children.size(); j++) {
            Node temp = children.get(j);
            if (!temp.visited) {
                return temp;
            }
        }
        return null;
    }

    public Node getDfsChild(Node n) {
        ArrayList<Node> children = prefer.get(n);
        for (int j = children.size() - 1; j >= 0; j--) {
            Node temp = children.get(j);
            if (!temp.visited) {
                return temp;
            }
        }
        return null;
    }

    //finding optimal path with dfs
    public void bfs() {
        //Map<Node, Node> previous = new HashMap<>();
        List<Node> path = new LinkedList<>();
        Queue<Node> queue = new LinkedList<>();
        Node temp = root;
        queue.add(temp);
        temp.visited = true;
        while (!queue.isEmpty()) {
            temp = queue.remove();
            if (temp.equals(goal)) {
                break;
            }
            Node child;
            while ((child = getChildNode(temp)) != null) {
                child.visited = true;
                queue.add(child);
                child.parent = temp;
                child.pathCost = temp.pathCost + 1;
            }
        }
        if (!temp.equals(goal)) {
            System.out.println(" no solution! ");
        } else {
            for (Node node = temp; node != null; node = node.parent) {
                path.add(node);
            }
            Collections.reverse(path);
        }
        writeOutput(path);


    }

    public void dfs() {
        List<Node> path = new LinkedList<>();
        Deque<Node> queue = new LinkedList<>();
        queue.addFirst(root);
        Node temp = root;
        root.visited = true;
        while (!queue.isEmpty()) {
            temp = queue.remove();
            Node child;
            if (temp.equals(goal)) {
                break;
            }
            while ((child = getDfsChild(temp)) != null) {
                child.visited = true;
                Node curr = null;
                child.parent = temp;
                child.pathCost = temp.pathCost + 1;
                if (queue.contains(child)) {
                    Iterator<Node> it = queue.iterator();
                    while (it.hasNext()) {
                        Node copy = it.next();
                        if (copy.equals(child)) {
                            curr = copy;
                            break;
                        }
                    }
                    if (curr.pathCost <= child.pathCost) {
                        continue;
                    }
                }
                queue.addFirst(child);

            }
        }
        if (!temp.equals(goal)) {
            System.out.println(" no solution! ");
        } else {
            for (Node node = temp; node != null; node = node.parent) {
                path.add(node);
            }
            Collections.reverse(path);
        }
        writeOutput(path);


    }


    public void ucs() {
        List<Node> path = new LinkedList<>();
        PriorityQueue<Node> open = new PriorityQueue<>(10, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pathCost > o2.pathCost) return 1;
                if (o1.pathCost == o2.pathCost) {
                    if (o1.timeStamp < o2.timeStamp) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return -1;
            }
        });
        int time = 0;
        Deque<Node> close = new LinkedList<>();
        Node temp = root;
        open.add(temp);
        root.timeStamp = time++;
        root.visited = true;

        while (!open.isEmpty()) {
            temp = open.poll();
            if (temp.equals(goal)) {
                break;
            }
            Node child;
            while ((child = getChildNode(temp)) != null) {
                child.timeStamp = time++;
                child.visited = true;
                child.parent = temp;
                child.pathCost = temp.pathCost + child.edge.get(temp);
                if (!open.contains(child) && !close.contains(child)) {
                    open.add(child);
                } else if (open.contains(child)) {
                    Iterator<Node> it = open.iterator();
                    Node curr = null;
                    while (it.hasNext()) {
                        Node copy = it.next();
                        if (copy.equals(child)) {
                            curr = copy;
                            break;
                        }
                    }
                    if (curr != null && curr.pathCost > child.pathCost) {
                        open.remove(curr);
                        open.add(child);
                    }
                } else if (close.contains(child)) {
                    Iterator<Node> it2 = close.iterator();
                    Node curr2 = null;
                    while (it2.hasNext()) {
                        Node copy2 = it2.next();
                        if (copy2.equals(child)) {
                            curr2 = copy2;
                            break;
                        }
                    }
                    if (curr2 != null && curr2.pathCost > child.pathCost) {
                        open.add(child);
                        close.remove(curr2);
                    }
                }

            }
            close.addLast(temp);
        }
        if (!temp.equals(goal)) {
            //System.out.println(" no solution! ");
        } else {
            for (Node node = temp; node != null; node = node.parent) {
                path.add(node);
            }
            Collections.reverse(path);
        }
        writeOutput(path);

    }


    public void ASearch() {
        int time = 0;
        List<Node> path = new LinkedList<>();
        PriorityQueue<Node> open = new PriorityQueue<>(10, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pathCost + o1.sunday > o2.pathCost + o2.sunday) return 1;
                if (o1.pathCost + o1.sunday == o2.pathCost + o2.sunday) {
                    if (o1.timeStamp < o2.timeStamp) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return -1;
            }
        });
        Deque<Node> close = new LinkedList<>();
        Node temp = root;
        temp.sunday = sundays.get(temp);
        open.add(temp);
        root.timeStamp = time++;
        root.visited = true;

        while (!open.isEmpty()) {

            temp = open.poll();
            if (temp.equals(goal)) {
                break;
            }
            for (Node child : prefer.get(temp)) {
                child.timeStamp = time++;
                child.sunday = sundays.get(child);
                child.visited = true;
                child.parent = temp;
                child.pathCost = temp.pathCost + child.edge.get(temp);
                if (child.equals(new Node("H")) && child.pathCost == 24) {
                    System.out.println(child.parent.name + child.parent.pathCost);
                }
                if (!open.contains(child) && !close.contains(child)) {
                    open.add(child);
                } else if (open.contains(child)) {
                    Iterator<Node> it = open.iterator();
                    Node curr = null;
                    while (it.hasNext()) {
                        Node copy = it.next();
                        if (copy.equals(child)) {
                            curr = copy;
                            break;
                        }
                    }
                    if (curr != null && curr.pathCost > child.pathCost) {
                        open.remove(curr);
                        open.add(child);
                    }
                } else if (close.contains(child)) {
                    Iterator<Node> it2 = close.iterator();
                    Node curr2 = null;
                    while (it2.hasNext()) {
                        Node copy = it2.next();
                        if (copy.equals(child)) {
                            curr2 = copy;
                            break;
                        }
                    }
                    if (curr2 != null && curr2.pathCost > child.pathCost) {
                        open.add(child);
                        close.remove(curr2);
                    }
                }
            }
            Node closeNode = new Node(temp.name);
            closeNode.copy(temp);
            close.addLast(closeNode);
        }
        if (!temp.equals(goal)) {
            System.out.println(" no solution! ");
        } else {
            for (Node node = temp; node != null; node = node.parent) {
                path.add(node);
            }
            Collections.reverse(path);
        }
        writeOutput(path);

    }

    public void writeOutput(List<Node> path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            for (Node n : path) {
                bw.write(n.name);
                bw.write(" ");
                bw.write(Integer.toString(n.pathCost));
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        homework graph = new homework();
        String input = "input.txt";
        BufferedReader in = new BufferedReader(new FileReader(input));
        String algor = in.readLine();

        String start = in.readLine();
        String goal = in.readLine();

        graph.setRootNGoal(start, goal);
        int numOfLiveTraffic = Integer.parseInt(in.readLine());

        ArrayList<String> traLines = new ArrayList<>();
        for (int i = 0; i < numOfLiveTraffic; i++) {
            traLines.add(in.readLine());
        }
        int numOfLocations = Integer.parseInt(in.readLine());

        graph.setEdgesSize(numOfLocations);
        //add nodes and sunday traffic info
        for (int i = 0; i < numOfLocations; i++) {
            String[] suns = in.readLine().split(" ");
            graph.addNodes(new Node(suns[0]), Integer.parseInt(suns[1]));
        }
        //set edges and costs
        for (int i = 0; i < numOfLiveTraffic; i++) {
            String[] strs = traLines.get(i).split(" ");
            graph.setEdges(new Node(strs[0]), new Node(strs[1]), Integer.parseInt(strs[2]));


        }

        //running search algorithms based on input's request
        if (algor.equals("BFS")) {
            graph.bfs();
        } else if (algor.equals("DFS")) {
            graph.dfs();
        } else if (algor.equals("UCS")) {
            graph.ucs();
        } else if (algor.equals("A*")) {
            graph.ASearch();
        } else {
            System.out.println("algorithm input is wrong! ");
        }
        in.close();
    }
}

class Node {
    public Node parent;
    public boolean visited = false;
    public String name;
    public int timeStamp;

    public Node(String name) {
        this.name = name;
        edge = new HashMap<>();
    }

    public int pathCost;
    public Map<Node, Integer> edge;
    public int sunday;

    @Override
    public boolean equals(Object obj) {
        Node n = (Node) obj;
        boolean res = false;
        res = (this.name.equals(n.name));
        return res;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public void copy(Node n) {
        pathCost = n.pathCost;
        parent = n.parent;
        visited = n.visited;
        name = n.name;
        timeStamp = n.timeStamp;
        edge = n.edge;
        sunday = n.sunday;
    }
}


