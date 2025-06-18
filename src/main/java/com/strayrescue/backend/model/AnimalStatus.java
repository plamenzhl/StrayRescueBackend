package com.strayrescue.backend.model;

public enum AnimalStatus {
    REPORTED,       // Just reported, needs verification
    VERIFIED,       // Confirmed stray animal
    IN_CARE,        // Being cared for by rescuer
    MEDICAL_CARE,   // At vet/receiving treatment
    READY_ADOPTION, // Healthy and ready for adoption
    ADOPTED,        // Successfully adopted
    DECEASED,       // Sadly passed away
    LOST            // Lost/can't be found anymore
}