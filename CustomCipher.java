import java.util.*;

public class CustomCipher {

    private static final Map<Character, Integer> charToNum = new HashMap<>();

    static {
        // Initialize character mappings (A=1, B=2, ..., Z=26)
        for (int i = 0; i < 26; i++) {
            charToNum.put((char) ('A' + i), i + 1);
        }
    }

    /*
     * Encoding methods
     */
    public void encode(String plaintext, CipherKey encodeKey) {
        // Call Substitute Method
        String substitutionString = substitute(plaintext, encodeKey.getSubstitutionKey());
        //System.out.println("Substitution Result: " + substitutionString);

        // Call Product Method
        int[] productArray = makeProduct(substitutionString, encodeKey.getMatrixKey());
        //System.out.println("Product Result: " + Arrays.toString(productArray));

        // Call Transposition Method
        int[] transposeArray = transpose(productArray, encodeKey.getTranspositionKey());
        //System.out.println("Transposition Result: " + Arrays.toString(transposeArray));

        String finalCipher = mapToLetters(transposeArray);
        System.out.println("Resulting Cipher: " + finalCipher);
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private String substitute(String plaintext, Map<Integer, Integer> substitutionKey) {

        // Send the plaintext to be formatted and turned into an array of chars
        char[] plaintextChars = toCapitalChar(plaintext);
        if (plaintextChars == null) {
            return null;
        }

        // Turn the plaintext to their digit values for the substitution part
        int[] digitValues = toDigitValue(plaintextChars);

        // Maps the digit key to its substitution value and adds it to the string
        StringBuilder substitutionCipherString = new StringBuilder();
        for (int i = 0; i < plaintextChars.length; i++) {
            substitutionCipherString.append(substitutionKey.get(digitValues[i])).append(",");
        }

        // To remove the trailing "," char and makes parsing later on easier
        substitutionCipherString.setLength(substitutionCipherString.length() - 1);

        return substitutionCipherString.toString();
    }

    /*
     * Cleans up the string input into just an array of chars
     * that have no numbers, whitespaces, etc. Also capitalizes everything
     */
    private char[] toCapitalChar(String input) {

        // To remove any spaces in case multiple words are used
        input = input.replaceAll("\\s", "");

        // Makes sure we don't perform our cipher on characters other than letters
        if (input.matches(".*\\d.*") || input.length() == 0) {
            System.out.println("Error: Incorrect input detected");
            return null;
        }

        return input.toUpperCase().toCharArray();
    }

    /*
     * Converts alphabet characters a-z to their positional value [a=1, b=2, ...
     * z=26]
     * by using a predefined hash-map key-value pair
     * Key being the char and value being it's digit
     */
    private int[] toDigitValue(char[] plaintextChars) {

        int[] plaintextDigitValues = new int[plaintextChars.length];
        for (int i = 0; i < plaintextChars.length; i++) {
            plaintextDigitValues[i] = charToNum.get(plaintextChars[i]);
        }

        return plaintextDigitValues;
    }

    private int[] convertStringToIntArray(String stringToConvert) {
        // The String seperates the numbers by "," character so we use that to turn it
        // into an array
        String[] parts = stringToConvert.split(",");
        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }

        return result;
    }

    private int[] makeProduct(String substitutionString, int[][] keyMatrix) {
        // Convert the comma-separated string into an int array.
        int[] subIntArray = convertStringToIntArray(substitutionString);
        int n = subIntArray.length;

        // Ensure the array's length is a multiple of 2 (since each 2x1 vector has 2
        // numbers)
        if (n % 2 != 0) {
            int padding = 0; // Default padding value (0)
            int newLength = n + 1;
            int[] padded = new int[newLength];
            System.arraycopy(subIntArray, 0, padded, 0, n);
            padded[newLength - 1] = padding;
            subIntArray = padded;
            n = subIntArray.length;
        }

        int[] result = new int[n];

        // Process each 2x1 block (each pair of numbers).
        for (int i = 0; i < n; i += 2) {
            int x = subIntArray[i];
            int y = subIntArray[i + 1];

            // Multiply the 2x1 vector [x, y]^T by the 2x2 key matrix.
            int newX = (keyMatrix[0][0] * x + keyMatrix[0][1] * y) % 26;
            int newY = (keyMatrix[1][0] * x + keyMatrix[1][1] * y) % 26;

            // Adjust values to ensure they are within 1..26.
            if (newX <= 0) {
                newX += 26;
            }
            if (newY <= 0) {
                newY += 26;
            }

            result[i] = newX;
            result[i + 1] = newY;
        }
        return result;
    }

    private int[] transpose(int[] productArray, int[] transposeKey) {
        int[] transposedArray = new int[productArray.length];
        // Loop through each index of the transposeKey
        for (int i = 0; i < transposedArray.length; i++) {
            // Directly swap the elements according to transposeKey
            transposedArray[i] = productArray[transposeKey[i]];
        }

        return transposedArray;
    }

