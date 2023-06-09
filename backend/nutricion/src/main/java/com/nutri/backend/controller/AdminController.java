package com.nutri.backend.controller;

import com.nutri.backend.model.User;
import com.nutri.backend.repositories.DietRepository;
import com.nutri.backend.service.UserService;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class AdminController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService  userService;

	@Autowired
	private DietRepository dietRepository;

	@GetMapping("/admin")
	public String showAdmin(Model model, HttpServletRequest request) {
		model.addAttribute("activeDiets",dietRepository.numOfDiets());
		model.addAttribute("earns",userService.findAllByUser("client")*9);
		model.addAttribute("Nclient",userService.findAllByUser("client"));
		int month[]=new int[12];
		for (int i=0;i<12;i+=1){
			month[i]=(userService.finAllByUserMonth("client",i)*9);
		}
		model.addAttribute("registeredUsers", Arrays.toString(month));
		return "USR_Admin";
	}

	@GetMapping("/adminCharts")
	public String showCharts(Model model) {
		int dietas[]= new int[3];
		dietas[0]=dietRepository.numOfDietsType("Bulking");
		dietas[1]=dietRepository.numOfDietsType("Cutting");
		dietas[2]=dietRepository.numOfDietsType("Maintenence");
		int month[]=new int[12];
		for (int i=0;i<12;i+=1){
			month[i]=(userService.findByEntryDate(i));
		}
		model.addAttribute("registeredUsers", Arrays.toString(month));
		model.addAttribute("listDiets",Arrays.toString(dietas));
		return "USR_AdminCharts";
	}

	//Worker Administration Controller
	@GetMapping("/workerTable")
	public String workers(Model model) {
		Page<User> clientPage = userService.findPageClient(0, "worker");
		model.addAttribute("listWorker",clientPage.toList());
		model.addAttribute("lastWorker",clientPage.getTotalPages());
		return "USR_AdminWorkerTable";
	}

	@GetMapping("/workerTable/page/{page}")
	public String getWorkerPage(Model model, @PathVariable int page) {
		Page<User> client = userService.findPageClient(page, "worker");
		List<User> users = client.toList();
		model.addAttribute("listWorker", users);
		return "USR_AdminWorkerTableAjax";

	}
	@PostMapping("/deleteWorker")
	public String deleteWorker(Model model,@RequestParam(required = false) List<Long> id){
		if(id != null) {
			for (Long l : id) {
				userService.delete(l);
			}
		}else{
			return "redirect:/workerTable";
		}
		return "redirect:/workerTable";
	}

	@GetMapping("/addWorker")
	public String showAddWorkersForm() {
		return "USR_AdminAddWorker";
	}
	@PostMapping("/addWorker")
	public String addWorker(@RequestParam String workerName,@RequestParam String workerLastname,@RequestParam String workerEmail
			,@RequestParam String workerPassword,@RequestParam String workerDescription) {
		Calendar c1 = Calendar.getInstance();
		int month =c1.get(Calendar.MONTH) ;
		User user = new User(workerName,workerLastname,workerEmail,workerDescription,passwordEncoder.encode(workerPassword));
		user.setEntryDate(month);
		setUserImage(user,new ClassPathResource("static/images/undraw_profile.jpg").getPath());
		userService.save(user);
		return "redirect:/workerTable";
	}

	private void setUserImage(User user, String classpathResource){
		try {
			Resource image = new ClassPathResource(classpathResource);
			user.setImage("Default");
			user.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
		} catch(Exception e){

		}
	}

	//***Client Administration***
	@GetMapping("/tablesClient")
	public String showClients(Model model) {
		Page<User> clientPage = userService.findPageClient(0, "client");
		model.addAttribute("listClient",clientPage.toList());
		model.addAttribute("lastClient",clientPage.getTotalPages());
		return "USR_AdminClientTable";
	}
	@GetMapping("/tablesClient/{id}")
	public String showUsers(Model model,@PathVariable long id){
		User user =userService.findById(id).orElseThrow();
		model.addAttribute("name",user.getName());
		model.addAttribute("surname",user.getSurname());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("id",user.getId());
		if(user.getUserType().equals("worker")){
			model.addAttribute("worker", true);
			model.addAttribute("description", user.getDescription());
		}
		return "USR_AdminShowUser";
	}


	@PostMapping("/deleteClient")
	public String deleteUser(Model model, @RequestParam(required = false) List<Long> id){
		if(id != null) {
			for (Long l : id) {
				userService.delete(l);
			}
		}
		return "redirect:/tablesClient";
	}

	//ajax
	@GetMapping("/tablesClient/page/{page}")
	public String getClientPage(Model model, @PathVariable int page) {
		Page<User> client = userService.findPageClient(page, "client");
		List<User> users = client.toList();
		model.addAttribute("listClient", users);
		return "USR_AdminClientTableAjax";

	}
}
