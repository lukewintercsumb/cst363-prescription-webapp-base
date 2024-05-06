USE mydb;

SELECT * FROM patient;

INSERT INTO doctor
(doctor_id, ssn, first_name, last_name, specialty, first_year_practice)
VALUES
(1, "012345678", "John", "Smith", "pediatrics", 2000),
(2, "989898989", "Jane", "Doe", "dermatology", 1989),
(3, "111111111", "Liam", "Moore", "oncology", 2003);

INSERT INTO patient
(patient_id, primary_physician_id, ssn, first_name, last_name, date_of_birth, street_address)
VALUES
(1, 1, "012321210", "Evan", "Phillips", "2007-09-08", "557 Spring Dr.
Hattiesburg, MS 39401"),
(2, 3, "777777777", "Lucky", "Man", "2019-10-23", "Lucky St. 77777"),
(3, 2, "098767898", "Carl", "Kahn", "1969-12-09", "23 Bradford St.
Sarasota, FL 34231"),
(4, 1, "676776767", "Joey", "Jones", "2015-05-12", "77 East Trout Street
Coram, NY 11727");

INSERT INTO pharmacy
(pharmacy_id, pharmacy_name, address, phone)
VALUES
(1, "CVS", "10 William Avenue
Tacoma, WA 98444", "8758885655"),
(2, "CVS", "9405 Armstrong Lane
Commack, NY 11725", "9873349870"),
(3, "Wallgreens", "257 Foster Ave.
Zionsville, IN 46077", "1224568878");

INSERT INTO drug
(drug_id, drug_name, drug_brand)
VALUES
(1, "cancer-cure", "alwayshappy"),
(2, "cough-be-gone", "alwayshappy"),
(3, "cancer-cure", "evilcorp");

INSERT INTO inventory
(drug_id, pharmacy_id, drug_unit_price, drug_stock)
VALUES
(1, 1, 40.00, 20),
(1, 2, 45.00, 15),
(1, 3, 42.00, 80),
(2, 1,