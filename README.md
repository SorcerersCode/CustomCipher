# Custom Encryption Algorithm

## Overview
This project implements a custom encryption algorithm inspired by classic cipher techniques. The algorithm combines three key components: **Substitution Cipher**, **Hill Cipher Matrix Multiplication**, and **Transposition Cipher**, to provide an additional layer of security.

## Key Structure
The encryption key consists of three main parts and has a total length of `26 + 4 + n`, where `n` represents the number of characters in the message to be encrypted or decrypted.

### 1. Substitution Key (First 26 Values)
The first 26 numbers in the key define a substitution mapping for the alphabet:
- The indexes `K[0] - K[25]` correspond to remapped letter positions
- For example, if `K[0] = 6, K[1] = 1, K[2] = 10`, then:
  - 'A' (position 1) is remapped to 'F' (position 6 in the alphabet)
  - 'B' (position 2) is remapped to 'A' (position 1 in the alphabet)
  - 'C' (position 3) is remapped to 'J' (position 10 in the alphabet)
- This mapping ensures that all 26 characters have unique replacements

### 2. Matrix Key (Next 4 Values)
The next four numbers represent a `2x2` key matrix used in the **Hill Cipher** transformation. This matrix is applied to character pairs using matrix multiplication:
- The plaintext is broken into 2-character blocks
- Each block is represented as a `2x1` vector
- It is multiplied by the `2x2` key matrix and reduced modulo 26
- Padding is added if the message length is not an even number

### 3. Transposition Key (Final `n` Values)
The last segment of the key consists of `n` values, where `n` is the length of the message. This defines a **randomized transposition (shuffling) of characters** in the final ciphertext:
- The transposition key assigns new positions to characters in the encrypted text
- This obfuscates frequency analysis and prevents heuristic attacks

---

## Encryption Process
### **User Input**
- The user enters a plaintext message
- Spaces are removed, and numbers are not allowed
- A unique key is generated and displayed along with the ciphertext

### **Encoding Logic**
1. **Preprocessing:**
   - Remove spaces and non-alphabetic characters
   - Convert characters to numeric values (`A=1, B=2, ..., Z=26`)

2. **Substitution Cipher:**
   - Map numeric values to their substituted values using the generated key

3. **Hill Cipher Matrix Multiplication:**
   - Split values into 2-character pairs
   - Perform matrix multiplication with the `2x2` key matrix
   - Apply modulo 26 to the results

4. **Transposition Cipher:**
   - Shuffle character positions based on the transposition key

5. **Output the Ciphertext and Key**

---

## Decryption Process
### **User Input**
- The user enters the cipher text
- The user then enters the encryption key
- Decoding takes place and prints out to the console the deciphered message

### **Decoding Logic**
The decryption process reverses each step using the provided key:
1. **Parse the Key:**
   - Extract substitution mapping, matrix key, and transposition sequence

2. **Reverse Transposition Cipher:**
   - Restore characters to their original positions

3. **Reverse Hill Cipher Matrix Multiplication:**
   - Apply the inverse of the `2x2` key matrix
   - Reverse the modulo 26 operation

4. **Reverse Substitution Cipher:**
   - Convert numeric values back to their original plaintext characters

5. **Output the Decoded Message**

---

## Notes
- This algorithm is purely educational and should not be used for real-world security applications
- The implementation demonstrates key principles of classical cryptography and was a project for my cryptography class
- Understanding each stage of encryption enhances knowledge of cryptographic techniques like **substitution**, **matrix transformation**, and **transposition obfuscation**.

---

## Future Possible Improvements
- Expand support for punctuation and numbers
- Implement larger matrix sizes for increased security
- Optimize key generation for better randomness

---

## Author
Dylan Tarace
