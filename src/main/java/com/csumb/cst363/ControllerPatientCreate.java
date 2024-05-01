package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			PreparedStatement ps = con.prepareStatement("select doctor_id from doctor where last_name = ?",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, p.getPrimaryName());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			String doctorId;

			if (rs.next()) {
				doctorId = rs.getString(1);
			} else {
				throw new NoSuchElementException();
			};

			// insert the patient profile into the patient table
			PreparedStatement psCreate = con.prepareStatement("insert into patient (primary_physicican_id, ssn, first_name, last_name, data_of_birth, street_address) values (?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			// obtain the generated id for the patient and update patient object
			p.setId(123456);

			// display message and patient information
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("patient", p);
			return "patient_show";
		} catch (SQLException e) {
			// if there is error
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
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}
