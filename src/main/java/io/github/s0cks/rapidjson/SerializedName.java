/*
 * 
 */
package io.github.s0cks.rapidjson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Auto-generated Javadoc
/**
 * The Interface SerializedName.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface SerializedName {
    
    /**
     * Value.
     *
     * @return the {@link SerializedName}
     */
    String value();
}