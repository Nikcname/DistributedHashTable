package com.company;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;

public class OpenFile {

    private String fileName;

    public OpenFile(String fileName) {
        this.fileName = fileName;
    }

    public List<String> readFileInList(){
        List<String> lines = Collections.emptyList();

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

}
