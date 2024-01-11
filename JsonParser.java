import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

public class JsonParser {

    private String input;
    private int index;


    /**
     * Constructor for the class
     * @param jsonString takes in the string of the file
     */
    public JsonParser(String jsonString) {
        this.input = jsonString;
        this.index = 0;
    }

    // Functions for parser

    /**
     * Checks if the next character is available
     * @return true if not EOF
     */
    private boolean hasNextChar() {
        return index < input.length();
    }

    /**
     * Go to the next character
     * @return the next character
     */
    private char nextChar() {
        if (!hasNextChar()) {
            throw new IllegalStateException("Unexpected end of input");
        }

        char returnChar = input.charAt(index++);
        System.out.println("Processing character '" + returnChar + "'");
        return returnChar;
    }

    /**
     * Checks the current character
     * @return returns the current character
     */
    private char peekChar() {
        if (!hasNextChar()) {
            throw new IllegalStateException("Unexpected end of input");
        }

        return input.charAt(index);
    }

    /**
     * Skips whitespace on the file
     */
    private void skipWhitespace() {

        //System.out.println(!hasNextChar());
        //System.out.println(Character.isWhitespace(peekChar()));
        while (hasNextChar() & Character.isWhitespace(peekChar())) {
            System.out.println("skipping whitespace");
            nextChar();

        }
        System.out.println("Not skipping whitespace");
    }

    // Methods for parsing each type of token

    /**
     * Parses object
     * @return a LinkedHashMap of the key value pairs of the object
     * @throws ParseException
     */
    private LinkedHashMap<String, Object> object() throws ParseException {

        // Instantiate the object
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // Check if the object is starting with a '{'
        if (nextChar() != '{') {
            return result;
            //throw new ParseException("Error parsing json object not starting with '{'"  + (index - 1), index - 1);
        }

        while (hasNextChar()) {
            // Skip whitespace until you find the next character
            skipWhitespace();
            // Getting the key value by parsing what is within quotes
            String key = "";
            if (peekChar() == '\"') {
                key = parseJsonStringLiteral();
            } else if (peekChar() == '}') {
                System.out.println("if statement } calling the nextChar()");
                if (!hasNextChar()) {
                    return result;
                }
                // if the object is empty, then return empty linkedhashmap
                nextChar();
                return result;
            }

            if (!hasNextChar()) {
                result.put(key, null);
                return result;
            }
            // Skipping whitespace after the key
            skipWhitespace();

            // Getting the delimiter that separates key and value
            if (!hasNextChar()) {
                result.put(key, null);
                return result;
            }


            if (nextChar() == ':') {
                skipWhitespace();
                Object value = token();
                result.put(key, value);

                if (!hasNextChar()) {
                    return result;
                }

                skipWhitespace();
            } else {
                // Throw exception if there is no delimiter
                return result;
                //throw new ParseException("Expected token ':' at index "  + (index - 1), index - 1);
            }

            // Completing the object
            if (peekChar() == '}') {
                System.out.println("Completing the object calling nextChar()");

                nextChar();
                return result;
            } else if (peekChar() == ','){

                nextChar();
                if (!hasNextChar()) {
                    return result;
                }
                skipWhitespace();
                // continue in the loop in this case
            } else {
                return result;
                // Object is not completed but there aren't more key value pairs
                //throw new ParseException("Object not completed with '}' at the end"  + (index - 1), index - 1);
            }
        }


        return result;
        //throw new ParseException("Reached EOF before parsing the objects"  + (index - 1), index - 1);
    }

    /**
     * Parses the json string
     * @return string literal
     */
    private String parseJsonStringLiteral() {
        System.out.println("jsonStringLiteral");
        StringBuilder jsonString = new StringBuilder();
        // Skip first character which is a double quote character
        if (!hasNextChar()) {
            return jsonString.toString();
        }
        nextChar();

        if (!hasNextChar()) {
            return jsonString.toString();
        }
        // While there is no double quote, keep adding it to json for parsing
        while (peekChar() != '\"') {
            jsonString.append(nextChar());
            if (!hasNextChar()) {
                return jsonString.toString();
            }
        }
        // Skip the final double quote character
        if (!hasNextChar()) {
            return jsonString.toString();
        }
        nextChar();
        skipWhitespace();

        return jsonString.toString();
    }

    private Object parseJsonNumericLiteral() throws ParseException {
        System.out.println("jsonNumericLiteral");
        StringBuilder jsonString = new StringBuilder();
        // Get all the digits before the ',' character and the whitespace character
        while (peekChar() != ',' & !Character.isWhitespace(peekChar())) {
            jsonString.append(nextChar());
            if (!hasNextChar()) {
                return jsonString.toString();
            }
        }

        // If there is a '.' character inside the string, its double, otherwise its integer
        try {
            System.out.println(peekChar());
            System.out.println(jsonString.toString());
            return Double.parseDouble(jsonString.toString());
        } catch (NumberFormatException e) {
            return jsonString.toString();
            //throw new ParseException("Issue parsing the number at index "  + (index - 1), index - 1);
        }

    }

