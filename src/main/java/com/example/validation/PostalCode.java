package com.example.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// リファクタリング課題#12 カスタムバリデーションの実装
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostalCodeValidator.class)
public @interface PostalCode {
  String message() default "郵便番号の形式が正しくありません（例：123-4567）";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
