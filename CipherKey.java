import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CipherKey {

    private int[] masterKey;
    private final Map<Integer, Integer> substitutionKey = new HashMap<>();
    private int[] matrixKey;
    private int[] transpositionKey;

    // Constructor for when a key is provided/predefined
    public CipherKey(int[] predefinedKey) {
        if (predefinedKey.length < 30) {
            throw new IllegalArgumentException("Error: Master key must be at least 30 integers long");
        }
        this.masterKey = predefinedKey.clone();
        parseKeys();
    }

    // Constructor for when a key is needed to be generated
    // We need the size of message to generate the transposition key size
    public CipherKey(int sizeOfMessage) {
        // Ensure sizeOfMessage accounts for 2x2 matrix padding for when the string falls short of %2
        int paddedSize = (sizeOfMessage % 2 == 0) ? sizeOfMessage : sizeOfMessage + (2 - (sizeOfMessage % 2));
        this.masterKey = generateRandomKey(paddedSize);
        parseKeys();
    }

    private int[] generateRandomKey(int sizeOfMessage) {
        Random randomGenerator = new Random();
        // 26 for substitution, 4 for matrix, remaining for transposition
        int totalKeySize = 26 + 4 + sizeOfMessage;
        int[] masterKey = new int[totalKeySize];

        // Generates unique substitution key
        List<Integer> substitutionPool = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            substitutionPool.add(i);
        }
        // Shuffle and store substitution key values
        Collections.shuffle(substitutionPool);
        for (int i = 0; i < 26; i++) {
            masterKey[i] = substitutionPool.get(i);
        }

        // Generates matrix key (4 random numbers between 1-26) ensuring invertibility
        // modulo 26. If it isn't invertable then you become a betting man every time
        boolean invertible = false;
        while (!invertible) {
            // Generate 4 random numbers for the matrix key
            for (int i = 26; i < 30; i++) {
                masterKey[i] = randomGenerator.nextInt(26) + 1;
            }
            // Extract matrix elements
            int a = masterKey[26];
            int b = masterKey[27];
            int c = masterKey[28];
            int d = masterKey[29];
            // Compute determinant: det = a*d - b*c
            int det = a * d - b * c;
            // Normalize determinant modulo 26 make sure it's positive otherwise it breaks :(
            int normalizedDet = ((det % 26) + 26) % 26;
            // Check if the determinant is invertible modulo 26 (gcd == 1)
            if (gcd(normalizedDet, 26) == 1) {
                invertible = true;
            }
        }

        // Generates unique transposition key
        List<Integer> transpositionPool = new ArrayList<>();
        for (int i = 0; i < sizeOfMessage; i++) {
            transpositionPool.add(i);
        }
        // Shuffle the positions and save to key
        Collections.shuffle(transpositionPool);
        for (int i = 30; i < totalKeySize; i++) {
            masterKey[i] = transpositionPool.get(i - 30);
        }

        return masterKey;
    }

    // Needed to compute the GCD
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // Parses the parts of the keys needed
    private void parseKeys() {
        parseSubstitutionKey();
        parseMatrixKey();
        parseTranspositionKey();
    }

    private void parseSubstitutionKey() {
        // Map the first 26 characters of the key to the hash map
        for (int i = 0; i < 26; i++) {
            substitutionKey.put(i + 1, masterKey[i]);
        }
    }

    private void parseMatrixKey() {
        // The matrixKey is defined as a 2x2 matrix so we take the next 4 integers
        matrixKey = new int[4];

        // The matrix key sits after the substitution key so we shift the index to
        // accomidate
        for (int i = 0; i < 4; i++) {
            matrixKey[i] = masterKey[26 + i];
        }
    }

    private void parseTranspositionKey() {

        // The size of the transposition key is the remaining size of the array after
        // the other two keys so we calculate that here and iterate until the end
        transpositionKey = new int[masterKey.length - 30];
        for (int i = 0; i < transpositionKey.length; i++) {
            transpositionKey[i] = masterKey[30 + i];
        }
    }

    /*
     * Accessors
     * 
     * I send back a copy of the values just because the key should not be changed
     * in any way. This helps maintain the integrity of the key
     */
    public int[] getMasterKey() {
        return masterKey.clone();
    }

    public Map<Integer, Integer> getSubstitutionKey() {
        return new HashMap<>(substitutionKey);
    }

    // Used to convert the digit values back to their original mapping in decoding
    public Map<Integer, Integer> getInverseSubstitutionKey() {
        Map<Integer, Integer> inverseSubstitutionKey = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : substitutionKey.entrySet()) {
            inverseSubstitutionKey.put(entry.getValue(), entry.getKey());
        }
        return inverseSubstitutionKey;
    }

    public int[][] getMatrixKey() {
        // The CustomCipher class needs a Matrix as a key so we return it as such here
        int[][] matrix = new int[2][2];
        matrix[0][0] = matrixKey[0]; // Assign the 1st element to matrix[0][0]
        matrix[0][1] = matrixKey[1]; // Assign the 2nd element to matrix[0][1]
        matrix[1][0] = matrixKey[2]; // Assign the 3rd element to matrix[1][0]
        matrix[1][1] = matrixKey[3]; // Assign the 4th element to matrix[1][1]
        return matrix;
    }

    public int[] getTranspositionKey() {
        return transpositionKey.clone();
    }

}
