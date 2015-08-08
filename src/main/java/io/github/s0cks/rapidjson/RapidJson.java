/*
 * 
 */
package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.io.JsonInputStream;
import io.github.s0cks.rapidjson.io.JsonOutputStream;
import io.github.s0cks.rapidjson.reflect.InstanceFactory;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeAdapterFactory;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

// TODO: Auto-generated Javadoc
/**
 * {@code RapidJson} is a Java library that can be used to convert Java Objects into their JSON representation. 
 * It can also be used to convert a JSON string to an equivalent Java object. RapidJson can work with 
 * arbitrary Java objects including pre-existing objects lacking source code. Namely, it does so
 * faster and more efficiently than Gson or boon. 
 */
public final class RapidJson{
    
    /** The instance factory. */
    private final InstanceFactory instanceFactory;

    /**
     * Instantiates a new default RapidJson.
     */
    public RapidJson(){
        this.instanceFactory = new InstanceFactory(this, new HashMap<Type, TypeAdapter>(), new LinkedList<TypeAdapterFactory>());
    }

    /**
     * Instantiates a new custom RapidJson.
     *
     * @param builder the {@link RapidJsonBuilder}
     */
    protected RapidJson(RapidJsonBuilder builder){
        this.instanceFactory = new InstanceFactory(this, builder.adapters, builder.factories);
    }

    /**
     * Returns a Java class from an existing JSON type.
     *
     * @param <T> the generic type
     * @param json the existing JSON
     * @param tClass the generic t class
     * @return the Java class
     * @throws JsonException the JSON exception
     */
    public <T> T fromJson(String json, Class<T> tClass)
    throws JsonException{
        try {
            return this.instanceFactory.create(tClass, new JsonInputStream(new ByteArrayInputStream(json.getBytes())).parse());
        } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a Java class from an existing JSON type using {@link TypeToken}.
     *
     * @param <T> the generic type
     * @param json the existing JSON
     * @param t the t
     * @return the Java class
     * @throws JsonException the JSON exception
     */
    public <T> T fromJson(String json, TypeToken<T> t)
    throws JsonException{
        try{
            return this.instanceFactory.create(t, new JsonInputStream(new ByteArrayInputStream(json.getBytes())).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a Java class from an existing JSON type using {@link InputStream} and {@link TypeToken}.
     *
     * @param <T> the generic type
     * @param in the existing InputStream
     * @param t the generic TypeToken
     * @return the Java class
     * @throws JsonException the json exception
     */
    public <T> T fromJson(InputStream in, TypeToken<T> t)
    throws JsonException{
        try{
            return this.instanceFactory.create(t, new JsonInputStream(in).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a Java class from an existing JSON type using {@link InputStream}.
     *
     * @param <T> the generic type
     * @param in the existing {@code InputStream}
     * @param tClass the t class
     * @return the Java class
     * @throws JsonException the json exception
     */
    public <T> T fromJson(InputStream in, Class<T> tClass)
    throws JsonException{
        try{
            return this.instanceFactory.create(tClass, new JsonInputStream(in).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Converts an existing Java Class to a JSON representative.
     *
     * @param obj the Java Object
     * @return the string
     * @throws JsonException the {@link JsonException} exception
     */
    public String toJson(Object obj){
        try(StringOutputStream sos = new StringOutputStream();
            JsonOutputStream jos = new JsonOutputStream(sos)){

            this.instanceFactory.write(obj, jos);
            return sos.toString();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Extends {@link OutputStream} so that it outputs as a String via {@code char}. 
     */
    private static final class StringOutputStream
    extends OutputStream{
        
        /** The string to be output. */
        private String str = "";

        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int i)
        throws IOException {
            this.str += (char) i;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            return this.str;
        }
    }
}