# Approach

This program is capable of processing any json and can handle an incomplete json file.

The solution is around taking a json file and reading it as a string. The program goes through each of the characters in
the json file and parses them as you go through each of the characters. When you go through the file, you encounter
either a string, boolean, number, null, list or an object. The program handles the parsing for each scenario to
construct the Java object and return the object to the user at the end.

# Design

It is useful to think of the string of the json file as a tape of different values that is being parsed.

The methods hasNextChar(), nextChar(), peekChar() and skipWhitespace() handle traversing the tape for parsing the string
of the json.

The methods object(), list() handles parsing an object and list respectively.

The method token() handles each type of token based on the character encountered.

The methods parseJsonNull(), parseJsonBoolean(), parseJsonStringLiteral(), parseJsonNumericLiteral() handle the null,
boolean, string and numeric input.

The methods parseObject() and parseArray() handle objects and array objects, and is called through the token() method.

# How to run the code

Place the json file you want to parse into the main folder.

Open your terminal and enter the following command:

    "java JsonParser.java"

Enter the filename of the file when you want to parse when prompted.

The output should show the parsedJson object


