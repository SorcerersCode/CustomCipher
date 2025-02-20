import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner keyboard = new Scanner(System.in)) {
            int inputValue = 0;
            CustomCipher customCipher = new CustomCipher();
            do {
                System.out.print("Do you want to:\n(1)Encode\n(2)Decode\n(3)Quit");
                System.out.print("\nEnter Selection: ");
                String input = keyboard.nextLine();
                try {
                    inputValue = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Only enter a number corresponding to the operation you want done");
                }

                // ENCODING OPTION SELECTED
                if (inputValue == 1) {
                    System.out.println("Encoding Option Selected");
                    System.out.print("Enter Text you want to encode: ");
                    String plaintext = keyboard.nextLine();
                    // Need to remove whitespaces and such to prevent it being taken into account when transpose calculates it's size
                    String cleanedText = plaintext.replaceAll("\\s+", "");
                    CipherKey encodeKey = new CipherKey(cleanedText.length());
                    System.out.println("-----------------------------------------------------------------------------------");
                    System.out.println("Key Generated: " + Arrays.toString(encodeKey.getMasterKey()) + "\n");
                    customCipher.encode(plaintext, encodeKey);

                    // DECODING OPTION SELECTED
                } else if (inputValue == 2) {
                    System.out.println("Decoding Operation Selected");
                    System.out.print("Enter the ciphertext you wish to decode: ");
                    String cipherText = keyboard.nextLine();
                    System.out.print("Cyphertext saved\nNow enter the key to decode it: ");
                    String submittedKeyStr = keyboard.nextLine();
                    int[] submittedKey = Arrays.stream(submittedKeyStr.split(","))
                            .map(String::trim) // Trim any spacing
                            .mapToInt(Integer::parseInt) // Converts to integers
                            .toArray();
                    // Create a new key instance for decoding with predefined key
                    CipherKey decodeKey = new CipherKey(submittedKey);
                    // Submit it to the customCipher class to decode
                    customCipher.decode(cipherText, decodeKey);

                } else if (inputValue == 3) {
                    System.out.println("Quit operation detected, now closing application...");
                    inputValue = -1;
                    // SYSTEM ERROR MESSAGE FOR ANY OTHER INPUT
                } else if (inputValue != -1) {
                    System.out.println("Error: Erogenous number input entered\nTry again...");
                }

            } while (inputValue != -1);
        }
    }
}
