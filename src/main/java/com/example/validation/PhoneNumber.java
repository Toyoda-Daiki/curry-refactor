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
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
  String message() default "電話番号の形式が正しくありません（例：090-1234-5678）";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
