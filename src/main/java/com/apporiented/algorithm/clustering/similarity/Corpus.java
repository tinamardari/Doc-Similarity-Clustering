package com.apporiented.algorithm.clustering.similarity;
import java.io.*;
import java.util.*;

public class Corpus {

    private final String WORDS_MATRIX_FILE = "WordsMatrix.txt";
    private final String PARSED_WORDS_FILE = "ParsedWordsFromDocs.txt";
    private String path;
    private List<Document> documents = new ArrayList<>();
    private Map<String, List<String>> wordsMatrix = new HashMap<>();
    private List<String> allWords = new ArrayList<>();
    private boolean forceRefresh;
    int[][] freqMatrix;

    public Corpus(String path, String[] fileNames, boolean forceRefresh) {
        this.path = path;
        this.forceRefresh = forceRefresh;
        initDocuments(fileNames);

        createFreqMatrix();
        printStats();
        saveFreqMatrix();
    }

    private void createFreqMatrix() {
        if (new File(path + WORDS_MATRIX_FILE).exists() && !forceRefresh) {
            readFreqMatrix();
            System.out.println("WARNING: Frequency matrix was read from file. The matrix might be outdated\n");
            return;
        }

        extractWordsFromDocs();
        findAllDistinctWords();
        freqMatrix = new int[documents.size()][allWords.size()];
        for (int i = 0; i < documents.size(); i++) {
            for (int j = 0; j < allWords.size(); j++) {
                String word = allWords.get(j);
                freqMatrix[i][j] = documents.get(i).frequencyOfWord(word);
            }
        }
    }

    private void initDocuments(String[] fileNames) {
        for (String file : fileNames) {
            documents.add(new Document(path, file));
        }
    }

    private void extractWordsFromDocs() {
        for (Document doc : documents) {
            wordsMatrix.put(doc.getFileName(), doc.getWords());
        }
    }

    private void findAllDistinctWords() {
        Set<String> words = new HashSet<>();
        for (Document document : documents) {
            words.addAll(wordsMatrix.get(document.getFileName()));
        }
        allWords = new ArrayList<>(words);
        Collections.sort(allWords, (s1, s2) -> s1.compareToIgnoreCase(s2));
    }

    public void saveFreqMatrix() {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path + WORDS_MATRIX_FILE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(allWords);
            objectOutputStream.writeObject(freqMatrix);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFreqMatrix() {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(path + WORDS_MATRIX_FILE);
            objectInputStream = new ObjectInputStream(fileInputStream);
            allWords = (List<String>) objectInputStream.readObject();
            freqMatrix = (int[][]) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveWordsFromDocsInFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + PARSED_WORDS_FILE));
            for (Document doc : documents) {
                writer.write("Document: " + doc.getFileName());
                writer.newLine();
                writer.write("\tWords:" + wordsMatrix.get(doc.getFileName()));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<String> getAllWords() {
        return allWords;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    private void printStats() {
        System.out.println("CORPUS INFO");
        System.out.println("\t------------------------------------");
        System.out.println("\tTotal Documents: " + documents.size());
        System.out.println("\tTotal Distinct Words: " + allWords.size());
        System.out.println("\t------------------------------------");
    }

    public int[][] getFreqMatrix() {
        return freqMatrix;
    }
}
