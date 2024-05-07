package com.csumb.cst363;

import java.sql.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 */
@SuppressWarnings("unused")
@Controller
public class ControllerPatientCreate {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Request blank patient registration form.
	 * Do not modify this method.
	 */
	@GetMapping("/patient/new")
	public String getNewPatientForm(Model model) {
		model.addAttribute("patient", new Patient());
		return "patient_register";
	}
	
	/*
	 * Process new patient registration
	 */
	@PostMapping("/patient/new")
	public String createPatient(Patient p, Model model) {

		System.out.println("createPatient "+p);  // debug

		// TODO
		// get a connection to the database
		try (Connection con = getConnection();) {
			// validate the doctor's last name and obtain the doctor id
			// create the query statement
			PreparedStatement ps = con.prepareStatement("select doctor_id from doctor where last_name = ?");
			ps.setString(1, p.getPrimaryName());
			ResultSet rs = ps.executeQuery();
			String doctorId;

			if (rs.next()) {
				doctorId = rs.getString("doctor_id");
			} else {
				// Handle the case where no matching doctor is found
				model.addAttribute("message", "No doctor found with the last name: " + p.getPrimaryName());
				model.addAttribute("patient", p);
				return "patient_register";
			}

			// insert the patient profile into the patient table
			PreparedStatement psCreate = con.prepareStatement("insert into patient (patient_id, primary_physician_id, ssn, first_name, last_name, date_of_birth, street_address) values (5,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			psCreate.setString(1, doctorId);
			psCreate.setString(2, p.getSsn());
			psCreate.setString(3, p.getFirst_name());
			psCreate.setString(4, p.getLast_name());
			psCreate.setString(5, p.getBirthdate());
			psCreate.setString(6, p.getStreet());
			// TODO: insert the remaining fields for the address

			psCreate.executeUpdate();

			// obtain the generated id for the patient and update patient object
			ResultSet generatedKeys = psCreate.getGeneratedKeys();
			if (generatedKeys.next()) {
				p.setId(generatedKeys.getInt(1));
			}

			// display message and patient information
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("patient", p);
			System.out.println("success!");
			return "patient_show";
		} catch (SQLException e) {
			// if there is error
			System.out.println(e);
			System.out.println("error");
			model.addAttribute("message",  "Registration unsuccessful.");
			model.addAttribute("patient", p);
			return "patient_register";
		}
	}
	
	/*
	 * Request blank form to search for patient by and and id
	 * Do not modify this method.
	 */
	@GetMapping("/patient/edit")
	public String getSearchForm(Model model) {
		model.addAttribute("patient", new Patient());
		return "patient_get";
	}
	
	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String showPatient(Patient p, Model model) {

		System.out.println("showPatient " + p); // debug

		// TODO

		// get a connection to the database
		// using patient id and patient last name from patient object
		// retrieve patient profile and doctor's last name
		// update patient object with patient profile data

		model.addAttribute("patient", p);
		return "patient_show";

		// if there is error
		// model.addAttribute("message", <error message>);
		// model.addAttribute("patient", p);
		// return "patient_get";
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */


	private Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "CT$*mv1RK$^$y%&wR18T");
		return conn;
	}

}
