package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.repository.ItemRepository;

@RestController
@RequestMapping("")
public class AutoCompleteApiController {

	@Autowired
	private ItemRepository itemRepository;
	
	
	@GetMapping("/searchItem")
	public List<String> autoCompleteList() {
		List<String> allNames = itemRepository.getAllNames();
		return allNames;
	}
}
