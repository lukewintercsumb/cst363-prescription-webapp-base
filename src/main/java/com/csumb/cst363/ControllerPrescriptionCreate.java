package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@SuppressWarnings("unused")
@Controller    
public class ControllerPrescriptionCreate {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Doctor requests blank form for new prescription.
	 * Do not modify this method.
	 */
	@GetMapping("/prescription/new")
	public String getPrescriptionForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_create";
	}
	

	/*
	 * Doctor creates a prescription.
	 */
	@PostMapping("/prescription")
	public String createPrescription(Prescription p, Model model) {

		System.out.println("createPrescription " + p);

		// TODO

		/*-
		 * Process the new prescription form. 
		 * 1. Obtain connection to database. 
		 * 2. Validate that doctor id and name exists 
		 * 3. Validate that patient id and name exists 
		 * 4. Validate that Drug name exists and obtain drug id. 
		 * 5. Insert new prescription 
		 * 6. Get generated value for rxid 
		 * 7. Update prescription object and return
		 */

		model.addAttribute("message", "Prescription created.");
		model.addAttribute("prescription", p);
		return "prescription_show";

		// if there is error
		// model.addAttribute("message",  <error message>);
		// model.addAttribute("prescription", p);
		// return "prescription_create";

	}
	
	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection(
				"jdbc:mysql://localhost:3306/?user=root", "CT$*mv1RK$^$y%&wR18T");
		return conn;
	}
	
}
