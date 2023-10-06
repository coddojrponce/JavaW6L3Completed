package com.robertp.testW6L1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.robertp.testW6L1.models.Comment;
import com.robertp.testW6L1.models.LoginUser;
import com.robertp.testW6L1.models.User;
import com.robertp.testW6L1.repositories.UserRepository;
import com.robertp.testW6L1.services.CommentService;
import com.robertp.testW6L1.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommentService cService;
	
	@GetMapping("/dashboard")
	public String dash(
			Model model,
			HttpSession session
			) {
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		 
		 model.addAttribute("comments",cService.getAll());
		
		return "dash.jsp";
	}
	
	@GetMapping("/")
	public String index(
			Model model
			) {
		
		
		model.addAttribute("login",new LoginUser());
		model.addAttribute("register",new User());
		return "index.jsp";
	}
	
	@PostMapping("/register")
	public String register(
			@Valid @ModelAttribute("register") User newUser,
			BindingResult result,
			Model model,
			HttpSession session
			) {
		
		userService.register(newUser, result);
		
		if(result.hasErrors()) {
			model.addAttribute("login", new LoginUser());
			return "index.jsp";
		}
		
		session.setAttribute("userId", newUser.getId());
		System.out.println("This is the user");
		System.out.println(newUser.getId());
		
		
		return "redirect:/dashboard";
	}
	
	@PostMapping("/login")
	public String login(
			@Valid @ModelAttribute("login") LoginUser newLogin,
			BindingResult result,
			Model model,
			HttpSession session
			
			) {
		
		User thisUser = userService.login(newLogin, result);
		
		if(result.hasErrors()) {
			model.addAttribute("register",new User());
			return "index.jsp";
		}
		
		session.setAttribute("userId", thisUser.getId());
		
		return "redirect:/dashboard";
	}
	
	@GetMapping("/logout")
	public String logout(
			HttpSession session
			) {
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/comments/new")
	public String commentPage(
			Model model,
			HttpSession session
			) {
		
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		
		
		model.addAttribute("comment",new Comment());
		
		return "create.jsp";
	}
	
	@PostMapping("/comments")
	public String commentHandler(
			@Valid @ModelAttribute("comment") Comment comment,
			BindingResult result,
			HttpSession session
			) {
		
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		
			if(result.hasErrors()) {
				
				return "create.jsp";
			}
			
			Long id = (Long) session.getAttribute("userId");
			User user = userService.findById(id);
			comment.setUser(user);
			
			cService.save(comment);
			
			
			
			return "redirect:/dashboard";
		
		
	}
	
	@GetMapping("/comments/{id}")
	public String viewOne(
			@PathVariable("id") Long id,
			Model model,
			HttpSession session
			) {
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		model.addAttribute("comment",cService.getOne(id));
		
		return "view.jsp";
		
		
		
	}
	
	@DeleteMapping("/comments/{id}")
	public String destroy(
			@PathVariable("id") Long id,
			Model model,
			HttpSession session,
			RedirectAttributes redirectAttributes
			) {
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		 
		 Comment comment = cService.getOne(id);
		 
		 Long commentUserId = comment.getUser().getId();
		 
		 if(!userId.equals(commentUserId)) {
			 redirectAttributes.addFlashAttribute("hack", "That is not allowed");
			 return "redirect:/dashboard";
		 }
		 
		cService.destory(id);
		
		
		return "redirect:/dashboard";
		
	}
	
	@GetMapping("/comments/{id}/edit")
	public String update(
			@PathVariable("id") Long id,
			Model model,
			HttpSession session
			) {
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		 
		 Comment comment = cService.getOne(id);
		 model.addAttribute("comment",comment);
		 
		 
		 return "update.jsp";
		
		
	}
	
	@PutMapping("/comments/{id}")
	public String updateHandler(
			@PathVariable("id") Long id,
			@Valid @ModelAttribute("comment") Comment comment,
			BindingResult result,
			Model model,
			HttpSession session
			) {
		
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		 
		 if(result.hasErrors()) {
			 return "update.jsp";
		 }
		 
		 comment.setUser(userService.findById(userId));
		 cService.save(comment);
		 
		 return "redirect:/dashboard";
		
	}
	
	@PostMapping("/comments/{id}/like")
	public String like(
			@PathVariable("id") Long id,
			Model model,
			HttpSession session
			) {
		
		Long userId = (Long) session.getAttribute("userId");
		 if(userId==null) {
			 return "redirect:/";
		 }
		 
		 User user = userService.findById(userId);
		 
		 Comment comment = cService.getOne(id);
		 
		 if(comment.getLikes().contains(user)) {
			 return "redirect:/dashboard";
		 }
		 
		 
		 
		 comment.getLikes().add(user);
		 
		 cService.save(comment);
		 
		return "redirect:/dashboard";
		
		
		
		
	}
	
	
	
	
	
	

}
