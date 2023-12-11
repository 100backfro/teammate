package com.api.backend.schedule.customValidAnnotation;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerErrorException;

@Slf4j
public class StartAndEndDtCheckValidator implements ConstraintValidator<StartAndEndDtCheck, Object> {
  private String message;
  private String scheduleStart;
  private String scheduleEnd;

  @Override
  public void initialize(StartAndEndDtCheck constraintAnnotation) {
    message = constraintAnnotation.message();
    scheduleStart = constraintAnnotation.scheduleStart();
    scheduleEnd = constraintAnnotation.scheduleEnd();
  }

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext context) {
    int invalidCount = 0;
    LocalDateTime startDt = getFieldValue(o, scheduleStart);
    LocalDateTime endDt = getFieldValue(o, scheduleEnd);
    if (startDt.isAfter(endDt)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message)
          .addPropertyNode(scheduleStart)
          .addConstraintViolation();
      invalidCount += 1;
    }
    return invalidCount == 0;
  }

  private LocalDateTime getFieldValue(Object object, String fieldName) {
    Class<?> clazz = object.getClass();
    Field dateField;
    try {
      dateField = clazz.getDeclaredField(fieldName);
      dateField.setAccessible(true);
      Object target = dateField.get(object);
      if (!(target instanceof LocalDateTime)) {
        throw new ClassCastException("casting exception");
      }
      return (LocalDateTime) target;
    } catch (NoSuchFieldException e) {
      log.error("NoSuchFieldException", e);
    } catch (IllegalAccessException e) {
      log.error("IllegalAccessException", e);
    }
    throw new ServerErrorException("Not Found Field");
  }
}
