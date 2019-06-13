package jp.co.sample.emp_management.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CommonController implements ErrorController {
	
	private static final String PATH = "/error";
	
	@RequestMapping("/maintenance")
	public String maintenance() {
		return "common/error";
	}
	
	@Override
	@RequestMapping(PATH)
	public String getErrorPath() {
		System.out.println("404 not found");
		return "common/error";
	}
}
