import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Trie {
    public static final int RADIX = 27;
    private final Prefix root;
    private int threshold;
    private int[] letterMap;
    private int[] toIndexMap;
    public Trie(int threshold) {
        root = new Prefix();
        this.threshold = threshold;
        this.letterMap = new int[RADIX];
        this.toIndexMap = new int[256];
        // Fill letter map with letters in alphabet
        for (int i = 0; i < RADIX - 1; i++) {
            this.letterMap[i] = i + 'a';
        }
        // Set the last index of the trie equal to the ' ascii
        this.letterMap[RADIX - 1] = 39;
        // Fill to index map with letters in alphabet
        for (int i = 0; i < RADIX - 1; i++) {
            this.toIndexMap[i + 'a'] = i;
        }
        // Set the last index of the trie equal to the ' ascii
        this.toIndexMap[39] = RADIX - 1;
    }
    private class Prefix {
        private boolean isWord;
        private Prefix[] children;

        public Prefix() {
            this.isWord = false;
            children = new Prefix[RADIX];
        }

        public void setWord() {
            isWord = true;
        }

        public Prefix[] getChildren() {
            return children;
        }

        public boolean isWord() {
            return isWord;
        }
    }
    public void insert(String word) {
        Prefix currentPrefix = root;
        // Iterate through each letter
        int length = word.length();
        for (int i = 0; i < length; i++) {
            // Get the current letter
            int letterIndex = word.charAt(i);
            Prefix[] currentSuffixes = currentPrefix.getChildren();
            // If that prefix does not exist in the currentPrefix's children
            // Create new child of that prefix with current letter added
            if (currentSuffixes[toIndexMap[letterIndex]] == null) {
                currentSuffixes[toIndexMap[letterIndex]] = new Prefix();
            }
            // Go to the location of that newly created prefix
            currentPrefix = currentSuffixes[toIndexMap[letterIndex]];
        }
        // Once at the end of the current word, set that prefix to existing in the dictionary
        currentPrefix.setWord();
    }

    public void editDistDFS(Prefix currentPrefix, String word, String currentString, int index, ArrayList<String> possibleWords, int edits) {
        // If more edits made than threshold, prune branch
        if (edits > threshold) {
            return;
        }
        // Prune branch if prefix does not exist in dictionary
        if (currentPrefix == null) {
            return;
        }
        // If valid word, add to our possible words
        if (index == word.length() && currentPrefix.isWord() && !currentString.equals(word)) {
            possibleWords.add(currentString);
        }
        if (index < word.length()) {
            // Case where nothing is changed, follows the word down
            editDistDFS(currentPrefix, word, currentString + word.charAt(index), index + 1, possibleWords, edits);
            // Try all possible substitutions
            for (int j = 0; j < RADIX; j++) {
                editDistDFS(currentPrefix, word, currentString + letterMap[j], index + 1, possibleWords, edits + 1);
            }
        }
        for (int j = 0; j < RADIX; j++) {
            Prefix currentChild = currentPrefix.getChildren()[j];
            // Addition
            editDistDFS(currentChild, word, currentString + letterMap[j], index, possibleWords, edits + 1);
        }
        // Deletion
        editDistDFS(currentPrefix, word, currentString, index + 1, possibleWords, edits + 1);
    }
    public void findCandidates(String word) {
            ArrayList<String> possibleWords = new ArrayList<>();
            editDistDFS(root, word, "", 0, possibleWords, 0);
        }


    }
}
