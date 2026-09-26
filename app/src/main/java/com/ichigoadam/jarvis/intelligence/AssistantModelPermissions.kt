package com.ichigoadam.jarvis.intelligence

enum class ModelPermission {
    BASIC_COMMANDS,
    APP_CONTROL,
    MEMORY,
    ANALYSIS,
    SYSTEM_STATUS,
    OWNER_COMMANDS
}

class AssistantModelPermissions {
    fun permissions(model: AssistantModel): Set<ModelPermission> =
        when (model) {
            AssistantModel.JARVIS -> setOf(
                ModelPermission.BASIC_COMMANDS,
                ModelPermission.APP_CONTROL,
                ModelPermission.MEMORY,
                ModelPermission.ANALYSIS,
                ModelPermission.SYSTEM_STATUS,
                ModelPermission.OWNER_COMMANDS
            )
            AssistantModel.FRIDAY -> setOf(
                ModelPermission.ANALYSIS,
                ModelPermission.SYSTEM_STATUS
            )
        }
}
