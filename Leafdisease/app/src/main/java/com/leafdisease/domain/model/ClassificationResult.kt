package com.leafdisease.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.leafdisease.R

/**
 * Data class representing the result of a leaf disease classification.
 */
data class ClassificationResult(
    val diseaseName: String,
    val scientificName: String?,
    val confidence: Float,
    val isHealthy: Boolean,
    val description: String,
    val treatmentRecommendations: List<TreatmentStep>,
    val plantType: String,
    val confidenceLevel: ConfidenceLevel,
    /** Etiqueta tal como aparece en labels.txt, ej. "Apple___Apple_scab". */
    val rawLabel: String = "",
) {
    enum class ConfidenceLevel {
        HIGH,    // > 0.85
        MEDIUM,  // 0.60 - 0.85
        LOW      // < 0.60
    }
}

/**
 * Represents a treatment step for a detected disease.
 */
data class TreatmentStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val priority: Priority
) {
    enum class Priority {
        HIGH, MEDIUM, LOW
    }
}

/**
 * Plant and disease information from PlantVillage dataset.
 * Strings are stored as resource IDs and resolved via Context at classification time.
 */
object PlantDiseaseInfo {

    private data class TreatmentStepRes(
        val stepNumber: Int,
        @StringRes val titleRes: Int,
        @StringRes val descriptionRes: Int,
        val priority: TreatmentStep.Priority
    )

    private data class DiseaseInfoRes(
        val classIndex: Int,
        val plantType: String,
        @StringRes val diseaseNameRes: Int,
        val scientificName: String?,
        val isHealthy: Boolean,
        @StringRes val descriptionRes: Int,
        val treatmentSteps: List<TreatmentStepRes>
    )

