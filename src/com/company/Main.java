package com.company;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.BreadthFirstIterator;
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

                    try {
                        Integer.parseInt(keyAndNode[0]);
                        Integer.parseInt(keyAndNode[1]);
                        printLookupGraph(directedGraph, Integer.parseInt(keyAndNode[0]), Integer.parseInt(keyAndNode[1]));
                    } catch (Exception e){
                        System.out.println("Wrong input");
                    }

                } else if (line.contains("Join")){

                    String[] words = line.split(" ");

                    try {
                        int newNode = Integer.parseInt(words[1]);

                        if (newNode >= keysSpace[0] && newNode <= keysSpace[1]){
                            directedGraph = createNewGraph(newNode);
                        } else {
                            System.out.println("Node " +newNode +" can not be added. Key space "+ keysSpace[0] + "-" + keysSpace[1]);
                        }
                    } catch (Exception e){
                        System.out.println("Wrong input");
                    }


                } else if (line.contains("Leave")){

                    String[] words = line.split(" ");

                    if (nodes.length == 1){
                        System.out.println("Can not delete last node.");
                    } else {
                        try {
                            int deleteNode = Integer.parseInt(words[1]);

                            directedGraph = deleteGraph(deleteNode);
                        } catch (Exception e){
                            System.out.println("Wrong input");
                        }
                    }


                } else if (line.contains("Shortcut")){

                    String[] words = line.split(" ");
                    String[] startEnd = words[1].split(":");

                    try {

                        Integer.parseInt(startEnd[0]);
                        Integer.parseInt(startEnd[1]);

                        directedGraph = addEdgeToGraph(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]));
                    } catch (Exception e){
                        System.out.println("Wrong input");
                    }

                } else if (line.contains("Remove")){

                    String[] words = line.split(" ");

                    for (int i = 1; i < words.length; i++){
                        words[i] = words[i].replaceAll(",", "" );
                    }

                    for (String w: words){

                        try {
                            int deleteNode = Integer.parseInt(w);
                            directedGraph = deleteGraph(deleteNode);
                        } catch (Exception e){

                        }

                    }

                } else {
                    System.out.println("Commands are: List, Lookup int, Lookup int:int, Join int, Leave int, Shortcut int:int, Remove int...");
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
                boolean firstTime = true;

                breadthFirstIterator = new BreadthFirstIterator<>(directedGraph, nodes[i]);
//                System.out.println(directedGraph);
                while (breadthFirstIterator.hasNext()) {

                    int nextNode = (int) breadthFirstIterator.next();

                    if (directedGraph.containsEdge(nodes[i], nextNode)){
                        restList.add(nextNode);

                        if(firstTime){
                            BreadthFirstIterator breadthFirstIterator2 = new BreadthFirstIterator<>(directedGraph, restList.get(0));

                            breadthFirstIterator2.next();
                            restList.add((int) breadthFirstIterator2.next());

                            firstTime = false;
                        }

                    }


//                    int nodeNum = i;
//                    System.out.print(nextNode + "/");
//                    if (i >= nodes.length - 2) {
//
//                        nodeNum = i % (nodes.length - 2);
//
//                        if (nextNode == nodes[nodeNum]) {
//                            break;
//                        }
//
//                        restList.add(nextNode);
//                        continue;
//
//                    } else if (nextNode == nodes[nodeNum + 2]) {
////                        break;
//                    }
//
//                    restList.add(nextNode);

                }
//
//                if (i >= nodes.length - 2) {
//
//                    restList.add(nodes[i % (nodes.length - 2)]);
//                } else {
//                    restList.add(nodes[i + 2]);
//                }

                adjList.put(nodes[i], restList);

            }
        }
        for (int i = 0; i < nodes.length; i++){

            if (nodes.length >= 3) {

                boolean smtPrinted = false;
                System.out.print(nodes[i] + ":");
//                System.out.print(Arrays.asList(adjList.get(nodes[i])));
                if (adjList.get(nodes[i]).size() > 3) {
                    for (int j = 2; j <= adjList.get(nodes[i]).size() - 2; j++) {
                        System.out.print(adjList.get(nodes[i]).get(j) + ", ");
                        smtPrinted = true;
                    }
                }
//
                String lookForSpecialShortcut1 = nodes[i] + ":" + adjList.get(nodes[i]).get(adjList.get(nodes[i]).size() - 1);
//                String lookForSpecialShortcut2 = adjList.get(nodes[i]).get(adjList.get(nodes[i]).size() - 1) + ":" + nodes[i];

                for (String s : shortcutsStr){

                    if (s.equals(lookForSpecialShortcut1)){
                        System.out.print(adjList.get(nodes[i]).get(adjList.get(nodes[i]).size() - 1) + ", ");
                        smtPrinted = true;
                    }
                }

                if (!smtPrinted){
                    System.out.print(", ");
                }

////
                System.out.print("S-" + adjList.get(nodes[i]).get(0) + ", ");

                if (nodes.length == 3 && i == nodes.length - 1) {
                    System.out.print("NS-" + nodes[1]);
                } else {
                    System.out.print("NS-" + adjList.get(nodes[i]).get(1));
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

                List<String> deleteList = new ArrayList<>();

                for (int j = 0; j < shortcutsStr.length; j++){
                    if (shortcutsStr[j].contains(String.valueOf(deleteNode))){

                        String[] startEnd = shortcutsStr[j].split(":");
                        int a = Integer.parseInt(startEnd[0]);
                        int b =Integer.parseInt(startEnd[1]);

                        if (a == deleteNode || b == deleteNode){

                            deleteList.add(shortcutsStr[j]);

                        }
                    }
                }

                List<String> tempStr = new ArrayList<>(Arrays.asList(shortcutsStr));
                for (String s : deleteList){

                    tempStr.remove(s);

                }

                shortcutsStr = new String[tempStr.size()];
                tempStr.toArray(shortcutsStr);

                System.out.println("Node " + deleteNode + " deleted.");
                exitFlag = true;
            }
        }

//        shortcutsStr[shortcutsStr.length - 1] = start + ":" + end;
//        shortcutsStr = Arrays.copyOf(shortcutsStr, shortcutsStr.length - 1);
/////////////

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

    public static DirectedGraph addEdgeToGraph(int start, int end){

        DirectedGraph<Integer, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i : nodes){
            directedGraph.addVertex(i);
        }

        for (int i = 0; i < nodes.length - 1; i++){
            directedGraph.addEdge(nodes[i], nodes[i+1]);
        }
        directedGraph.addEdge(nodes[nodes.length - 1], nodes[0]);

        if (directedGraph.containsEdge(start, end)){
            System.out.println("The node is successor or shortcut, shortcut not added.");
        } else if (directedGraph.containsVertex(start) && directedGraph.containsVertex(end)){

                boolean add = true;
                for (String s : shortcutsStr){
                    if (s.equals(start + ":" + end)){
                        add = false;
                    }
                }

                if (add){
                    shortcutsStr = Arrays.copyOf(shortcutsStr, shortcutsStr.length + 1);
                    shortcutsStr[shortcutsStr.length - 1] = start + ":" + end;

                } else {
                    System.out.println("Shortcut exist. No shortcut added");
                }


        } else if (!directedGraph.containsVertex(start)){
            System.out.println("Node " + start + " not exist, shortcut not added." );
        } else if (!directedGraph.containsVertex(end)){
            System.out.println("Node " + end + " not exist, shortcut not added." );
        }

        for (String s : shortcutsStr){

            int srcNode = Integer.parseInt(s.split(":")[0]);
            int desNode = Integer.parseInt(s.split(":")[1]);

            if (directedGraph.containsVertex(srcNode) && directedGraph.containsVertex(desNode)){
                directedGraph.addEdge(srcNode, desNode);
            }

        }

        return directedGraph;

    }

}
