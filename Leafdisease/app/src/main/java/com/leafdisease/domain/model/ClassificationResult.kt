package com.leafdisease.domain.model

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
    val confidenceLevel: ConfidenceLevel
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
 */
object PlantDiseaseInfo {

    data class DiseaseInfo(
        val classIndex: Int,
        val plantType: String,
        val diseaseName: String,
        val scientificName: String?,
        val isHealthy: Boolean,
        val description: String,
        val treatmentSteps: List<TreatmentStep>
    )

    // All 38 classes from PlantVillage dataset
    private val diseaseDatabase: Map<Int, DiseaseInfo> = mapOf(
        // Apple
        0 to DiseaseInfo(
            classIndex = 0,
            plantType = "Apple",
            diseaseName = "Apple Scab",
            scientificName = "Venturia inaequalis",
            isHealthy = false,
            description = "Fungal disease causing olive-green to black spots on leaves and fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected leaves", "Rake and remove fallen leaves to reduce fungal spores.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use sulfur-based fungicides during early spring.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Prune for airflow", "Improve tree ventilation to reduce humidity.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        1 to DiseaseInfo(
            classIndex = 1,
            plantType = "Apple",
            diseaseName = "Black Rot",
            scientificName = "Botryosphaeria obtusa",
            isHealthy = false,
            description = "Fungal disease causing fruit rot, leaf spots, and cankers on branches.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Prune cankers", "Cut branches 4 inches below visible cankers.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Remove mummified fruit", "Pick and destroy all infected fruits.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Apply fungicide", "Use captan or mancozeb-based fungicides.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        2 to DiseaseInfo(
            classIndex = 2,
            plantType = "Apple",
            diseaseName = "Cedar Apple Rust",
            scientificName = "Gymnosporangium juniperi-virginianae",
            isHealthy = false,
            description = "Fungal disease requiring both apple and cedar/juniper hosts.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove cedar hosts", "Remove nearby cedar/juniper trees if possible.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use myclobutanil or triadimefon sprays.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Plant resistant varieties", "Consider resistant apple varieties for future.", TreatmentStep.Priority.LOW)
            )
        ),
        3 to DiseaseInfo(
            classIndex = 3,
            plantType = "Apple",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Apple tree appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Continue maintenance", "Maintain regular watering and fertilization schedule.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Monitor regularly", "Check leaves periodically for early signs of disease.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Prune as needed", "Remove dead or damaged branches to maintain health.", TreatmentStep.Priority.LOW)
            )
        ),

        // Blueberry
        4 to DiseaseInfo(
            classIndex = 4,
            plantType = "Blueberry",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Blueberry plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Maintain soil pH", "Keep soil pH between 4.5-5.5 for optimal growth.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Mulch regularly", "Apply 2-3 inches of pine bark mulch.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Prune old canes", "Remove canes older than 6 years.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Cherry
        5 to DiseaseInfo(
            classIndex = 5,
            plantType = "Cherry",
            diseaseName = "Powdery Mildew",
            scientificName = "Podosphaera clandestina",
            isHealthy = false,
            description = "Fungal disease causing white powdery coating on leaves and fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Apply fungicide", "Use sulfur or potassium bicarbonate sprays.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Improve airflow", "Prune to increase air circulation.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Water at base", "Avoid overhead watering to reduce humidity.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        6 to DiseaseInfo(
            classIndex = 6,
            plantType = "Cherry",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Cherry tree appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Continue care", "Maintain regular pruning and fertilization.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Monitor for pests", "Check for aphids and other common cherry pests.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Harvest properly", "Pick fruit when ripe to prevent over-ripening on tree.", TreatmentStep.Priority.LOW)
            )
        ),

        // Corn
        7 to DiseaseInfo(
            classIndex = 7,
            plantType = "Corn",
            diseaseName = "Cercospora Leaf Spot",
            scientificName = "Cercospora zeae-maydis",
            isHealthy = false,
            description = "Fungal disease causing grayish lesions with dark borders on leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Rotate crops", "Avoid planting corn in the same field for 2+ years.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use azoxystrobin or pyraclostrobin if severe.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Tillage", "Bury crop residue to reduce fungal overwintering.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        8 to DiseaseInfo(
            classIndex = 8,
            plantType = "Corn",
            diseaseName = "Common Rust",
            scientificName = "Puccinia sorghi",
            isHealthy = false,
            description = "Fungal disease causing reddish-brown pustules on leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Plant resistant hybrids", "Use rust-resistant corn varieties.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use foliar fungicides if rust is severe.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Early planting", "Plant early to avoid peak rust season.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        9 to DiseaseInfo(
            classIndex = 9,
            plantType = "Corn",
            diseaseName = "Northern Leaf Blight",
            scientificName = "Exserohilum turcicum",
            isHealthy = false,
            description = "Fungal disease causing cigar-shaped lesions on leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Use resistant varieties", "Plant hybrids with Ht resistance genes.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Crop rotation", "Rotate with non-host crops for 1-2 years.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Fungicide application", "Apply if lesions appear before tasseling.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        10 to DiseaseInfo(
            classIndex = 10,
            plantType = "Corn",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Corn plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Maintain fertility", "Ensure adequate nitrogen and potassium.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Irrigation", "Water during critical growth stages.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Weed control", "Keep field weed-free during early growth.", TreatmentStep.Priority.LOW)
            )
        ),

        // Grape
        11 to DiseaseInfo(
            classIndex = 11,
            plantType = "Grape",
            diseaseName = "Black Rot",
            scientificName = "Guignardia bidwellii",
            isHealthy = false,
            description = "Fungal disease causing brown spots on leaves and shriveled black fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove mummies", "Pick and destroy all shriveled berries.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Prune infected parts", "Cut out diseased canes during dormancy.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Apply fungicide", "Use mancozeb or myclobutanil sprays.", TreatmentStep.Priority.HIGH)
            )
        ),
        12 to DiseaseInfo(
            classIndex = 12,
            plantType = "Grape",
            diseaseName = "Esca (Black Measles)",
            scientificName = "Phaeomoniella chlamydospora",
            isHealthy = false,
            description = "Fungal complex causing tiger-striped leaves and black spots on fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Prune during dry weather", "Make pruning cuts only when vines are dry.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Treat pruning wounds", "Apply wound dressing to cuts larger than 1 inch.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Remove dead wood", "Eliminate all dead and dying vines.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        13 to DiseaseInfo(
            classIndex = 13,
            plantType = "Grape",
            diseaseName = "Leaf Blight",
            scientificName = "Isariopsis clavispora",
            isHealthy = false,
            description = "Fungal disease causing reddish-brown spots with dark borders.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Apply fungicide", "Use copper-based fungicides in spring.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Improve ventilation", "Prune for better air circulation.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Remove infected leaves", "Clear fallen and infected leaves.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        14 to DiseaseInfo(
            classIndex = 14,
            plantType = "Grape",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Grape vine appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Regular pruning", "Maintain proper canopy management.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Disease prevention", "Apply preventive fungicide sprays.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Nutrient management", "Monitor and maintain proper soil fertility.", TreatmentStep.Priority.LOW)
            )
        ),

        // Orange/Citrus
        15 to DiseaseInfo(
            classIndex = 15,
            plantType = "Orange",
            diseaseName = "Citrus Greening (Huanglongbing)",
            scientificName = "Candidatus Liberibacter",
            isHealthy = false,
            description = "Bacterial disease spread by psyllids causing misshapen bitter fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected trees", "Destroy severely infected trees immediately.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Control psyllids", "Apply insecticides to control Asian citrus psyllid.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Plant certified trees", "Use only certified disease-free nursery stock.", TreatmentStep.Priority.HIGH)
            )
        ),

        // Peach
        16 to DiseaseInfo(
            classIndex = 16,
            plantType = "Peach",
            diseaseName = "Bacterial Spot",
            scientificName = "Xanthomonas campestris",
            isHealthy = false,
            description = "Bacterial disease causing dark spots on fruit and angular leaf lesions.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Plant resistant varieties", "Use tolerant cultivars like 'Redhaven'.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Copper sprays", "Apply copper bactericides during dormancy.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Avoid overhead irrigation", "Use drip irrigation to reduce spread.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        17 to DiseaseInfo(
            classIndex = 17,
            plantType = "Peach",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Peach tree appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Annual pruning", "Prune to maintain open center shape.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Thin fruit", "Remove excess fruit for better size and quality.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Fertilize properly", "Apply balanced fertilizer in early spring.", TreatmentStep.Priority.LOW)
            )
        ),

        // Pepper
        18 to DiseaseInfo(
            classIndex = 18,
            plantType = "Pepper",
            diseaseName = "Bacterial Spot",
            scientificName = "Xanthomonas campestris",
            isHealthy = false,
            description = "Bacterial disease causing water-soaked spots that turn brown with yellow halos.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Use certified seed", "Plant disease-free certified seed.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Copper sprays", "Apply copper-based bactericides weekly.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Avoid wetting foliage", "Use drip irrigation instead of overhead.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        19 to DiseaseInfo(
            classIndex = 19,
            plantType = "Pepper",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Pepper plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Consistent watering", "Maintain even soil moisture.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Fertilize appropriately", "Use phosphorus for fruit development.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Support plants", "Stake tall varieties to prevent breakage.", TreatmentStep.Priority.LOW)
            )
        ),

        // Potato
        20 to DiseaseInfo(
            classIndex = 20,
            plantType = "Potato",
            diseaseName = "Early Blight",
            scientificName = "Alternaria solani",
            isHealthy = false,
            description = "Fungal disease causing dark brown concentric rings on leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Apply fungicide", "Use chlorothalonil or mancozeb sprays.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Rotate crops", "Avoid planting tomatoes or potatoes for 3+ years.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Remove infected leaves", "Prune lower infected leaves.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        21 to DiseaseInfo(
            classIndex = 21,
            plantType = "Potato",
            diseaseName = "Late Blight",
            scientificName = "Phytophthora infestans",
            isHealthy = false,
            description = "Devastating fungal disease causing brown lesions and white fungal growth.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected plants", "Destroy all infected plants immediately.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use mancozeb or chlorothalonil preventively.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Hill potatoes", "Maintain proper hilling to protect tubers.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        22 to DiseaseInfo(
            classIndex = 22,
            plantType = "Potato",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Potato plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Hill regularly", "Build up soil around stems to protect tubers.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Monitor for pests", "Check for Colorado potato beetle.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Proper harvesting", "Harvest when vines have died back.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Raspberry
        23 to DiseaseInfo(
            classIndex = 23,
            plantType = "Raspberry",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Raspberry plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Prune annually", "Remove floricanes after fruiting.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Support canes", "Use trellis to keep canes upright.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Weed control", "Keep planting area weed-free.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Soybean
        24 to DiseaseInfo(
            classIndex = 24,
            plantType = "Soybean",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Soybean plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Inoculate seeds", "Use Rhizobium inoculant for nitrogen fixation.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Rotate crops", "Rotate with corn or small grains.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Weed management", "Control weeds during early growth.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Squash
        25 to DiseaseInfo(
            classIndex = 25,
            plantType = "Squash",
            diseaseName = "Powdery Mildew",
            scientificName = "Podosphaera xanthii",
            isHealthy = false,
            description = "Fungal disease causing white powdery coating on leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Apply fungicide", "Use sulfur or potassium bicarbonate.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Space plants", "Allow for good air circulation.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Water at base", "Avoid wetting leaves during irrigation.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Strawberry
        26 to DiseaseInfo(
            classIndex = 26,
            plantType = "Strawberry",
            diseaseName = "Leaf Scorch",
            scientificName = "Diplocarpon earliana",
            isHealthy = false,
            description = "Fungal disease causing purple spots that turn scorched brown.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected leaves", "Pick and destroy infected foliage.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Mulch properly", "Use plastic mulch to prevent soil splash.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Apply fungicide", "Use captan or thiram sprays.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        27 to DiseaseInfo(
            classIndex = 27,
            plantType = "Strawberry",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Strawberry plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Renovate beds", "Thin plants and remove old leaves after harvest.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Fertilize", "Apply nitrogen after renovation.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Winter protection", "Mulch heavily before winter.", TreatmentStep.Priority.MEDIUM)
            )
        ),

        // Tomato - classes 28-37
        28 to DiseaseInfo(
            classIndex = 28,
            plantType = "Tomato",
            diseaseName = "Bacterial Spot",
            scientificName = "Xanthomonas vesicatoria",
            isHealthy = false,
            description = "Bacterial disease causing water-soaked spots on leaves and fruit.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Use resistant varieties", "Plant tolerant cultivars.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Copper sprays", "Apply copper bactericides preventively.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Avoid overhead water", "Use drip irrigation only.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        29 to DiseaseInfo(
            classIndex = 29,
            plantType = "Tomato",
            diseaseName = "Early Blight",
            scientificName = "Alternaria solani",
            isHealthy = false,
            description = "Fungal disease causing dark concentric rings on lower leaves first.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected leaves", "Prune and destroy infected lower leaves.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use chlorothalonil or copper sprays.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Mulch", "Apply mulch to prevent soil splash.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        30 to DiseaseInfo(
            classIndex = 30,
            plantType = "Tomato",
            diseaseName = "Late Blight",
            scientificName = "Phytophthora infestans",
            isHealthy = false,
            description = "Destructive fungal disease causing brown lesions with white fungal growth.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected plants", "Destroy all infected tissue immediately.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use mancozeb or chlorothalonil urgently.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Space plants", "Improve air circulation around plants.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        31 to DiseaseInfo(
            classIndex = 31,
            plantType = "Tomato",
            diseaseName = "Leaf Mold",
            scientificName = "Passalora fulva",
            isHealthy = false,
            description = "Fungal disease causing yellow spots on upper leaves, mold underneath.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Improve ventilation", "Increase air circulation in greenhouse.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Reduce humidity", "Avoid overhead watering.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Apply fungicide", "Use chlorothalonil sprays.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        32 to DiseaseInfo(
            classIndex = 32,
            plantType = "Tomato",
            diseaseName = "Septoria Leaf Spot",
            scientificName = "Septoria lycopersici",
            isHealthy = false,
            description = "Fungal disease causing small circular spots with dark borders.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected leaves", "Prune and destroy infected foliage.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Mulch", "Prevent soil splash with organic mulch.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Rotate crops", "Avoid planting tomatoes in same spot.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        33 to DiseaseInfo(
            classIndex = 33,
            plantType = "Tomato",
            diseaseName = "Spider Mites (Two-spotted)",
            scientificName = "Tetranychus urticae",
            isHealthy = false,
            description = "Pest causing stippled leaves, webbing, and plant stress.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Spray water", "Use strong water spray to dislodge mites.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply miticide", "Use insecticidal soap or neem oil.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Increase humidity", "Mites prefer dry conditions.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        34 to DiseaseInfo(
            classIndex = 34,
            plantType = "Tomato",
            diseaseName = "Target Spot",
            scientificName = "Corynespora cassiicola",
            isHealthy = false,
            description = "Fungal disease causing circular brown lesions with concentric rings.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected leaves", "Clear infected foliage promptly.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Apply fungicide", "Use chlorothalonil or mancozeb.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Space plants", "Improve air circulation.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        35 to DiseaseInfo(
            classIndex = 35,
            plantType = "Tomato",
            diseaseName = "Yellow Leaf Curl Virus",
            scientificName = "Tomato Yellow Leaf Curl Virus",
            isHealthy = false,
            description = "Viral disease spread by whiteflies causing stunted, curled leaves.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected plants", "Destroy infected plants immediately.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Control whiteflies", "Use insecticides or reflective mulch.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Use resistant varieties", "Plant TYLCV-resistant cultivars.", TreatmentStep.Priority.HIGH)
            )
        ),
        36 to DiseaseInfo(
            classIndex = 36,
            plantType = "Tomato",
            diseaseName = "Mosaic Virus",
            scientificName = "Tobacco Mosaic Virus",
            isHealthy = false,
            description = "Viral disease causing mottled light and dark green leaf patterns.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Remove infected plants", "Destroy all infected plants immediately.", TreatmentStep.Priority.HIGH),
                TreatmentStep(2, "Sanitize tools", "Disinfect pruning tools with bleach.", TreatmentStep.Priority.HIGH),
                TreatmentStep(3, "Wash hands", "Tobacco users should wash hands before handling.", TreatmentStep.Priority.MEDIUM)
            )
        ),
        37 to DiseaseInfo(
            classIndex = 37,
            plantType = "Tomato",
            diseaseName = "Healthy",
            scientificName = null,
            isHealthy = true,
            description = "Tomato plant appears healthy with no visible disease symptoms.",
            treatmentSteps = listOf(
                TreatmentStep(1, "Regular care", "Maintain consistent watering and fertilizing.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(2, "Prune suckers", "Remove suckers for better fruit production.", TreatmentStep.Priority.MEDIUM),
                TreatmentStep(3, "Monitor", "Check regularly for early signs of disease.", TreatmentStep.Priority.MEDIUM)
            )
        )
    )

    /**
     * Get disease information for a given class index.
     */
    fun getDiseaseInfo(classIndex: Int): DiseaseInfo? {
        return diseaseDatabase[classIndex]
    }

    /**
     * Get all disease names for the labels file.
     */
    fun getAllDiseaseNames(): List<String> {
        return diseaseDatabase.values.sortedBy { it.classIndex }.map { it.diseaseName }
    }

    /**
     * Get confidence level based on prediction score.
     */
    fun getConfidenceLevel(confidence: Float): ClassificationResult.ConfidenceLevel {
        return when {
            confidence > 0.85f -> ClassificationResult.ConfidenceLevel.HIGH
            confidence > 0.60f -> ClassificationResult.ConfidenceLevel.MEDIUM
            else -> ClassificationResult.ConfidenceLevel.LOW
        }
    }
}
