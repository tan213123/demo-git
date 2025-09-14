package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Guest implements Serializable {
    private String nationalId;
    private String fullName;
    private LocalDate birthDate;
    private String gender; // Male/Female
    private String phone;
    private String coTenant;

    public Guest(String nationalId, String fullName, LocalDate birthDate, String gender, String phone, String coTenant) {
        this.nationalId = nationalId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.coTenant = coTenant;
    }

    public String getNationalId() { return nationalId; }
    public String getFullName() { return fullName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getCoTenant() { return coTenant; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCoTenant(String coTenant) { this.coTenant = coTenant; }
}