package com.csumb.cst363;

import java.sql.*;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
				// Validate pharmacy name and address
				if (p.getPharmacyName() == null || p.getPharmacyAddress() == null) {
					throw new IllegalArgumentException("Pharmacy name and address cannot be empty.");
				}

				// Fetching the pharmacy ID
				PreparedStatement ps = con.prepareStatement("SELECT pharmacy_id FROM pharmacy WHERE pharmacy_name = ? AND address = ?");
				ps.setString(1, p.getPharmacyName());
				ps.setString(2, p.getPharmacyAddress());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					p.setPharmacyID(rs.getInt("pharmacy_id"));
				} else {
					throw new IllegalArgumentException("Pharmacy not found.");
				}

				// Fetching prescription details
				ps = con.prepareStatement("SELECT * FROM prescription WHERE rx_id = ? AND prescription_patient_id = (SELECT patient_id FROM patient WHERE last_name = ?)");
				ps.setString(1, p.getRxid());
				ps.setString(2, p.getPatientLastName());
				rs = ps.executeQuery();
				if (rs.next()) {
					p.setDrugName(rs.getString("drug_name"));  // Assuming drug_name is part of the SELECT statement
					p.setDateCreated(rs.getString("date_created"));  // Assuming date_created is part of the SELECT statement
					p.setRefills(rs.getInt("refills"));
					p.setCost(rs.getString("total_cost"));
				} else {
					throw new IllegalArgumentException("Prescription not found.");
				}

				// Updating the prescription record
				ps = con.prepareStatement("UPDATE prescription SET pharmacy_id = ?, date_filled = ? WHERE rx_id = ?");
				ps.setInt(1, p.getPharmacyID());
				ps.setString(2, LocalDate.now().toString());
				ps.setString(3, p.getRxid());
				ps.executeUpdate();

				model.addAttribute("message", "Prescription filled.");
				model.addAttribute("prescription", p);
				return "prescription_show";
			} catch (SQLException e) {
				model.addAttribute("message", "Database error: " + e.getMessage());
				model.addAttribute("prescription", p);
				return "prescription_fill";
			} catch (IllegalArgumentException e) {
				model.addAttribute("message", e.getMessage());
				model.addAttribute("prescription", p);
				return "prescription_fill";
			}
		}
	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "CT$*mv1RK$^$y%&wR18T");
		return conn;
	}

}