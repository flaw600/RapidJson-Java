/*
 * 
 */
package io.github.s0cks.rapidjson;

// TODO: Auto-generated Javadoc
/**
 * Gets, sets, and adds different values to the {@link RapidJson}.
 */
public interface Value{
    
    /**
     * Returns the value as a byte.
     *
     * @return the byte
     */
    public byte asByte();
    
    /**
     * Returns the value as a short.
     *
     * @return the short
     */
    public short asShort();
    
    /**
     * Returns the value as an int.
     *
     * @return the int
     */
    public int asInt();
    
    /**
     * Returns the value as a long.
     *
     * @return the long
     */
    public long asLong();
    
    /**
     * Returns the value as a float.
     *
     * @return the float
     */
    public float asFloat();
    
    /**
     * Returns the value as a double.
     *
     * @return the double
     */
    public double asDouble();
    
    /**
     * Returns the value as a boolean.
     *
     * @return true, if successful
     */
    public boolean asBoolean();
    
    /**
     * Checks if the value is null.
     *
     * @return true, if null
     */
    public boolean isNull();
    
    /**
     * Returns the value as a char.
     *
     * @return the char
     */
    public char asChar();
    
    /**
     * Returns the value as a string.
     *
     * @return the string
     */
    public String asString();
    
    /**
     * Writes the value. TODO
     *
     * @return the string
     */
    public String write();
    
    /**
     * Gets the value.
     *
     * @param name the name of the value
     * @return the value
     */
    public Value getValue(String name);
    
    /**
     * Returns the values as an array.
     *
     * @return the value[]
     */
    public Value[] asArray();
    
    /**
     * Sets the value.
     *
     * @param name the name of the value
     * @param v the value to be set
     */
    public void setValue(String name, Value v);
    
    /**
     * Adds the value.
     *
     * @param v the value to be added
     */
    public void addValue(Value v);
}