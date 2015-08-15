package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.Value;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// TODO: Auto-generated Javadoc
/**
 * The Class JsonOutputStream outputs a {@link OutputStream} to be used for later parsing.
 */
public final class JsonOutputStream
implements Closeable {
    
    /** The {@link OutputStream} to be output. */
    private final OutputStream out;

    /**
     * Instantiates a new JsonOutputStream based on the existing {@link OutputStream}.
     *
     * @param out the OutputStream to be converted
     */
    public JsonOutputStream(OutputStream out){
        if(out == null) throw new NullPointerException("out == null");
        this.out = out;
    }

    /**
     * Writes the beginning indicator for a new Object.
     *
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream newObject()
    throws IOException{
        this.write('{');
        return this;
    }

    /**
     * Writes the ending indicator for the new Object.
     *
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream endObject()
    throws IOException {
        this.write('}');
        return this;
    }

    /**
     * Writes the beginning indicator for a new array.
     *
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream newArray()
    throws IOException {
        this.write('[');
        return this;
    }

    /**
     * Writes the ending indicator for the new array.
     *
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream endArray()
    throws IOException {
        this.write(']');
        return this;
    }

    /**
     * Writes the name of the Object being written.
     *
     * @param name name of the Object being written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream name(String name)
    throws IOException {
        this.write('\"');
        this.write(name);
        this.write('\"');
        this.write(':');
        return this;
    }

    /**
     * Writes {@link Value} being written as a String.
     *
     * @param str the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(String str)
    throws IOException {
        this.write('\"');
        this.write(str);
        this.write('\"');
        return this;
    }

    /**
     * Writes the {@link Value} being written as an int.
     *
     * @param i the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(int i)
    throws IOException {
        this.write(String.valueOf(i));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a double.
     *
     * @param d the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(double d)
    throws IOException {
        this.write(String.valueOf(d));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a float.
     *
     * @param f the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(float f)
    throws IOException {
        this.write(String.valueOf(f));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a char.
     *
     * @param c the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(char c)
    throws IOException {
        this.write(c);
        return this;
    }

    /**
     * Writes the {@link Value} being written as a short.
     *
     * @param s the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(short s)
    throws IOException {
        this.write(String.valueOf(s));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a long.
     *
     * @param l the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(long l)
    throws IOException {
        this.write(String.valueOf(l));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a byte.
     *
     * @param b the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(byte b)
    throws IOException {
        this.write(String.valueOf(b));
        return this;
    }

    /**
     * Writes the {@link Value} being written as a boolean.
     *
     * @param b the Value to be written
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream value(boolean b)
    throws IOException{
        this.write(String.valueOf(b));
        return this;
    }

    /**
     * Writes the indicator to indicate that there is a next Object.
     *
     * @return the JsonOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonOutputStream next()
    throws IOException {
        this.write(',');
        return this;
    }

    /**
     * Write the aforementioned material as a set of bytes.
     *
     * @param c the char to be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void write(char c)
    throws IOException{
        this.out.write((byte) c);
    }

    /**
     * Writes the String as a set of bytes.
     *
     * @param chars the String to be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void write(String chars)
    throws IOException{
        this.out.write(chars.getBytes(StandardCharsets.UTF_8));
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close()
    throws IOException {
        this.out.close();
    }
}