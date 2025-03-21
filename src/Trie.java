import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Trie {
    public static final int RADIX = 26;
    private final Prefix root;
    private int threshold;
    public Trie(int threshold) {
        root = new Prefix();
        this.threshold = threshold;
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
            if (currentSuffixes[letterIndex] == null) {
                currentSuffixes[letterIndex] = new Prefix();
            }
            // Go to the location of that newly created prefix
            currentPrefix = currentSuffixes[letterIndex];
        }
        // Once at the end of the current word, set that prefix to existing in the dictionary
        currentPrefix.setWord();
    }

    public void editDistDFS(Prefix currentPrefix, String word, String currentString, int index, ArrayList<String> possibleWords, int thresholdDist) {
        if (thresholdDist == 0) {
            return;
        }
        // Follow rest of word
        followRestOfWord(currentPrefix, word, currentString, index, possibleWords);
        for (int j = 0; j < RADIX; j++) {
            Prefix currentChild = currentPrefix.getChildren()[j];
            // Addition
            editDistDFS(currentChild, word, currentString + (j + 'a'), index, possibleWords, thresholdDist - 1);
            // Deletion
            editDistDFS(currentChild, word, currentString + (j + 'a'), index + 1, possibleWords, thresholdDist - 1);
            // Substitution
            editDistDFS(currentPrefix, word, currentString + (j + 'a'), index - 1, possibleWords, thresholdDist - 1);
        }
    }
    public void followRestOfWord(Prefix current, String word, String currentString, int index, ArrayList<String> possibleWords) {
        StringBuilder currentStringBuilder = new StringBuilder(currentString);
        for (int j = index; j < word.length(); j++) {
            current = current.getChildren()[word.charAt(index) - 'a'];
            currentStringBuilder.append(word.charAt(index) - 'a');
        }
        currentString = currentStringBuilder.toString();
        if (current.isWord() && !currentString.equals(word)) {
            possibleWords.add(currentString);
        }
    }
    public void findCandidates(String word) {
            ArrayList<String> possibleWords = new ArrayList<>();
            editDistDFS(root, word, "", 0, possibleWords, 0);
        }


    }
}
