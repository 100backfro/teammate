package com.api.backend.team.data.annotation;

import com.api.backend.team.data.type.adapter.ValidFileValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileValidator.class)
public @interface ValidFile {
    String message() default "Invalid File";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}