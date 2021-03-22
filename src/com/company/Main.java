package com.company;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static int[] keysSpace;
    static int[] nodes;
    static String[] shortcutsStr;

    public static void main(String[] args) {

        Parser parser = new Parser("/home/farrukh/IdeaProjects/DisHashTable/Input-file.txt");
//        Parser parser = new Parser(args[0]);

        keysSpace = parser.getKeySpace();
        nodes = parser.getNodes();
        shortcutsStr = parser.getShortcuts();

        DirectedGraph<Integer, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i : nodes){
            directedGraph.addVertex(i);
        }

        for (int i = 0; i < nodes.length - 1; i++){
            directedGraph.addEdge(nodes[i], nodes[i+1]);
        }
        directedGraph.addEdge(nodes[nodes.length - 1], nodes[0]);

        for (String s : shortcutsStr){

            try {

                int srcNode = Integer.parseInt(s.split(":")[0]);
                int desNode = Integer.parseInt(s.split(":")[1]);

                if (directedGraph.containsVertex(srcNode) && directedGraph.containsVertex(desNode)){
                    directedGraph.addEdge(srcNode, desNode);
                }
            } catch (Exception e){

            }

        }

        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String line = scanner.nextLine();

                if (line.equals("List")){

                    printListGraph(directedGraph);

                } else if (line.contains("Lookup")){

                    String[] words = line.split(" ");
                    String[] keyAndNode = words[1].split(":");

                    if (keyAndNode.length == 1){
                        keyAndNode = new String[]{ words[1], String.valueOf(nodes[0])};
                    }

                    printLookupGraph(directedGraph, Integer.parseInt(keyAndNode[0]), Integer.parseInt(keyAndNode[1]));

                } else if (line.contains("Join")){

                    String[] words = line.split(" ");
                    int newNode = Integer.parseInt(words[1]);

                    directedGraph = createNewGraph(newNode);

                } else if (line.contains("Leave")){

                    String[] words = line.split(" ");
                    int deleteNode = Integer.parseInt(words[1]);

                    directedGraph = deleteGraph(deleteNode);

                } else if (line.contains("Shortcut")){

                    String[] words = line.split(" ");
                    String[] startEnd = words[1].split(":");

                    directedGraph = addEdjeToGraph(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]));

                }

            }
        } catch(IllegalStateException | NoSuchElementException e) {
            // System.in has been closed
            System.out.println("System.in was closed; exiting");
        }

    }

    public static void printListGraph(DirectedGraph directedGraph){

        BreadthFirstIterator breadthFirstIterator = null;

        Map<Integer, List<Integer>> adjList = new HashMap<>();
        List<Integer> restList = new ArrayList<>();

        for (int i = 0; i < nodes.length; i++) {

            if (nodes.length >= 3) {

                restList = new ArrayList<>();

                breadthFirstIterator = new BreadthFirstIterator<>(directedGraph, nodes[i]);

                while (breadthFirstIterator.hasNext()) {

                    int nextNode = (int) breadthFirstIterator.next();

                    int nodeNum = i;

                    if (i >= nodes.length - 2) {

                        nodeNum = i % (nodes.length - 2);

                        if (nextNode == nodes[nodeNum]) {
                            break;
                        }

                        restList.add(nextNode);
                        continue;

                    } else if (nextNode == nodes[nodeNum + 2]) {
                        break;
                    }

                    restList.add(nextNode);

                }

                if (i >= nodes.length - 2) {

                    restList.add(nodes[i % (nodes.length - 2)]);
                } else {
                    restList.add(nodes[i + 2]);
                }

                adjList.put(nodes[i], restList);

            }
        }
        for (int i = 0; i < nodes.length; i++){

            if (nodes.length >= 3) {

                System.out.print(nodes[i] + ":");
                if (adjList.get(nodes[i]).size() > 3) {
                    for (int j = 2; j < adjList.get(nodes[i]).size() - 1; j++) {
                        System.out.print(adjList.get(nodes[i]).get(j) + ", ");
                    }
                } else {
                    System.out.print(", ");
                }
                System.out.print("S-" + adjList.get(nodes[i]).get(1) + ", ");

                if (nodes.length == 3 && i == nodes.length - 1) {
                    System.out.print("NS-" + nodes[1]);
                } else {
                    System.out.print("NS-" + adjList.get(nodes[i]).get(adjList.get(nodes[i]).size() - 1));
                }
                System.out.println();
            }

            if (nodes.length == 2){
                System.out.println(nodes[0] + ":, " + "S-" + nodes[1] + ", NS-" + nodes[0] );
                System.out.println(nodes[1] + ":, " + "S-" + nodes[0] + ", NS-" + nodes[1] );
                break;
            }

            if (nodes.length == 1){
                System.out.println(nodes[0] + ":, " + "S-" + nodes[0] + ", NS-" + nodes[0] );
                break;
            }

        }

    }

    public static void printLookupGraph(DirectedGraph directedGraph, int key, int node){

        AllDirectedPaths allDirectedPaths =  new AllDirectedPaths(directedGraph);

        int targetNode = nodes[0];
        for (int i = 0; i < nodes.length; i++)
            if (nodes[i] >= key){
                targetNode = nodes[i];
                break;
            }

        List<String> shortestPath = allDirectedPaths.getAllPaths(node, targetNode,
                true, directedGraph.vertexSet().size());

        int prevStep = keysSpace[1];
        int lookupNum;

        for (Object path : shortestPath){

            System.out.println(path);

            Pattern pattern = Pattern.compile("[^,]*,");
            Matcher matcher = pattern.matcher(path.toString());
            int count = 0;

            while (matcher.find()) {
                count++;
            }

            if (prevStep >= count){
                prevStep = count;
            }

        }

        lookupNum = prevStep;

        if (node != targetNode){
            lookupNum = prevStep + 1;
        }

        System.out.println("Result: Data stored in node " + targetNode + " – " + lookupNum + " requests sent");
    }

    public static DirectedGraph createNewGraph(int newNode){

        boolean exitFlag = false;
        for (int i = 0; i < nodes.length; i++){
            if (nodes[i] == newNode){
                System.out.println("Node " + newNode + " already exist. Node not added.");
                exitFlag = true;
            }
        }

        if (!exitFlag){
            nodes = addElement(nodes, newNode);

            Arrays.sort(nodes);
        }


        DirectedGraph<Integer, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i : nodes){
            directedGraph.addVertex(i);
        }

        for (int i = 0; i < nodes.length - 1; i++){
            directedGraph.addEdge(nodes[i], nodes[i+1]);
        }
        directedGraph.addEdge(nodes[nodes.length - 1], nodes[0]);

        for (String s : shortcutsStr){

            int srcNode = Integer.parseInt(s.split(":")[0]);
            int desNode = Integer.parseInt(s.split(":")[1]);

            if (directedGraph.containsVertex(srcNode) && directedGraph.containsVertex(desNode)){
                directedGraph.addEdge(srcNode, desNode);
            }

        }

        return directedGraph;
    }

    public static DirectedGraph deleteGraph(int deleteNode){

        boolean exitFlag = false;

        for (int i = 0; i < nodes.length; i++){
            if (nodes[i] == deleteNode){
                nodes = deleteElement(nodes, deleteNode);
                Arrays.sort(nodes);

                List<Integer> deleteList = new ArrayList<>();
                for (int j = 0; j < shortcutsStr.length; j++){
                    if (shortcutsStr[j].contains(String.valueOf(deleteNode))){
                        deleteList.add(j);
                    }
                }


                System.out.println("Node " + deleteNode + " deleted.");
                exitFlag = true;
            }
        }

        if (!exitFlag){
            System.out.println("Node does not exist.");
        }

        DirectedGraph<Integer, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i : nodes){
            directedGraph.addVertex(i);
        }

        for (int i = 0; i < nodes.length - 1; i++){
            directedGraph.addEdge(nodes[i], nodes[i+1]);
        }
        directedGraph.addEdge(nodes[nodes.length - 1], nodes[0]);

        for (String s : shortcutsStr){

            int srcNode = Integer.parseInt(s.split(":")[0]);
            int desNode = Integer.parseInt(s.split(":")[1]);

            if (directedGraph.containsVertex(srcNode) && directedGraph.containsVertex(desNode)){
                directedGraph.addEdge(srcNode, desNode);
            }

        }

        return directedGraph;
    }

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    static int[] deleteElement(int[] a, int e) {
        boolean deletedNode = false;
        for (int i = 0; i < a.length; i++){
            if (a[i] == e){
                a[i] = a[a.length - 1];
                deletedNode = true;
                break;
            }
        }

        if (deletedNode){
            a  = Arrays.copyOf(a, a.length - 1);
        }

        return a;
    }

    public static DirectedGraph addEdjeToGraph(int start, int end){

        DirectedGraph<Integer, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i : nodes){
            directedGraph.addVertex(i);
        }

        for (int i = 0; i < nodes.length - 1; i++){
            directedGraph.addEdge(nodes[i], nodes[i+1]);
        }
        directedGraph.addEdge(nodes[nodes.length - 1], nodes[0]);

        for (String s : shortcutsStr){

            int srcNode = Integer.parseInt(s.split(":")[0]);
            int desNode = Integer.parseInt(s.split(":")[1]);

            if (directedGraph.containsVertex(srcNode) && directedGraph.containsVertex(desNode)){
                directedGraph.addEdge(srcNode, desNode);
            }

        }

        if (directedGraph.containsVertex(start) && directedGraph.containsVertex(end)){
            directedGraph.addEdge(start, end);
        } else if (!directedGraph.containsVertex(start)){
            System.out.println("Node " + start + " not exist, shortcut not added." );
        } else if (!directedGraph.containsVertex(end)){
            System.out.println("Node " + end + " not exist, shortcut not added." );
        }

        return directedGraph;

    }

}
