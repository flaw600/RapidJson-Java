/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.RapidJson;
import io.github.s0cks.rapidjson.RapidJsonBuilder;
import io.github.s0cks.rapidjson.SerializedName;

import java.awt.Color;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class InstanceFactoryTest.
 */
public class InstanceFactoryTest {
    private static final RapidJson rapidJson = new RapidJsonBuilder()
            .build();

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String... args)
    throws Exception{
        long start;
        List<Name> names;
        try(InputStream in = System.class.getResourceAsStream("/test.json")){
            start = System.nanoTime();
            names = rapidJson.fromJson(in, new TypeToken<LinkedList<Name>>(){});
            System.out.println("Deserialization Took: " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - start)) + "ms");
            for(Name name : names){
                System.out.println(name);
            }
        }

        start = System.nanoTime();
        names = rapidJson.fromJson("[\"BOB\", \"GEORGE\"]", new TypeToken<LinkedList<Name>>(){});
        System.out.println("Deserialization Took: " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - start)) + "ms");
        for(Name name : names){
            System.out.println(name);
        }
    }

    /**
     * The Enum Name.
     */
    private enum Name{
        
        /** The george. */
        GEORGE, 
 /** The bob. */
 BOB, 
 /** The jim. */
 JIM;
    }

    /**
     * The Class Colors.
     */
    private static final class Colors{
        
        /** The colors. */
        @SerializedName("colorz") private final Color[] colors;
        
        /** The flags. */
        private final boolean[] flags;
        
        /** The names. */
        private final String[] names;

        /**
         * Instantiates a new colors.
         *
         * @param colors the colors
         * @param flags the flags
         * @param names the names
         */
        private Colors(Color[] colors, boolean[] flags, String[] names){
            this.colors = colors;
            this.flags = flags;
            this.names = names;
        }
    }

    /**
     * The Class TypeClass.
     */
    private static final class TypeClass{
        
        /** The dev. */
        private boolean dev = false;
        
        /** The id. */
        private int id = 100;
        
        /** The name. */
        private String name = "Hello World";
        
        /** The color. */
        private Color color = new Color(0x000);
    }
}