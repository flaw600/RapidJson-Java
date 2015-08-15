/*
 * 
 */
package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.JsonException;
import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * A Class that converts a {@link InputStream} to a JsonInputStream
 */
public final class JsonInputStream
implements Closeable {
    
    /** The InputStream to be parsed. */
    private final InputStream in;
    
    /** The placeholder for the current character being parsed . */
    private char peek = '\0';
    
    /** The buffer for the name of the {@link JsonInputStream}. */
    private String name;
    
    /** The buffer for the current value being parsed. */
    private String buffer;

    /**
     * Instantiates a new JsonInputStream.
     *
     * @param in the InputStream to be converted
     */
    public JsonInputStream(InputStream in){
        if(in == null) throw new NullPointerException("input == null");
        this.in = in;
    }

    /**
     * Parses the {@link Value} based on whether the {@link #peek}.
     *
     * @return the parsed Value
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JsonException Signals that a JSON Exception has occurred
     */
    public Value parse()
    throws IOException, JsonException {
        switch(this.peek()){
            case '{': return this.parseObject();
            case '[':{
                this.nextReal();
                return this.parseArray();
            }
            default: throw new JsonException("Invalid syntax");
        }
    }

    /**
     * Parses the Object.
     *
     * @return the parsed Object
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JsonException Signals that a JSON Exception has occurred
     */
    private Value parseObject()
    throws IOException, JsonException{
        Map<String, Value> values = new HashMap<>();
        char c;
        it: while((c = this.nextReal()) != '}'){
            switch(c){
                case '"':{
                    this.name = this.parseName();
                    c = this.nextReal();
                    break;
                }
                case ',':
                case '{':{
                    continue it;
                }
                case ':':{
                    c = this.nextReal();
                    break;
                }
                default:{
                    break;
                }
            }

            if(c == ':'){
                c = this.nextReal();
            }

            if(c == '"'){
                values.put(this.name, this.parseString());
            } else if(c >= '0' && c <= '9'){
                values.put(this.name, this.parseNumber(c));
            } else if(c == 't' || c == 'f'){
                values.put(this.name, this.parseBoolean(c));
            } else if(c == 'n'){
                values.put(this.name, this.parseNull());
            } else if(c == '['){
                values.put(this.name, this.parseArray());
            } else if(c == '{'){
                values.put(this.name, this.parseObject());
            } else if(c == '\0'){
                throw new JsonException("End of stream");
            } else if(c == '}'){
                break;
            } else{
                throw new JsonException("Invalid syntax (" + this.name + "): " + c);
            }
        }

        return new Values.ObjectValue(values);
    }

    /**
     * Parses the array.
     *
     * @return the parsed array
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JsonException Signals that a JSON Exception has occurred
     */
    private Value parseArray()
    throws IOException, JsonException{
        List<Value> values = new LinkedList<>();
        char c;
        while((c = this.nextReal()) != '\0'){
            if(c == '"'){
                values.add(this.parseString());
            } else if(c >= '0' && c <= '9'){
                values.add(this.parseNumber(c));
            } else if(c == 't' || c == 'f'){
                values.add(this.parseBoolean(c));
            } else if(c == 'n'){
                values.add(this.parseNull());
            } else if(c == '{'){
                values.add(this.parseObject());
            } else if(c == '['){
                values.add(this.parseArray());
            } else if(c == ','){
                // Fallthrough
            } else if(c == ']'){
                break;
            } else{
                throw new JsonException("Invalid syntax: " + c);
            }
        }

        return new Values.ArrayValue(values.toArray(new Value[values.size()]));
    }

    /**
     * Parses the number.
     *
     * @param c the c
     * @return the parsed number
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Value parseNumber(char c)
    throws IOException{
        this.buffer = c + "";
        while(((c = this.next()) >= '0' && c <= '9') || (c == '.' && !this.buffer.contains("."))){
            this.buffer += c;
        }
        return new Values.NumberValue(new FlexibleNumber(this.buffer));
    }

    /**
     * Parses the null.
     *
     * @return the parsed null
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Value parseNull()
    throws IOException{
        this.skip(4);
        return Values.NullValue.NULL;
    }

    /**
     * Parses the boolean.
     *
     * @param c the c
     * @return the parsed boolean
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JsonException the json exception
     */
    private Value parseBoolean(char c)
    throws IOException, JsonException {
        switch(c){
            case 't':{
                this.skip(3);
                return Values.BooleanValue.TRUE;
            }
            case 'f':{
                this.skip(4);
                return Values.BooleanValue.FALSE;
            }
            default:{
                throw new JsonException("Invalid syntax: " + c);
            }
        }
    }

    /**
     * Parses the name.
     *
     * @return the parsed name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String parseName()
    throws IOException{
        this.buffer = "";
        char c;
        while((c = this.next()) != '"'){
            this.buffer += c;
        }
        return this.buffer;
    }

    /**
     * Parses the string.
     *
     * @return the parsed String
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Value parseString()
    throws IOException{
        this.buffer = "";
        char c;
        while((c = this.next()) != '"'){
            switch(c){
                case '\\':{
                    switch(c = this.next()){
                        case '\\':{
                            this.buffer += '\\';
                            break;
                        }
                        case 't':{
                            this.buffer += '\t';
                            break;
                        }
                        case '/':{
                            break;
                        }
                        default:{
                            this.buffer += c;
                            break;
                        }
                    }
                }
                default:{
                    this.buffer += c;
                    break;
                }
            }
        }

        return new Values.StringValue(this.buffer);
    }

    /**
     * Skips the designated amount of characters in {@link #in}.
     *
     * @param amount the amount
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void skip(int amount)
    throws IOException{
        for(int i = 0; i < amount; i++){
            this.next();
        }
    }

    /**
     * Checks whether the next character is a real, non-whitespace character and returns if it is.
     *
     * @return the next real character
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private char nextReal()
    throws IOException{
        char c;
        while(this.isWhitespace(c = this.next()));
        return c;
    }

    /**
     * Checks if is whitespace.
     *
     * @param c the character to be examined for whitespace
     * @return true, if is whitespace
     */
    private boolean isWhitespace(char c){
        switch(c){
            case '\n':
            case '\t':
            case ' ':
            case '\r':{
                return true;
            }
            default:{
                return false;
            }
        }
    }

    /**
     * Determines whether there is a next character.
     *
     * @return the next character
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private char next()
    throws IOException{
        char ret;
        if(this.peek == '\0'){
            ret = (char) this.in.read();
        } else{
            ret = this.peek;
            this.peek = '\0';
        }
        if(ret == '\0'){
            throw new IllegalStateException("End of stream");
        }
        return ret;
    }

    /**
     * Peeks at the current character to be parsed to determine Object state for {@link #parse()}.
     *
     * @return the current character
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private char peek()
    throws IOException{
        if(this.peek != '\0') {
            throw new IllegalStateException("Already peeking");
        }

        this.peek = (char) this.in.read();
        return this.peek;
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close()
    throws IOException {
        this.in.close();
    }

    /**
     * The Class FlexibleNumber.
     */
    private static final class FlexibleNumber
    extends Number{
        
        /** The data. */
        private final String data;

        /**
         * Instantiates a new flexible number.
         *
         * @param data the data
         */
        private FlexibleNumber(String data){
            this.data = data;
        }

        /* (non-Javadoc)
         * @see java.lang.Number#intValue()
         */
        @Override
        public int intValue() {
            return Integer.valueOf(this.data);
        }

        /* (non-Javadoc)
         * @see java.lang.Number#longValue()
         */
        @Override
        public long longValue() {
            return Long.valueOf(this.data);
        }

        /* (non-Javadoc)
         * @see java.lang.Number#floatValue()
         */
        @Override
        public float floatValue() {
            return Float.valueOf(this.data);
        }

        /* (non-Javadoc)
         * @see java.lang.Number#doubleValue()
         */
        @Override
        public double doubleValue() {
            return Double.valueOf(this.data);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            return data;
        }
    }
}