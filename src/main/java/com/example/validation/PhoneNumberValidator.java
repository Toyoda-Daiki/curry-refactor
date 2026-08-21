package com.example.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// リファクタリング課題#12 カスタムバリデーションの実装
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9]+-[0-9]+-[0-9]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // null/空は @NotBlank に任せる
        return PHONE_PATTERN.matcher(value).matches();
    }
}
