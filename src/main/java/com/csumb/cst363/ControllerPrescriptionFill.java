package com.csumb.cst363;

import java.sql.*;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
@SuppressWarnings("unused")
@Controller   
public class ControllerPrescriptionFill {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	/*
	 * Patient requests form to search for prescription.
	 * Do not modify this method.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_fill";
	}


	/*
	 * Pharmacy fills prescription.
	 */

	@PostMapping("/prescription/fill")
	public String processFillForm(Prescription p, Model model) {
		try (Connection con = getConnection()) {
			System.out.println("Connection established successfully.");

			// Validate pharmacy name and address
			if (p.getPharmacyName() == null || p.getPharmacyAddress() == null) {
				model.addAttribute("message", "Pharmacy name and address cannot be empty.");
				return "prescription_fill";
			}

			// Fetching the pharmacy ID
			System.out.println("Querying for Pharmacy Name: '" + p.getPharmacyName().trim() + "'");
			System.out.println("Querying for Address without newlines: '" + p.getPharmacyAddress().trim().replace("\n", "") + "'");
			PreparedStatement ps = con.prepareStatement(
					"SELECT pharmacy_id FROM pharmacy WHERE pharmacy_name = ? AND REPLACE(address, '\n', '') = ?"
			);
			ps.setString(1, p.getPharmacyName().trim());
			ps.setString(2, p.getPharmacyAddress().trim().replace("\n", ""));
			System.out.println("Executing query...");
			ResultSet rs = ps.executeQuery();



			// Check if the pharmacy is found
			if (rs.next()) {
				System.out.println("Pharmacy found with ID: " + rs.getInt("pharmacy_id"));
			} else {
				System.out.println("Pharmacy not found. Details provided: " + p.getPharmacyName() + " - " + p.getPharmacyAddress());
				model.addAttribute("message", "Pharmacy not found.");
				return "prescription_fill";
			}
			// Fetching prescription details
			ps = con.prepareStatement("SELECT * FROM prescription WHERE rx_id = ? AND prescription_patient_id = (SELECT patient_id FROM patient WHERE last_name = ?)");
			ps.setString(1, p.getRxid());
			ps.setString(2, p.getPatientLastName());
			rs = ps.executeQuery();
			if (rs.next()) {
				System.out.println("Prescription details found:");
				System.out.println("Drug Name: " + rs.getString("drug_name"));
				System.out.println("Date Created: " + rs.getString("date_created"));
				System.out.println("Refills: " + rs.getInt("refills"));
				System.out.println("Total Cost: " + rs.getString("total_cost"));
				// Populate the prescription object with details from the database
				p.setDrugName(rs.getString("drug_name"));
				p.setDateCreated(rs.getString("date_created"));
				p.setRefills(rs.getInt("refills"));
				p.setCost(rs.getString("total_cost"));
			} else {
				model.addAttribute("message", "Prescription not found.");
				System.out.println("No prescription found for RX ID: " + p.getRxid() + " and Patient Last Name: " + p.getPatientLastName());
				return "prescription_fill";
			}

			// Updating the prescription record
			ps = con.prepareStatement("UPDATE prescription SET pharmacy_id = ?, date_filled = ? WHERE rx_id = ?");
			ps.setInt(1, p.getPharmacyID());
			ps.setString(2, LocalDate.now().toString());
			ps.setString(3, p.getRxid());
			ps.executeUpdate();

			model.addAttribute("message", "Prescription filled successfully.");
			model.addAttribute("prescription", p);
			return "prescription_show";
		} catch (SQLException e) {
			model.addAttribute("message", "Database error: " + e.getMessage());
			System.out.println("SQL Exception: " + e.getMessage());
			return "prescription_fill";
		}
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "password");
        return conn;
    }

}