package com.example.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// リファクタリング課題#12 カスタムバリデーションの実装
public class PostalCodeValidator implements ConstraintValidator<PostalCode, String> {
  private static final Pattern POSTAL_PATTERN =
  Pattern.compile("^[0-9]{3}-[0-9]{4}$");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;
    return POSTAL_PATTERN.matcher(value).matches();
  }
}
