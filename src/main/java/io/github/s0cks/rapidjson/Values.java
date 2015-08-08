/*
 * 
 */
package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.reflect.TypeToken;
import io.github.s0cks.rapidjson.reflect.Types;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class Values organizes the various {@code Value} types.
 */
public final class Values{
    
    /** The Constant MAP_TYPE. */
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, Value>>(){}.rawType;
    
    /** The Constant LIST_TYPE. */
    private static final Type LIST_TYPE = new TypeToken<LinkedList<Value>>(){}.rawType;

    /**
     * Returns the Values of the given Object with the restriction Field
     *
     * @param instance the Object that the Values will be gotten from
     * @param f the Field restriction
     * @return the value
     */
    public static Value of(Object instance, Field f){
        try {
            return Values.of(f.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Object as a type of Value.
     *
     * @param obj the Object to be transformed
     * @return the transformed Value
     */
    @SuppressWarnings("unchecked")
    public static Value of(Object obj){
        if(obj == null){
            return new NullValue();
        } else if(obj instanceof Number){
            return new NumberValue((Number) obj);
        } else if(obj instanceof String){
            return new StringValue(String.valueOf(obj));
        } else if(obj instanceof Boolean){
            return new BooleanValue((Boolean) obj);
        } else if(Types.equals(LIST_TYPE, Types.getRawType(obj.getClass()))){
            List<Value> values = (List<Value>) obj;
            return new ArrayValue(values.toArray(new Value[values.size()]));
        } else if(Types.equals(MAP_TYPE, Types.getRawType(obj.getClass()))){
            return new ObjectValue(((Map<String, Value>) obj));
        } else{
            throw new IllegalStateException("Unknown type: " + obj.getClass().getName());
        }
    }

    /**
     * The Class AbstractValue.
     */
    private static abstract class AbstractValue
    implements Value{
        
        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#isNull()
         */
        @Override
        public boolean isNull(){
            return false;
        }


        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asByte()
         */
        @Override
        public byte asByte() {
            throw new UnsupportedOperationException("Not of type byte");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asShort()
         */
        @Override
        public short asShort() {
            throw new UnsupportedOperationException("Not of type short");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asInt()
         */
        @Override
        public int asInt() {
            throw new UnsupportedOperationException("Not of type int");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asLong()
         */
        @Override
        public long asLong() {
            throw new UnsupportedOperationException("Not of type long");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asFloat()
         */
        @Override
        public float asFloat() {
            throw new UnsupportedOperationException("Not of type float");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asDouble()
         */
        @Override
        public double asDouble() {
            throw new UnsupportedOperationException("Not of type double");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asBoolean()
         */
        @Override
        public boolean asBoolean() {
            throw new UnsupportedOperationException("Not of type boolean");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asChar()
         */
        @Override
        public char asChar() {
            throw new UnsupportedOperationException("Not of type char");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asString()
         */
        @Override
        public String asString() {
            throw new UnsupportedOperationException("Not of type String");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#asArray()
         */
        @Override
        public Value[] asArray() {
            throw new UnsupportedOperationException("Not of type Array");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#getValue(java.lang.String)
         */
        @Override
        public Value getValue(String name) {
            throw new UnsupportedOperationException("Not of type object");
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            return this.write();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#setValue(java.lang.String, io.github.s0cks.rapidjson.Value)
         */
        @Override
        public void setValue(String name, Value v){
            throw new UnsupportedOperationException("Not of type object");
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#addValue(io.github.s0cks.rapidjson.Value)
         */
        @Override
        public void addValue(Value v){
            throw new UnsupportedOperationException("Not of type array");
        }
    }

    /**
     * The Class ObjectValue.
     */
    public static final class ObjectValue
    extends AbstractValue{
        
        /** The Map of Values. */
        private final Map<String, Value> values;

        /**
         * Instantiates a new object value.
         *
         * @param values the values
         */
        public ObjectValue(Map<String, Value> values){
            this.values = values;
        }

        /**
         * Instantiates a new default object value.
         */
        public ObjectValue(){
            this(new HashMap<String, Value>());
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#getValue(java.lang.String)
         */
        @Override
        public Value getValue(String name) {
            return this.values.get(name);
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write(){
            StringBuilder builder = new StringBuilder()
                    .append("{");
            List<Map.Entry<String, Value>> entrySet = new LinkedList<>(this.values.entrySet());
            for(int i = 0; i < entrySet.size(); i++){
                Map.Entry<String, Value> entry = entrySet.get(i);
                builder.append("\"")
                        .append(entry.getKey())
                        .append("\"")
                        .append(":")
                        .append(entry.getValue().write());
                if(i < entrySet.size() - 1){
                    builder.append(",");
                }
            }
            return builder.append("}").toString();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#setValue(java.lang.String, io.github.s0cks.rapidjson.Value)
         */
        @Override
        public void setValue(String name, Value value){
            this.values.put(name, value);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectValue
                && ((ObjectValue) obj).values.equals(this.values);
        }
    }

    /**
     * The Class StringValue.
     */
    public static final class StringValue
    extends AbstractValue{
        
        /** The value. */
        private final String value;

        /**
         * Instantiates a new string value.
         *
         * @param value the value
         */
        public StringValue(String value){
            this.value = value;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asString()
         */
        @Override
        public String asString(){
            return this.value;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write() {
            return "\"" + this.value + "\"";
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asChar()
         */
        @Override
        public char asChar(){
            return this.value.charAt(0);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj){
            return (obj instanceof StringValue
                && ((StringValue) obj).value.equals(this.value))
                || (obj instanceof String
                && obj.equals(this.value));
        }
    }

    /**
     * The Class NumberValue.
     */
    public static final class NumberValue
    extends AbstractValue{
        
        /** The value. */
        private final Number value;

        /**
         * Instantiates a new number value.
         *
         * @param value the value
         */
        public NumberValue(Number value){
            this.value = value;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asByte()
         */
        @Override
        public byte asByte() {
            return this.value.byteValue();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asDouble()
         */
        @Override
        public double asDouble() {
            return this.value.doubleValue();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write() {
            return this.value.toString();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asFloat()
         */
        @Override
        public float asFloat() {
            return this.value.floatValue();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asShort()
         */
        @Override
        public short asShort(){
            return this.value.shortValue();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asLong()
         */
        @Override
        public long asLong() {
            return this.value.longValue();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asInt()
         */
        @Override
        public int asInt() {
            return this.value.intValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj){
            return obj instanceof Number
                && obj.equals(this.value);
        }
    }

    /**
     * The Class ArrayValue.
     */
    public static final class ArrayValue
    extends AbstractValue{
        
        /** The values. */
        private Value[] values;

        /**
         * Instantiates a new array value.
         *
         * @param values the values
         */
        public ArrayValue(Value[] values){
            this.values = values;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write() {
            StringBuilder builder = new StringBuilder()
                    .append("[");
            for(int i = 0; i < this.values.length; i++){
                if(this.values[i] != null){
                    builder.append(this.values[i].toString());
                } else{
                    builder.append("null");
                }

                if(i < this.values.length - 1){
                    builder.append(",");
                }
            }
            return builder.append("]").toString();
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asArray()
         */
        @Override
        public Value[] asArray() {
            return this.values;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#addValue(io.github.s0cks.rapidjson.Value)
         */
        @Override
        public void addValue(Value v){
            Value[] newArrays = new Value[this.values.length + 1];
            System.arraycopy(this.values, 0, newArrays, 0, this.values.length);
            newArrays[this.values.length] = v;
            this.values = newArrays;
        }
    }

    /**
     * The Class NullValue.
     */
    public static final class NullValue
    extends AbstractValue{
        
        /** The Constant NULL. */
        public static final NullValue NULL = new NullValue();

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#isNull()
         */
        @Override
        public boolean isNull(){
            return true;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write() {
            return "null";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj){
            return obj == null;
        }
    }

    /**
     * The Class BooleanValue.
     */
    public static final class BooleanValue
    extends AbstractValue{
        
        /** The Constant TRUE. */
        public static final BooleanValue TRUE = new BooleanValue(true);
        
        /** The Constant FALSE. */
        public static final BooleanValue FALSE = new BooleanValue(false);

        /** The value. */
        private final boolean value;

        /**
         * Instantiates a new boolean value.
         *
         * @param value the value
         */
        public BooleanValue(boolean value){
            this.value = value;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Values.AbstractValue#asBoolean()
         */
        @Override
        public boolean asBoolean(){
            return this.value;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.Value#write()
         */
        @Override
        public String write() {
            return String.valueOf(this.value);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj){
            return obj instanceof Boolean
                && obj.equals(this.value);
        }
    }
}