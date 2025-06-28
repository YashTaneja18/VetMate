package com.example.vetmate.data.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Treatment {

    private String id;
    private String clinicId;        // ID of the clinic (vet) who added the treatment
    private Timestamp date;         // Date of the visit
    private String diagnosis;
    private List<Medication> medications; // List of medications
    private String remarks;
    private Timestamp nextVisitDate;      // Optional follow-up/reminder
    private Timestamp createdAt;

    // Required empty constructor for Firestore
    public Treatment() {}

    public Treatment(String clinicId, Timestamp date, String diagnosis, List<Medication> medications,
                     String remarks, Timestamp nextVisitDate, Timestamp createdAt) {
        this.clinicId = clinicId;
        this.date = date;
        this.diagnosis = diagnosis;
        this.medications = medications;
        this.remarks = remarks;
        this.nextVisitDate = nextVisitDate;
        this.createdAt = createdAt;
    }

    // Medication inner class
    public static class Medication {
        private String name;
        private String dosage;
        private String duration;

        public Medication() {}

        public Medication(String name, String dosage, String duration) {
            this.name = name;
            this.dosage = dosage;
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getNextVisitDate() {
        return nextVisitDate;
    }

    public void setNextVisitDate(Timestamp nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
