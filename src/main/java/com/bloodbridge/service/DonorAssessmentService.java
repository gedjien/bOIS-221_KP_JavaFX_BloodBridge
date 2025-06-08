package com.bloodbridge.service;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.DonorAssessment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для оценки состояния доноров в системе BloodBridge.
 * Обеспечивает проведение медицинской оценки.
 * Реализует логику определения пригодности донора.
 */
public class DonorAssessmentService {
    private static final List<DonorAssessment> assessments = new ArrayList<>();
    
    static {
        loadAssessments();
    }
    
    public static void loadAssessments() {
        assessments.clear();
        // Implementation of loadAssessments method
    }
    
    public static void saveAssessments() {
        // Implementation of saveAssessments method
    }
    
    public static List<DonorAssessment> getAllAssessments() {
        return assessments;
    }
    
    public static List<DonorAssessment> getDonorAssessments(Long donorId) {
        return assessments.stream()
                .filter(assessment -> assessment.getDonorId().equals(donorId))
                .toList();
    }
    
    public static void addAssessment(DonorAssessment assessment) {
        assessments.add(assessment);
    }
    
    public static void removeAssessment(DonorAssessment assessment) {
        assessments.remove(assessment);
    }
    
    public static void updateAssessment(DonorAssessment assessment) {
        for (int i = 0; i < assessments.size(); i++) {
            if (assessments.get(i).getId() == assessment.getId()) {
                assessments.set(i, assessment);
                break;
            }
        }
        saveAssessments();
    }
    
    public static boolean validateAssessment(DonorAssessment assessment) {
        if (assessment.getDonorId() <= 0) {
            return false;
        }
        if (assessment.getAssessmentDate() == null) {
            return false;
        }
        if (assessment.getBloodPressure() == null || assessment.getBloodPressure().isEmpty()) {
            return false;
        }
        if (assessment.getHemoglobin() <= 0) {
            return false;
        }
        if (assessment.getWeight() <= 0) {
            return false;
        }
        return true;
    }
    
    public static String getAssessmentResult(DonorAssessment assessment) {
        StringBuilder result = new StringBuilder();
        
        if (!validateAssessment(assessment)) {
            result.append("Донор не допущен к сдаче крови.\nПричины:\n");
            
            if (assessment.getSystolicPressure() < 90 || assessment.getSystolicPressure() > 180 ||
                assessment.getDiastolicPressure() < 60 || assessment.getDiastolicPressure() > 100) {
                result.append("- Некорректное артериальное давление\n");
            }
            
            if (assessment.getPulse() < 50 || assessment.getPulse() > 100) {
                result.append("- Некорректный пульс\n");
            }
            
            if (assessment.getHemoglobin() < 120) {
                result.append("- Низкий уровень гемоглобина\n");
            }
            
            if (assessment.getTemperature() < 36.2 || assessment.getTemperature() > 37.2) {
                result.append("- Некорректная температура тела\n");
            }
            
            if (assessment.getWeight() < 50) {
                result.append("- Недостаточный вес\n");
            }
            
            if (assessment.isHasFever()) {
                result.append("- Повышенная температура\n");
            }
            
            if (assessment.isHasCold()) {
                result.append("- Простуда\n");
            }
            
            if (assessment.isHasRecentSurgery()) {
                result.append("- Недавняя операция\n");
            }
            
            if (assessment.isHasPregnancy()) {
                result.append("- Беременность\n");
            }
            
            assessment.setEligible(false);
        } else {
            result.append("Донор допущен к сдаче крови.");
            assessment.setEligible(true);
        }
        
        return result.toString();
    }
    
    public static List<DonorAssessment> getAssessmentsByDonorId(Long donorId) {
        List<DonorAssessment> donorAssessments = new ArrayList<>();
        for (DonorAssessment assessment : assessments) {
            if (assessment.getDonorId() == donorId) {
                donorAssessments.add(assessment);
            }
        }
        return donorAssessments;
    }

    public static Optional<DonorAssessment> getLatestAssessmentByDonorId(Long donorId) {
        DonorAssessment latestAssessment = null;
        LocalDateTime latestDateTime = null;

        for (DonorAssessment assessment : assessments) {
            if (assessment.getDonorId() == donorId) {
                LocalDateTime assessmentDateTime = assessment.getAssessmentDate();
                if (latestDateTime == null || assessmentDateTime.isAfter(latestDateTime)) {
                    latestDateTime = assessmentDateTime;
                    latestAssessment = assessment;
                }
            }
        }

        return Optional.ofNullable(latestAssessment);
    }

    public static boolean isDonorEligible(Donor donor) {
        Optional<DonorAssessment> latestAssessment = getLatestAssessmentByDonorId(donor.getId());
        return latestAssessment.map(DonorAssessment::isEligible).orElse(false);
    }
} 