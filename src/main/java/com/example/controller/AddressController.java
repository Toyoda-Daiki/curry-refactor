package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.AddressForm;

/**
 * 配送先住所の動的フォームコントローラー
 * リファクタリング課題#13 動的フォームの作成
 */
@Controller
@RequestMapping("/address")
public class AddressController {

  private static final Logger log = LoggerFactory.getLogger(AddressController.class);

  @GetMapping("/form")
  public String showForm(Model model) {
    model.addAttribute("addressForm", new AddressForm());
    return "address/address_form";
  }

  @PostMapping("/register")
  public String register(AddressForm addressForm, Model model) {
    if (addressForm.getAddresses() != null) {
      addressForm.getAddresses().forEach(address -> {
        log.info("受け取った住所: name={}, address={}, tel={}",
            address.getName(), address.getAddress(), address.getTel());
      });
      model.addAttribute("addresses", addressForm.getAddresses());
    }
    return "address/address_confirm";
  }
}
