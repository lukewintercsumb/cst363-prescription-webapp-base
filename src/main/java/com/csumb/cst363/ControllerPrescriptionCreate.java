package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.SortedMap;

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
	public String createPrescription(Prescription p, Model model) throws SQLException {

		System.out.println("createPrescription " + p);

		//TODO

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

		Connection connection = null;
		ResultSet rs = null;
		String query = "SELECT doctor_id, first_name, last_name" +
				"FROM doctor " +
				"WHERE doctor_id = ?";
		try {
			//Obtain connection to database.
			connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, p.getDoctor_id());
			// Validate that doctor id and name exists
			rs = preparedStatement.executeQuery();
			if (!rs.next()) {
				model.addAttribute("message", "Dr."
						+ p.getDoctorLastName() + " ID: "
						+ p.getDoctor_id() + " could not be found");
				model.addAttribute("prescription", p);
				return "prescription_create";
			}
			query = "SELECT patient_id, first_name, last_name" +
					"FROM patient " +
					"WHERE patient_id = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, p.getPatient_id());
			// Validate that patient id and name exists
			rs = preparedStatement.executeQuery();

			if (!rs.next()) {
				model.addAttribute("message", "Patient: "
						+ p.getPatientLastName() + " ID: "
						+ p.getPatient_id() + " could not be found");
				model.addAttribute("prescription", p);
				return "prescription_create";
			}

			query = "SELECT drug_id, drug_name" +
					"FROM drug" +
					"WHERE drug_name = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, p.getDrugName());
			// Validate that patient id and name exists
			rs = preparedStatement.executeQuery();

			if (!rs.next()) {
				model.addAttribute("message", "Drug: "
						+ p.getDrugName() + " ID could not be found");
				model.addAttribute("prescription", p);
				return "prescription_create";
			}

			//Insert new prescription
			int drug_id = rs.getInt("drug_id");

			query = "INSERT into prescription(prescribing_doctor_id, prescription_patient_id, drug_id, " +
					"drug_quantity, max_refills, total_cost) values (?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, p.getDoctor_id());
			preparedStatement.setInt(2, p.getPatient_id());
			preparedStatement.setInt(3, drug_id);
			preparedStatement.setInt(4, p.getQuantity());
			preparedStatement.setInt(5, p.getRefills());
			preparedStatement.setString(6, p.getCost());

			rs = preparedStatement.executeQuery();

			//Get generated value for rxid
			query = "SELECT rxid FROM prescription WHERE prescribing_doctor_id = ? AND prescribing_patient_id = ?" +
					" AND drug_id = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, p.getDoctor_id());
			preparedStatement.setInt(2, p.getPatient_id());
			preparedStatement.setInt(3, drug_id);

			rs = preparedStatement.executeQuery();

			// Update prescription object and return
			p.setRxid(rs.getString("rxid"));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				// close all resources
				if (rs != null) rs.close();
				if (connection != null) connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		//BOILER CODE
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
