package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {

    private String pathToFile;
    private int keySpaceInt[];

    public Parser(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public int[] getKeySpace(){

        String keySpaceStr[];
        keySpaceInt = new int[0];

        OpenFile openFile = new OpenFile(pathToFile);
        Iterator<String> iterator = openFile.readFileInList().iterator();

        while (iterator.hasNext()){

            String line = iterator.next();

            if (line.equals("#key-space")) {

                keySpaceStr = iterator.next().split(",");
                keySpaceInt = new int[keySpaceStr.length];

                for (int i = 0; i < keySpaceStr.length; i++) {
                    keySpaceInt[i] = Integer.parseInt(keySpaceStr[i].replaceAll(" ", ""));
                }

                Arrays.sort(keySpaceInt);

            }

        }

        return keySpaceInt;
    }

    public int[] getNodes(){

        String nodesStr[];
        int nodesInt[] = new int[0];

        OpenFile openFile = new OpenFile(pathToFile);
        Iterator<String> iterator = openFile.readFileInList().iterator();

        while (iterator.hasNext()){

            String line = iterator.next();

            if (line.equals("#nodes")){

                nodesStr = iterator.next().split(",");

                List<String> tempList = new ArrayList<>(Arrays.asList(nodesStr));
                List<String> removeList = new ArrayList<>();
                for (String s: tempList){
                    int tempEl = Integer.parseInt(s.replaceAll(" ", ""));
                    if (tempEl < keySpaceInt[0]
                            || tempEl > keySpaceInt[1]){
                        removeList.add(s);
                    }
                }

                for (String s: removeList){
                    tempList.remove(s);
                    System.out.println("Node: " + s + " is not in range " +keySpaceInt[0] + "-" + keySpaceInt[1]);
                }

                nodesStr = tempList.toArray(new String[0]);

                nodesInt = new int[nodesStr.length];

                for (int i = 0; i < nodesStr.length; i++){
                    nodesInt[i] = Integer.parseInt(nodesStr[i].replaceAll(" ", ""));
                }

                Arrays.sort(nodesInt);

            }

        }

        return nodesInt;

    }

    public String[] getShortcuts(){

        String shortcutsStr[] = new String[0];

        OpenFile openFile = new OpenFile(pathToFile);
        Iterator<String> iterator = openFile.readFileInList().iterator();

        while (iterator.hasNext()) {

            String line = iterator.next();

            if (line.equals("#shortcuts")) {

                shortcutsStr = iterator.next().split(",");

                for (int i = 0; i < shortcutsStr.length; i++){
                    shortcutsStr[i] = shortcutsStr[i].replaceAll(" ", "");
                }

            }
        }
        
        return shortcutsStr;

    }

}
