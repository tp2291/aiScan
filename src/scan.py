# Example: vulnerable_python_code.py
# WARNING: This code contains intentional security vulnerabilities for educational purposes.

import sqlite3
import os
import pickle

# Vulnerable to SQL Injection
def unsafe_sql_query(username, password):
    connection = sqlite3.connect('example.db')
    cursor = connection.cursor()
    query = f"SELECT * FROM users WHERE username = '{username}' AND password = '{password}'"
    cursor.execute(query)
    results = cursor.fetchall()
    cursor.close()
    connection.close()
    return results

# Vulnerable to Command Injection
def unsafe_system_command(filename):
    command = f"cat {filename}"
    os.system(command)

# Vulnerable to Insecure Deserialization
def unsafe_deserialization(serialized_data):
    obj = pickle.loads(serialized_data)
    return obj

# Hardcoded Sensitive Information
ADMIN_PASSWORD = "admin123"  # Should not be hardcoded

# Insecure Use of Cryptography
def weak_encryption(plaintext):
    from Crypto.Cipher import DES
    key = b'8bytekey'  # DES is an outdated and insecure encryption algorithm
    cipher = DES.new(key, DES.MODE_ECB)
    padded_plaintext = plaintext + b' ' * (8 - len(plaintext) % 8)  # Poor padding strategy
    encrypted_message = cipher.encrypt(padded_plaintext)
    return encrypted_message

# Main function to demonstrate the vulnerabilities (Do not run this code)
def main():
    # SQL Injection demonstration
    username = input("Enter username: ")
    password = input("Enter password: ")
    users = unsafe_sql_query(username, password)
    print(users)

    # Command Injection demonstration
    filename = input("Enter the filename to display: ")
    unsafe_system_command(filename)

    # Insecure Deserialization demonstration
    serialized_data = input("Enter serialized data: ")
    obj = unsafe_deserialization(serialized_data.encode())
    print(obj)

    # Weak Encryption demonstration
    plaintext = b"Secret Message"
    encrypted_message = weak_encryption(plaintext)
    print(encrypted_message)

if __name__ == "__main__":
    main()
