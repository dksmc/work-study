package com.dk.study.common.validate;

import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

/**
 * @author qixlin
 * @date 2020/09/21 17:37
 */

@Data
public class ValidateError {

    private String fieldName;

    private String error;

    public ValidateError(ObjectError error) {
        this.error = error.getDefaultMessage();
        if (error instanceof FieldError) {
            this.fieldName = ((FieldError)error).getField();
        }
    }

    public ValidateError(ConstraintViolation<?> constraintViolation) {
        this.error = constraintViolation.getMessage();
        Path propertyPath = constraintViolation.getPropertyPath();
        if (propertyPath instanceof PathImpl) {
            PathImpl path = (PathImpl) propertyPath;
            this.fieldName = path.getLeafNode().getName();
        }
    }
}
