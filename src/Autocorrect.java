import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.params.shadow.com.univocity.parsers.common.NormalizedString.toArray;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Cody Kletter
 */
public class Autocorrect {
    private int threshold;
    private String[] words;
    public static class Word implements Comparable<Word> {
        private int editDistance;
        private String word;
        public Word(int editDistance, String word) {
            this.editDistance = editDistance;
            this.word = word;
        }

        public int getEditDistance() {
            return editDistance;
        }

        public String getWord() {
            return word;
        }

        @Override
        public int compareTo(Word other) {
            if (this.editDistance != other.editDistance) {
                return Integer.compare(this.editDistance, other.editDistance);
            }
            return this.word.compareTo(other.word);
        }
    }
    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        this.threshold = threshold;
        this.words = words;
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distance, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        Trie trieDict = new Trie(threshold);
        // Add each word in the dictionary to the trie
        for (String word : words) {
            trieDict.insert(word);
        }
        ArrayList<Word> possibleWords = new ArrayList<Word>();
        // Find all candidate words within threshold distance away
        trieDict.findCandidates(typed, possibleWords);
        // Sort, first by threshold, then alphabetical
        possibleWords.sort(null);
        String[] similarArray = new String[possibleWords.size()];
        for (int i = 0; i < possibleWords.size(); i++) {
            similarArray[i] = possibleWords.get(i).getWord();
//            System.out.println(possibleWords.get(i).getWord());
        }
        return similarArray;
//        return possibleWords.toArray(new Word[0]);

        // Calculate levenshtein distance
//        ArrayList<Word> similar = new ArrayList<Word>();
//        for (String word : words) {
//            int editDistance = editDistance(word, typed);
//            if (editDistance <= threshold) {
//                Word thisWord = new Word(editDistance, word);
//                similar.add(thisWord);
//            }
//        }
//        similar.sort(null);
//        String[] similarArray = new String[similar.size()];
//        for (int i = 0; i < similar.size(); i++) {
//            similarArray[i] = similar.get(i).getWord();
//        }
//        return similarArray;
    }
    public int editDistance(String typed, String dict) {
        int[][] lev = new int[typed.length() + 1][dict.length() + 1];
        // Fill first row with 0, 1, 2...
        for (int i = 0; i < lev[0].length; i++) {
            lev[0][i] = i;
        }
        // Fill first col with 0, 1, 2...
        for (int j = 0; j < lev.length; j++) {
            lev[j][0] = j;
        }
        for (int i = 1; i < lev.length; i++) {
            for (int j = 1; j < lev[0].length; j++) {
                if (typed.charAt(i - 1) == dict.charAt(j - 1)) {
                    lev[i][j] = lev[i - 1][j - 1];
                }
                else {
                    lev[i][j] = Math.min(Math.min(lev[i - 1][j], lev[i][j - 1]), lev[i - 1][j - 1]) + 1;
                }
            }
        }
        return lev[typed.length()][dict.length()];
    }
//    public String[] getTokens(String word, int grams) {
//        String[] tokens = new String[word.length() - grams + 1];
//        for (int i = 0; i <= word.length() - grams; i++) {
//            tokens[i] = word.substring(i, i + grams);
//        }
//        return tokens;
//    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run() {
        Scanner s = new Scanner(System.in);
        // Get user word
        System.out.println("Type a word:");
        String word = s.nextLine();
        String[] possibleWords = runTest(word);
        // Print out all possible matching words to inputted word, up to 3 maximum
        if (possibleWords.length == 0) {
            System.out.println("No matches found.");
        }
        else if (possibleWords.length == 1) {
            System.out.println("Did you mean...");
            System.out.println(possibleWords[0]);
        }
        else if (possibleWords.length == 2) {
            System.out.println("Did you mean...");
            System.out.println(possibleWords[0]);
            System.out.println(possibleWords[1]);
        }
        else {
            System.out.println("Did you mean...");
            for (int i = 0; i < 3; i++) {
                System.out.println(possibleWords[i]);
            }
        }
    }
    public static void main(String[] args) {
        String[] words = loadDictionary("large");
        Autocorrect autocorrect = new Autocorrect(words, 2);
        autocorrect.run();
    }
}