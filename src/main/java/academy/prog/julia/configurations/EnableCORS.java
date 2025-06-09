package academy.prog.julia.configurations;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Custom annotation to enable Cross-Origin Resource Sharing (CORS) configuration in a Spring application.
 *
 * When this annotation is applied to a Spring configuration class, it automatically imports the
 * {@link CorsAutoConfiguration} class, which sets up CORS configuration properties and filters according to the
 * application's settings.
 *
 * This annotation is marked with {@link Inherited}, meaning that if a class is annotated with {@code @EnableCORS},
 * its subclasses will also inherit this annotation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CorsAutoConfiguration.class})
public @interface EnableCORS {
}