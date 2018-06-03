package com.apporiented.algorithm.clustering.similarity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {

    private String basePath;
    private String fileName;
    private String filePath;
    private List<String> words;

    private static String SPACE = " ";

    public Document(String basePath, String fileName) {
        this.basePath = basePath;
        this.fileName = fileName;
        filePath = basePath + fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getWords() {
        if (words == null) {
            parse();
        }
        return words;
    }

    public void parse() {
        Pattern pattern = Pattern.compile("[a-zăîâșț]+");
        List<String> list = new ArrayList<String>();
        Matcher m = pattern.matcher(getFileText().toLowerCase());
        while (m.find()) {
            list.add(m.group());
        }

        words = list;

      /*  words = Observable.just(getFileText())
                .map(text -> Arrays.asList(text.split("[^a-zA-Z-]+")))
                .flatMap(listWords -> Observable.from(listWords))
                .filter(word -> !word.equals("-") && !word.equals(" "))
                .toList()
                .toBlocking()
                .single();*/
    }

    private String getFileText() {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = bufferedReader.readLine();
            while (line != null) {
                text.append(line);
                line = bufferedReader.readLine();
                text.append(SPACE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(text);
    }

    public int frequencyOfWord(String word) {
        return Collections.frequency(words, word);
    }

}
