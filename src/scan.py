# Example: additional_vulnerable_python_code.py
# WARNING: This code contains intentional security vulnerabilities for educational purposes.

import subprocess
import flask
from flask import Flask, request, render_template_string

app = Flask(__name__)

# Vulnerable to Remote Code Execution (RCE)
@app.route('/rce')
def remote_code_execution():
    code_to_run = request.args.get('code')
    exec(code_to_run)  # Never use exec with untrusted input
    return "Code executed"

# Vulnerable to Server-Side Template Injection (SSTI)
@app.route('/ssti')
def server_side_template_injection():
    user_input = request.args.get('input')
    return render_template_string(f"Hello {user_input}!")  # User input is not sanitized

# Vulnerable to Path Traversal
@app.route('/readfile')
def read_file():
    filename = request.args.get('filename')
    with open(filename, 'r') as file:  # This could allow access to any file on the server
        content = file.read()
    return content

# Vulnerable to Subprocess Injection
@app.route('/subprocess')
def subprocess_injection():
    user_input = request.args.get('command')
    subprocess.run(user_input, shell=True)  # Never pass unsanitized user input to subprocess.run with shell=True
    return "Command executed"

if __name__ == "__main__":
    app.run(debug=True)