    private val diseaseDatabase: Map<Int, DiseaseInfoRes> = mapOf(
        // Apple
        0 to DiseaseInfoRes(
            classIndex = 0,
            plantType = "Apple",
            diseaseNameRes = R.string.disease_apple_scab,
            scientificName = "Venturia inaequalis",
            isHealthy = false,
            descriptionRes = R.string.desc_apple_scab,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_apple_scab_1_title, R.string.treatment_apple_scab_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_apple_scab_2_title, R.string.treatment_apple_scab_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_apple_scab_3_title, R.string.treatment_apple_scab_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        1 to DiseaseInfoRes(
            classIndex = 1,
            plantType = "Apple",
            diseaseNameRes = R.string.disease_apple_black_rot,
            scientificName = "Botryosphaeria obtusa",
            isHealthy = false,
            descriptionRes = R.string.desc_apple_black_rot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_apple_black_rot_1_title, R.string.treatment_apple_black_rot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_apple_black_rot_2_title, R.string.treatment_apple_black_rot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_apple_black_rot_3_title, R.string.treatment_apple_black_rot_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        2 to DiseaseInfoRes(
            classIndex = 2,
            plantType = "Apple",
            diseaseNameRes = R.string.disease_cedar_apple_rust,
            scientificName = "Gymnosporangium juniperi-virginianae",
            isHealthy = false,
            descriptionRes = R.string.desc_cedar_apple_rust,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_cedar_apple_rust_1_title, R.string.treatment_cedar_apple_rust_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_cedar_apple_rust_2_title, R.string.treatment_cedar_apple_rust_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_cedar_apple_rust_3_title, R.string.treatment_cedar_apple_rust_3_desc, TreatmentStep.Priority.LOW)
            )
        ),
        3 to DiseaseInfoRes(
            classIndex = 3,
            plantType = "Apple",
            diseaseNameRes = R.string.disease_apple_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_apple_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_continue_maintenance_title, R.string.treatment_continue_maintenance_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_monitor_regularly_title, R.string.treatment_monitor_regularly_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_prune_as_needed_title, R.string.treatment_prune_as_needed_desc, TreatmentStep.Priority.LOW)
            )
        ),

        // Blueberry
        4 to DiseaseInfoRes(
            classIndex = 4,
            plantType = "Blueberry",
            diseaseNameRes = R.string.disease_blueberry_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_blueberry_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_blueberry_1_title, R.string.treatment_blueberry_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_blueberry_2_title, R.string.treatment_blueberry_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_blueberry_3_title, R.string.treatment_blueberry_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Cherry
        5 to DiseaseInfoRes(
            classIndex = 5,
            plantType = "Cherry",
            diseaseNameRes = R.string.disease_cherry_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_cherry_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_cherry_1_title, R.string.treatment_cherry_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_cherry_2_title, R.string.treatment_cherry_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_cherry_3_title, R.string.treatment_cherry_3_desc, TreatmentStep.Priority.LOW)
            )
        ),
        6 to DiseaseInfoRes(
            classIndex = 6,
            plantType = "Cherry",
            diseaseNameRes = R.string.disease_cherry_powdery_mildew,
            scientificName = "Podosphaera clandestina",
            isHealthy = false,
            descriptionRes = R.string.desc_cherry_powdery_mildew,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_cherry_powdery_mildew_1_title, R.string.treatment_squash_powdery_mildew_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_cherry_powdery_mildew_2_title, R.string.treatment_grape_leaf_blight_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_cherry_powdery_mildew_3_title, R.string.treatment_tomato_leaf_mold_2_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Corn
        7 to DiseaseInfoRes(
            classIndex = 7,
            plantType = "Corn",
            diseaseNameRes = R.string.disease_corn_cercospora_leaf_spot,
            scientificName = "Cercospora zeae-maydis",
            isHealthy = false,
            descriptionRes = R.string.desc_corn_cercospora_leaf_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_corn_cercospora_1_title, R.string.treatment_corn_cercospora_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_corn_cercospora_2_title, R.string.treatment_corn_cercospora_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_corn_cercospora_3_title, R.string.treatment_corn_cercospora_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        8 to DiseaseInfoRes(
            classIndex = 8,
            plantType = "Corn",
            diseaseNameRes = R.string.disease_corn_common_rust,
            scientificName = "Puccinia sorghi",
            isHealthy = false,
            descriptionRes = R.string.desc_corn_common_rust,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_corn_rust_1_title, R.string.treatment_corn_rust_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_corn_rust_2_title, R.string.treatment_corn_rust_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_corn_rust_3_title, R.string.treatment_corn_rust_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        9 to DiseaseInfoRes(
            classIndex = 9,
            plantType = "Corn",
            diseaseNameRes = R.string.disease_corn_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_corn_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_corn_healthy_1_title, R.string.treatment_corn_healthy_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_corn_healthy_2_title, R.string.treatment_corn_healthy_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_corn_healthy_3_title, R.string.treatment_corn_healthy_3_desc, TreatmentStep.Priority.LOW)
            )
        ),
        10 to DiseaseInfoRes(
            classIndex = 10,
            plantType = "Corn",
            diseaseNameRes = R.string.disease_corn_northern_leaf_blight,
            scientificName = "Exserohilum turcicum",
            isHealthy = false,
            descriptionRes = R.string.desc_corn_northern_leaf_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_corn_blight_1_title, R.string.treatment_corn_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_corn_blight_2_title, R.string.treatment_corn_blight_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_corn_blight_3_title, R.string.treatment_corn_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Grape
        11 to DiseaseInfoRes(
            classIndex = 11,
            plantType = "Grape",
            diseaseNameRes = R.string.disease_grape_black_rot,
            scientificName = "Guignardia bidwellii",
            isHealthy = false,
            descriptionRes = R.string.desc_grape_black_rot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_grape_black_rot_1_title, R.string.treatment_grape_black_rot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_grape_black_rot_2_title, R.string.treatment_grape_black_rot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_grape_black_rot_3_title, R.string.treatment_grape_black_rot_3_desc, TreatmentStep.Priority.HIGH)
            )
        ),
        12 to DiseaseInfoRes(
            classIndex = 12,
            plantType = "Grape",
            diseaseNameRes = R.string.disease_grape_esca,
            scientificName = "Phaeomoniella chlamydospora",
            isHealthy = false,
            descriptionRes = R.string.desc_grape_esca,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_grape_esca_1_title, R.string.treatment_grape_esca_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_grape_esca_2_title, R.string.treatment_grape_esca_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_grape_esca_3_title, R.string.treatment_grape_esca_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        13 to DiseaseInfoRes(
            classIndex = 13,
            plantType = "Grape",
            diseaseNameRes = R.string.disease_grape_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_grape_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_grape_healthy_1_title, R.string.treatment_grape_healthy_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_grape_healthy_2_title, R.string.treatment_grape_healthy_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_grape_healthy_3_title, R.string.treatment_grape_healthy_3_desc, TreatmentStep.Priority.LOW)
            )
        ),
        14 to DiseaseInfoRes(
            classIndex = 14,
            plantType = "Grape",
            diseaseNameRes = R.string.disease_grape_leaf_blight,
            scientificName = "Isariopsis clavispora",
            isHealthy = false,
            descriptionRes = R.string.desc_grape_leaf_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_grape_leaf_blight_1_title, R.string.treatment_grape_leaf_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_grape_leaf_blight_2_title, R.string.treatment_grape_leaf_blight_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_grape_leaf_blight_3_title, R.string.treatment_grape_leaf_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Orange / Citrus
        15 to DiseaseInfoRes(
            classIndex = 15,
            plantType = "Orange",
            diseaseNameRes = R.string.disease_citrus_greening,
            scientificName = "Candidatus Liberibacter",
            isHealthy = false,
            descriptionRes = R.string.desc_citrus_greening,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_citrus_greening_1_title, R.string.treatment_citrus_greening_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_citrus_greening_2_title, R.string.treatment_citrus_greening_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_citrus_greening_3_title, R.string.treatment_citrus_greening_3_desc, TreatmentStep.Priority.HIGH)
            )
        ),

        // Peach
        16 to DiseaseInfoRes(
            classIndex = 16,
            plantType = "Peach",
            diseaseNameRes = R.string.disease_peach_bacterial_spot,
            scientificName = "Xanthomonas campestris",
            isHealthy = false,
            descriptionRes = R.string.desc_peach_bacterial_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_peach_bacterial_spot_1_title, R.string.treatment_tomato_bacterial_spot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_peach_bacterial_spot_2_title, R.string.treatment_tomato_bacterial_spot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_peach_bacterial_spot_3_title, R.string.treatment_tomato_bacterial_spot_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        17 to DiseaseInfoRes(
            classIndex = 17,
            plantType = "Peach",
            diseaseNameRes = R.string.disease_peach_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_peach_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_peach_healthy_1_title, R.string.treatment_peach_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_peach_healthy_2_title, R.string.treatment_peach_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_peach_healthy_3_title, R.string.treatment_peach_3_desc, TreatmentStep.Priority.LOW)
            )
        ),

        // Pepper
        18 to DiseaseInfoRes(
            classIndex = 18,
            plantType = "Pepper",
            diseaseNameRes = R.string.disease_pepper_bacterial_spot,
            scientificName = "Xanthomonas campestris",
            isHealthy = false,
            descriptionRes = R.string.desc_pepper_bacterial_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_pepper_bacterial_spot_1_title, R.string.treatment_pepper_bacterial_spot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_pepper_bacterial_spot_2_title, R.string.treatment_tomato_bacterial_spot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_pepper_bacterial_spot_3_title, R.string.treatment_tomato_leaf_mold_2_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        19 to DiseaseInfoRes(
            classIndex = 19,
            plantType = "Pepper",
            diseaseNameRes = R.string.disease_pepper_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_pepper_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_pepper_healthy_1_title, R.string.treatment_pepper_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_pepper_healthy_2_title, R.string.treatment_pepper_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_pepper_healthy_3_title, R.string.treatment_pepper_3_desc, TreatmentStep.Priority.LOW)
            )
        ),

        // Potato
        20 to DiseaseInfoRes(
            classIndex = 20,
            plantType = "Potato",
            diseaseNameRes = R.string.disease_potato_early_blight,
            scientificName = "Alternaria solani",
            isHealthy = false,
            descriptionRes = R.string.desc_potato_early_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_potato_early_blight_1_title, R.string.treatment_potato_early_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_potato_early_blight_2_title, R.string.treatment_potato_early_blight_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_potato_early_blight_3_title, R.string.treatment_potato_early_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        21 to DiseaseInfoRes(
            classIndex = 21,
            plantType = "Potato",
            diseaseNameRes = R.string.disease_potato_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_potato_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_potato_healthy_1_title, R.string.treatment_potato_healthy_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_potato_healthy_2_title, R.string.treatment_potato_healthy_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_potato_healthy_3_title, R.string.treatment_potato_healthy_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        22 to DiseaseInfoRes(
            classIndex = 22,
            plantType = "Potato",
            diseaseNameRes = R.string.disease_potato_late_blight,
            scientificName = "Phytophthora infestans",
            isHealthy = false,
            descriptionRes = R.string.desc_potato_late_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_potato_late_blight_1_title, R.string.treatment_potato_late_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_potato_late_blight_2_title, R.string.treatment_potato_late_blight_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_potato_late_blight_3_title, R.string.treatment_potato_late_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Raspberry
        23 to DiseaseInfoRes(
            classIndex = 23,
            plantType = "Raspberry",
            diseaseNameRes = R.string.disease_raspberry_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_raspberry_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_raspberry_1_title, R.string.treatment_raspberry_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_raspberry_2_title, R.string.treatment_raspberry_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_raspberry_3_title, R.string.treatment_raspberry_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Soybean
        24 to DiseaseInfoRes(
            classIndex = 24,
            plantType = "Soybean",
            diseaseNameRes = R.string.disease_soybean_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_soybean_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_soybean_1_title, R.string.treatment_soybean_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_soybean_2_title, R.string.treatment_soybean_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_soybean_3_title, R.string.treatment_soybean_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Squash
        25 to DiseaseInfoRes(
            classIndex = 25,
            plantType = "Squash",
            diseaseNameRes = R.string.disease_squash_powdery_mildew,
            scientificName = "Podosphaera xanthii",
            isHealthy = false,
            descriptionRes = R.string.desc_squash_powdery_mildew,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_squash_powdery_mildew_1_title, R.string.treatment_squash_powdery_mildew_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_squash_powdery_mildew_2_title, R.string.treatment_squash_powdery_mildew_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_squash_powdery_mildew_3_title, R.string.treatment_squash_powdery_mildew_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Strawberry
        26 to DiseaseInfoRes(
            classIndex = 26,
            plantType = "Strawberry",
            diseaseNameRes = R.string.disease_strawberry_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_strawberry_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_strawberry_healthy_1_title, R.string.treatment_strawberry_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_strawberry_healthy_2_title, R.string.treatment_strawberry_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_strawberry_healthy_3_title, R.string.treatment_strawberry_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        27 to DiseaseInfoRes(
            classIndex = 27,
            plantType = "Strawberry",
            diseaseNameRes = R.string.disease_strawberry_leaf_scorch,
            scientificName = "Diplocarpon earliana",
            isHealthy = false,
            descriptionRes = R.string.desc_strawberry_leaf_scorch,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_strawberry_leaf_scorch_1_title, R.string.treatment_strawberry_leaf_scorch_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_strawberry_leaf_scorch_2_title, R.string.treatment_strawberry_leaf_scorch_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_strawberry_leaf_scorch_3_title, R.string.treatment_strawberry_leaf_scorch_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Tomato
        28 to DiseaseInfoRes(
            classIndex = 28,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_bacterial_spot,
            scientificName = "Xanthomonas vesicatoria",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_bacterial_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_bacterial_spot_1_title, R.string.treatment_tomato_bacterial_spot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_bacterial_spot_2_title, R.string.treatment_tomato_bacterial_spot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_bacterial_spot_3_title, R.string.treatment_tomato_bacterial_spot_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        29 to DiseaseInfoRes(
            classIndex = 29,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_early_blight,
            scientificName = "Alternaria solani",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_early_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_early_blight_1_title, R.string.treatment_tomato_early_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_early_blight_2_title, R.string.treatment_tomato_early_blight_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_early_blight_3_title, R.string.treatment_tomato_early_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        30 to DiseaseInfoRes(
            classIndex = 30,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_healthy,
            scientificName = null,
            isHealthy = true,
            descriptionRes = R.string.desc_tomato_healthy,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_healthy_1_title, R.string.treatment_healthy_1_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(2, R.string.treatment_tomato_healthy_2_title, R.string.treatment_healthy_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_tomato_healthy_3_title, R.string.treatment_healthy_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        31 to DiseaseInfoRes(
            classIndex = 31,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_late_blight,
            scientificName = "Phytophthora infestans",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_late_blight,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_late_blight_1_title, R.string.treatment_tomato_late_blight_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_late_blight_2_title, R.string.treatment_tomato_late_blight_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_late_blight_3_title, R.string.treatment_tomato_late_blight_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        32 to DiseaseInfoRes(
            classIndex = 32,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_leaf_mold,
            scientificName = "Passalora fulva",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_leaf_mold,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_leaf_mold_1_title, R.string.treatment_tomato_leaf_mold_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_leaf_mold_2_title, R.string.treatment_tomato_leaf_mold_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_leaf_mold_3_title, R.string.treatment_tomato_leaf_mold_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        33 to DiseaseInfoRes(
            classIndex = 33,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_septoria_leaf_spot,
            scientificName = "Septoria lycopersici",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_septoria_leaf_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_septoria_1_title, R.string.treatment_tomato_septoria_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_septoria_2_title, R.string.treatment_tomato_septoria_2_desc, TreatmentStep.Priority.MEDIUM),
                TreatmentStepRes(3, R.string.treatment_tomato_septoria_3_title, R.string.treatment_tomato_septoria_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        34 to DiseaseInfoRes(
            classIndex = 34,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_spider_mites,
            scientificName = "Tetranychus urticae",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_spider_mites,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_spider_mites_1_title, R.string.treatment_tomato_spider_mites_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_spider_mites_2_title, R.string.treatment_tomato_spider_mites_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_spider_mites_3_title, R.string.treatment_tomato_spider_mites_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        35 to DiseaseInfoRes(
            classIndex = 35,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_target_spot,
            scientificName = "Corynespora cassiicola",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_target_spot,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_target_spot_1_title, R.string.treatment_tomato_target_spot_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_target_spot_2_title, R.string.treatment_tomato_target_spot_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_target_spot_3_title, R.string.treatment_tomato_target_spot_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        36 to DiseaseInfoRes(
            classIndex = 36,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_mosaic_virus,
            scientificName = "Tobacco Mosaic Virus",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_mosaic_virus,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_mosaic_1_title, R.string.treatment_tomato_mosaic_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_mosaic_2_title, R.string.treatment_tomato_mosaic_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_mosaic_3_title, R.string.treatment_tomato_mosaic_3_desc, TreatmentStep.Priority.MEDIUM)
            )
        ),
        37 to DiseaseInfoRes(
            classIndex = 37,
            plantType = "Tomato",
            diseaseNameRes = R.string.disease_tomato_yellow_leaf_curl_virus,
            scientificName = "Tomato Yellow Leaf Curl Virus",
            isHealthy = false,
            descriptionRes = R.string.desc_tomato_yellow_leaf_curl_virus,
            treatmentSteps = listOf(
                TreatmentStepRes(1, R.string.treatment_tomato_yellow_leaf_curl_1_title, R.string.treatment_tomato_yellow_leaf_curl_1_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(2, R.string.treatment_tomato_yellow_leaf_curl_2_title, R.string.treatment_tomato_yellow_leaf_curl_2_desc, TreatmentStep.Priority.HIGH),
                TreatmentStepRes(3, R.string.treatment_tomato_yellow_leaf_curl_3_title, R.string.treatment_tomato_yellow_leaf_curl_3_desc, TreatmentStep.Priority.HIGH)
            )
        )
    )

    /**
     * Builds a fully localized ClassificationResult using the device's current locale.
     */
    fun getClassificationResult(
        classIndex: Int,
        confidence: Float,
        rawLabel: String,
        context: Context
    ): ClassificationResult? {
        val info = diseaseDatabase[classIndex] ?: return null
        return ClassificationResult(
            diseaseName = context.getString(info.diseaseNameRes),
            scientificName = info.scientificName,
            confidence = confidence,
            isHealthy = info.isHealthy,
            description = context.getString(info.descriptionRes),
            treatmentRecommendations = info.treatmentSteps.map { step ->
                TreatmentStep(
                    stepNumber = step.stepNumber,
                    title = context.getString(step.titleRes),
                    description = context.getString(step.descriptionRes),
                    priority = step.priority
                )
            },
            plantType = info.plantType,
            confidenceLevel = getConfidenceLevel(confidence),
            rawLabel = rawLabel
        )
    }

    fun getAllDiseaseNames(): List<String> =
        diseaseDatabase.values.sortedBy { it.classIndex }.map { it.classIndex.toString() }

    fun getConfidenceLevel(confidence: Float): ClassificationResult.ConfidenceLevel = when {
        confidence > 0.85f -> ClassificationResult.ConfidenceLevel.HIGH
        confidence > 0.60f -> ClassificationResult.ConfidenceLevel.MEDIUM
        else               -> ClassificationResult.ConfidenceLevel.LOW
    }
}
