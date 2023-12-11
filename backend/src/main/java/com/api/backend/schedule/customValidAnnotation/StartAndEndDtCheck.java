package com.api.backend.schedule.customValidAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartAndEndDtCheckValidator.class)
public @interface StartAndEndDtCheck {
  String message() default "시작날짜가 종료날짜보다 늦을수 없습니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String scheduleStart();

  String scheduleEnd();
}