    private String mapToLetters(int[] numericValues) {
        StringBuilder result = new StringBuilder();

        for (int num : numericValues) {
            if (num == 0) {
                result.append('Z');
            }
            // A=1, B=2, ..., Z=26 which is why we -1
            char letter = (char) ('A' + num - 1);
            result.append(letter);
        }

        return result.toString();
    }

    /*
     * Decoding methods from here on below
     */
    public void decode(String cipherText, CipherKey decodedKey) {

        // Call decodeTransposition
        int[] decodedTransposition = decodeTransposition(cipherText, decodedKey.getTranspositionKey());
        //System.out.println("Restored Transposition: " + Arrays.toString(decodedTransposition));

        // Call decodeProduct
        int[] decodedProduct = decodeProduct(decodedTransposition, decodedKey.getMatrixKey());
        //System.out.println("Restored Product: " + Arrays.toString(decodedProduct));

        // Call decodeSubstitute
        int[] decodedSubstitute = decodeSubstitute(decodedProduct, decodedKey);

        // Convert the decodedSubstitute back into their letters
        String finalDecode = mapToLetters(decodedSubstitute);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Decoded Cipher: " + finalDecode);
        System.out.println("-----------------------------------------------------------------------------------");
    }

    // Swaps the positions of the ciphertext back
    private int[] decodeTransposition(String cipherText, int[] transpositionKey) {
        // Clean up cipherText and turn into array of chars for next method
        char[] cipherChars = toCapitalChar(cipherText);
        // Send the cipherText to be converted back into digitValues
        int[] cipherDigits = toDigitValue(cipherChars);

        // Create a new array to hold the decoded result
        int[] decodedArray = new int[cipherDigits.length];

        for (int i = 0; i < transpositionKey.length; i++) {
            // Reverses the transposition by maping the value back to its original position
            decodedArray[transpositionKey[i]] = cipherDigits[i];
        }

        return decodedArray;
    }

    // This method reverts the product operation that was done by performing the 
    // inverse of matrix multiplication of a vector
    private int[] decodeProduct(int[] decodedTransposition, int[][] matrixKey) {

        // Compute the determinant of the key matrix.
        int a = matrixKey[0][0];
        int b = matrixKey[0][1];
        int c = matrixKey[1][0];
        int d = matrixKey[1][1];
        int det = a * d - b * c;
        // Normalize determinant modulo 26.
        det = ((det % 26) + 26) % 26;

        // Compute the modular inverse of the determinant.
        int invDet = modInverse(det, 26);
        if (invDet == -1) {
            System.out.println("Error: Key matrix is not invertible modulo 26.");
            return null;
        }

        // Compute the inverse key matrix using the formula for 2x2 matrices:
        // inverse = (1/det) * [ d, -b; -c, a ]
        int[][] inverseKey = new int[2][2];
        inverseKey[0][0] = (d * invDet) % 26;
        inverseKey[0][1] = (-b * invDet) % 26;
        inverseKey[1][0] = (-c * invDet) % 26;
        inverseKey[1][1] = (a * invDet) % 26;

        // Normalize each entry to be in the range 1..26.
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                inverseKey[i][j] = ((inverseKey[i][j] % 26) + 26) % 26;
                if (inverseKey[i][j] <= 0) {
                    inverseKey[i][j] += 26;
                }
            }
        }

        // Process each 2x1 block in decodedTransposition.
        int n = decodedTransposition.length;
        int[] result = new int[n];

        // Iterate through each 2-element block.
        for (int i = 0; i < n; i += 2) {
            int x = decodedTransposition[i];
            int y = decodedTransposition[i + 1];

            // Multiply inverseKey by the 2x1 vector [x, y]^T.
            int origX = (inverseKey[0][0] * x + inverseKey[0][1] * y) % 26;
            int origY = (inverseKey[1][0] * x + inverseKey[1][1] * y) % 26;

            // Adjust if the result is zero or negative (to keep numbers 1-26).
            if (origX <= 0) {
                origX += 26;
            }
            if (origY <= 0) {
                origY += 26;
            }

            result[i] = origX;
            result[i + 1] = origY;
        }
        return result;
    }

    // Once we have the digit values, we need to revert them back to their original mapping
    private int[] decodeSubstitute(int[] decodedProduct, CipherKey decodedKey) {
        Map<Integer, Integer> inverseSubstitutionKey = decodedKey.getInverseSubstitutionKey();
        int[] originalValues = new int[decodedProduct.length];

        for (int i = 0; i < decodedProduct.length; i++) {
            originalValues[i] = inverseSubstitutionKey.getOrDefault(decodedProduct[i], decodedProduct[i]);
        }

        return originalValues;
    }

    // Computes the modular inverse of a modulo m
    private int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // No modular inverse exists if gcd(a, m) != 1
    }
}
