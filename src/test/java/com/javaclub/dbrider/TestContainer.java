package com.javaclub.dbrider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.context.ActiveProfiles;

/**
 * Run full-system feature test with MySQL in TestContainers.
 * Use this annotation in addition to the {@link IntegrationTest}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DatabaseUnit
public @interface TestContainer {

}