    /**
     * Parses a json
     * @return a boolean if its true or false, otherwise a null
     * @throws ParseException
     */
    private Object parseJsonBoolean() throws ParseException{
        System.out.println("jsonBoolean");
        StringBuilder jsonString = new StringBuilder();
        while ((peekChar() != ',' & peekChar() != '}')  || Character.isWhitespace(peekChar())) {
            jsonString.append(nextChar());
            if (!hasNextChar()) {
                return jsonString.toString();
            }
        }


        if (jsonString.toString().equalsIgnoreCase("true")) {
            return true;
        } else if (jsonString.toString().equalsIgnoreCase("false")){
            return false;
        } else {
            return null;
        }
    }

    /**
     * Parse a null
     * @return null if there is a null, empty quotes otherwise
     * @throws ParseException
     */
    private Object parseJsonNull() throws ParseException {
        System.out.println("parseJsonNull");
        StringBuilder jsonString = new StringBuilder();


        while ((peekChar() != ',' & peekChar() != '}') || Character.isWhitespace(peekChar())) {
            jsonString.append(nextChar());
            System.out.println(peekChar() != ',');
            System.out.println(peekChar() != '}');
            System.out.println(Character.isWhitespace(peekChar()));
        }

        System.out.println(peekChar());
        System.out.println(jsonString.toString());
        if (jsonString.toString().equalsIgnoreCase("null")) {
            System.out.println("Returned null");
            return null;
        } else {
            return "";
            //throw new ParseException("Error parsing null value", index - 1 );
        }
    }

    /**
     * Parse a list
     * @return returns a list when parsing the object
     * @throws ParseException
     */
    private ArrayList<Object> list() throws ParseException {
        System.out.println("list");
        ArrayList<Object> result = new ArrayList<>();

        // Skip the '[' character
        if (!hasNextChar()) {
            return result;
        }
        nextChar();
        if (!hasNextChar()) {
            return result;
        }
        skipWhitespace();

        if (peekChar() == ']') {
            if (!hasNextChar()) {
                return null;
            }
            nextChar();
            if (!hasNextChar()) {
                return result;
            }
            return result;
        }

        // Making sure list is not empty
        while (hasNextChar()) {
            skipWhitespace();

            Object value = token();
            System.out.println("Adding to list <" + value + ">");
            result.add(value);

            if (!hasNextChar()) {
                return result;
            }

            skipWhitespace();

            if (peekChar() == ']') {
                System.out.println("Closing out array");
                if (!hasNextChar()) {
                    return result;
                }
                nextChar();
                return result;
            } else if (peekChar() != ',') {
                System.out.println(peekChar());
                return result;
                //throw new ParseException("Error parsing list of json, expected ',' at index ", index - 1);
            } else {
                System.out.println(peekChar());
                System.out.println("Skipping ',' character");
                if (!hasNextChar()) {
                    return result;
                }
                nextChar();
                if (!hasNextChar()) {
                    return result;
                }
            }
        }

        return null;
        //throw new ParseException("Unexpected end of input", index - 1);
    }

    /**
     * Parsing tokens as each token comes up
     * @return result after parsing each token type
     * @throws ParseException
     */
    private Object token() throws ParseException {
        System.out.println("token");
        Object result = null;
        if (peekChar() == '[') {
            result = parseArray();
        } else if (peekChar() == '\"') {
            result = parseJsonStringLiteral();
        } else if (Character.isDigit(peekChar())) {
            result = parseJsonNumericLiteral();
        } else if (peekChar() == 't' || peekChar() == 'f') {
            result = parseJsonBoolean();
        } else if (peekChar() == 'n' ) {
            result = parseJsonNull();
        } else if (peekChar() == '{') {
            result = parseObject();
        } else {
            result = null;
            //throw new ParseException("Error parsing key with character '" + peekChar() + "' at index"  + (index - 1), index - 1);
        }
        return result;
    }

    /**
     * Parse object
     * @return the parsed object as a linkedhashmap
     * @throws ParseException
     */
    public LinkedHashMap<String, Object> parseObject() throws ParseException {
        LinkedHashMap<String, Object> returnObject = object();
        return returnObject;
    }

    /**
     * Parse array
     * @return the parsed array as an arraylist
     * @throws ParseException
     */
    public ArrayList<Object> parseArray() throws ParseException {
        ArrayList<Object> returnArray = list();
        System.out.println("List : " +  returnArray.toString());

        return returnArray;
    }

    /**
     * Convert file to string
     * @param file string of filename
     * @return string of file's contents
     * @throws Exception
     */
    public static String convertFileIntoString(String file) throws Exception
    {
        String result;
        result = new String(Files.readAllBytes(Paths.get(file)));
        return result;
    }



    public static void main(String[] args) {

        ArrayList<String> files = new ArrayList<>();
        files.add("example1.json");
        files.add("example2.json");
        files.add("example3.json");

        String fileName, file, location;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the name of the json file to parse: ");
        // take input from user and initialize filName variable
        fileName = sc.nextLine();
        try {
            file = convertFileIntoString(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Replacing the file's new line, return or tab character
        file = file.replace("\n", "").replace("\r", "").replace("\t", "");

        JsonParser parser = new JsonParser(file);
        try {
            LinkedHashMap<String, Object> jsonObject = parser.parseObject();
            System.out.println("Parsed JSON Object: " + jsonObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
