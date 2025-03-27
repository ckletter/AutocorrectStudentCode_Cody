import java.util.ArrayList;
import java.util.LinkedList;

public class Trie {
    public static final int RADIX = 27;
    private final Prefix root;
    private int threshold;
    private char[] letterMap;
    private int[] toIndexMap;
    public Trie(int threshold) {
        root = new Prefix();
        this.threshold = threshold;
        this.letterMap = new char[RADIX];
        this.toIndexMap = new int[256];
        // Fill letter map with letters in alphabet
        for (int i = 0; i < RADIX - 1; i++) {
            this.letterMap[i] = (char) (i + 'a');
        }
        // Set the last index of the trie equal to the ' ascii
        this.letterMap[RADIX - 1] = (char) 39;
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

    public void editDistDFS(Prefix currentPrefix, String word, String currentString, int index, ArrayList<Autocorrect.Word> possibleWords, int edits) {
        // If more edits made than threshold, prune branch
        if (edits > threshold) {
            return;
        }
        // Prune branch if prefix does not exist in dictionary
        if (currentPrefix == null) {
            return;
        }
        // If valid word and an edit has been made, add to our possible words
        if (index == word.length() && currentPrefix.isWord() && edits > 0) {
            // Check if the word is already in possibleWords by comparing stored words
            boolean alreadyExists = false;
            for (Autocorrect.Word possibleWord : possibleWords) {
                if (possibleWord.getWord().equals(currentString)) {
                    alreadyExists = true;
                    break;
                }
            }

            // Only add if it's not already present
            if (!alreadyExists) {
                possibleWords.add(new Autocorrect.Word(edits, currentString));
            }
        }
        // If not yet at the length of the word
        if (index < word.length()) {
            // Case where nothing is changed, follows the word down
            editDistDFS(currentPrefix.getChildren()[toIndexMap[word.charAt(index)]], word, currentString + word.charAt(index), index + 1, possibleWords, edits);
            // Try all possible substitutions
            for (int j = 0; j < RADIX - 1; j++) {
                Prefix currentChild = currentPrefix.getChildren()[j];
                editDistDFS(currentChild, word, currentString+ letterMap[j], index + 1, possibleWords, edits + 1);
            }
        }
        for (int j = 0; j < RADIX - 1; j++) {
            Prefix currentChild = currentPrefix.getChildren()[j];
            // Addition
            editDistDFS(currentChild, word, currentString + letterMap[j], index, possibleWords, edits + 1);
        }
        // Deletion
        editDistDFS(currentPrefix, word, currentString, index + 1, possibleWords, edits + 1);
    }
    public void findCandidates(String word, ArrayList<Autocorrect.Word> possibleWords) {
        editDistDFS(root, word, "", 0, possibleWords, 0);
    }
}
