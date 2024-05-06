-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`doctor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`doctor` ;

CREATE TABLE IF NOT EXISTS `mydb`.`doctor` (
  `doctor_id` INT NOT NULL,
  `ssn` CHAR(9) NULL,
  `first_name` VARCHAR(32) NULL,
  `last_name` VARCHAR(32) NULL,
  `specialty` VARCHAR(32) NULL,
  `first_year_practice` INT NULL,
  PRIMARY KEY (`doctor_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`patient`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`patient` ;

CREATE TABLE IF NOT EXISTS `mydb`.`patient` (
  `patient_id` INT NOT NULL,
  `primary_physician_id` INT NOT NULL,
  `ssn` CHAR(9) NULL,
  `first_name` VARCHAR(32) NULL,
  `last_name` VARCHAR(32) NULL,
  `date_of_birth` DATE NULL,
  `street_address` VARCHAR(64) NULL,
  PRIMARY KEY (`patient_id`),
  INDEX `fk_patient_doctor_idx` (`primary_physician_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_doctor`
    FOREIGN KEY (`primary_physician_id`)
    REFERENCES `mydb`.`doctor` (`doctor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`drug`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`drug` ;

CREATE TABLE IF NOT EXISTS `mydb`.`drug` (
  `drug_id` INT NOT NULL,
  `drug_name` VARCHAR(64) NULL,
  `drug_brand` VARCHAR(32) NULL,
  PRIMARY KEY (`drug_id`),
  UNIQUE INDEX `drug_name_UNIQUE` (`drug_id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`prescription`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`prescription` ;

CREATE TABLE IF NOT EXISTS `mydb`.`prescription` (
  `rx_id` INT NOT NULL,
  `prescribing_doctor_id` INT NOT NULL,
  `prescrption_patient_id` INT NOT NULL,
  `drug_id` INT NOT NULL,
  `drug_quantity` INT NULL,
  `max_refills` INT NULL,
  `total_cost` DECIMAL(5,2) NULL,
  PRIMARY KEY (`rx_id`),
  INDEX `fk_prescription_doctor1_idx` (`prescribing_doctor_id` ASC) VISIBLE,
  INDEX `fk_prescription_patient1_idx` (`prescrption_patient_id` ASC) VISIBLE,
  INDEX `fk_prescription_drug1_idx` (`drug_id` ASC) VISIBLE,
  CONSTRAINT `fk_prescription_doctor1`
    FOREIGN KEY (`prescribing_doctor_id`)
    REFERENCES `mydb`.`doctor` (`doctor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_patient1`
    FOREIGN KEY (`prescrption_patient_id`)
    REFERENCES `mydb`.`patient` (`patient_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_drug1`
    FOREIGN KEY (`drug_id`)
    REFERENCES `mydb`.`drug` (`drug_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`pharmacy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`pharmacy` ;

CREATE TABLE IF NOT EXISTS `mydb`.`pharmacy` (
  `pharmacy_id` INT NOT NULL,
  `pharmacy_name` VARCHAR(32) NULL,
  `address` VARCHAR(64) NULL,
  `phone` VARCHAR(10) NULL,
  PRIMARY KEY (`pharmacy_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`inventory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`inventory`;

CREATE TABLE IF NOT EXISTS `mydb`.`inventory` (
  `drug_id` INT NOT NULL,
  `pharmacy_id` INT NOT NULL,
  `drug_unit_price` DECIMAL(5,2) NULL,
  `drug_stock` INT NULL,
  PRIMARY KEY (`drug_id`, `pharmacy_id`),
  INDEX `fk_drug_has_pharmacy_pharmacy1_idx` (`pharmacy_id` ASC) VISIBLE,
  INDEX `fk_drug_has_pharmacy_drug1_idx` (`drug_id` ASC) VISIBLE,
  CONSTRAINT `fk_drug_has_pharmacy_drug1`
    FOREIGN KEY (`drug_id`)
    REFERENCES `mydb`.`drug` (`drug_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_drug_has_pharmacy_pharmacy1`
    FOREIGN KEY (`pharmacy_id`)
    REFERENCES `mydb`.`pharmacy` (`pharmacy_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`refill`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`refill` ;

CREATE TABLE IF NOT EXISTS `mydb`.`refill` (
  `pharmacy_id` INT NOT NULL,
  `prescription_rx_id` INT NOT NULL,
  `date` DATE NULL,
  `refill_number` INT NULL,
  INDEX `fk_refill_pharmacy1_idx` (`pharmacy_id` ASC) VISIBLE,
  INDEX `fk_refill_prescription1_idx` (`prescription_rx_id` ASC) VISIBLE,
  CONSTRAINT `fk_refill_pharmacy1`
    FOREIGN KEY (`pharmacy_id`)
    REFERENCES `mydb`.`pharmacy` (`pharmacy_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_refill_prescription1`
    FOREIGN KEY (`prescription_rx_id`)
    REFERENCES `mydb`.`prescription` (`rx_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
