package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

		System.out.println("processFillForm " + p);

		// TODO
		// obtain connection to database.
		try (Connection con = getConnection()) {


		// valid pharmacy name and address in the prescription object and obtain the
		// pharmacy id.
			if (p.getPharmacyName() == null || p.getPharmacyAddress() == null) {
				throw new IllegalArgumentException("Pharmacy name and address cannot be empty.");
			}
		PreparedStatement ps = con.prepareStatement("SELECT pharmacyID FROM pharmacies WHERE name = ? AND address = ?");
			ps.setString(1, p.getPharmacyName());
			ps.setString(2, p.getPharmacyAddress());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setPharmacyID(rs.getInt("pharmacyID"));
			} else {
				throw new IllegalArgumentException("Pharmacy not found.");
			}

		// get prescription information for the rxid value and patient last name from
		// prescription object.
//		ps = con.prepareStatement("SELECT * FROM prescriptions WHERE rxid = ? AND patientLastName = ?");
//		ps.setString(1, p.getRxid());
//		ps.setString(2, p.getPatientLastName());
//		rs = ps.executeQuery();

		ps = con.prepareStatement("SELECT * FROM prescriptions WHERE rx_id = ? AND prescrption_patient_id = (SELECT patient_id FROM patient WHERE last_name = ?)");
		ps.setString(1, p.getRxid());
		ps.setString(2, p.getPatientLastName());
		rs = ps.executeQuery();

		// copy prescription information into the prescription object for display.

		// get cost of drug and copy into prescription for display.
			if (rs.next()) {
				// Assuming all required fields are in the result set
				p.setDrugName(rs.getString("drugName"));
				p.setDateCreated(rs.getString("dateCreated"));
				p.setRefills(rs.getInt("refills"));
				p.setCost(rs.getString("cost"));
			} else {
				throw new IllegalArgumentException("Prescription not found.");
			}
		// update prescription table row with pharmacy id, fill date.
//	ps = con.prepareStatement("UPDATE prescriptions SET pharmacyID = ?, dateFilled = ? WHERE rxid = ?");
//	ps.setInt(1, p.getPharmacyID());
//	ps.setString(2, LocalDate.now().toString());
//	ps.setString(3, p.getRxid());
//	ps.executeUpdate();

	ps = con.prepareStatement("UPDATE prescriptions SET pharmacyID = ?, dateFilled = ? WHERE rx_id = ?");
	ps.setInt(1, p.getPharmacyID());
	ps.setString(2, LocalDate.now().toString());
	ps.setString(3, p.getRxid());
	ps.executeUpdate();

	model.addAttribute("message", "Prescription filled.");
	model.addAttribute("prescription", p);
	return "prescription_show";

		// if there is error
		// model.addAttribute("message", <error message>);
		// model.addAttribute("prescription", p);
		// return "prescription_fill";
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
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}