package org.freekode.tp2intervals.infrastructure.thirdparty.workout

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutStep
import org.freekode.tp2intervals.domain.workout.WorkoutStepTarget
import org.springframework.stereotype.Repository

@Repository
class WorkoutStepToThirdPartyStructureMapper(
    private val objectMapper: ObjectMapper,
) {

    fun mapToWorkoutStructureStr(workout: Workout): String? {
        if (workout.steps.isEmpty()) {
            return null
        }
        val structure = mapToWorkoutStructure(workout.steps)
        return objectMapper.copy()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writeValueAsString(structure)
    }

    private fun mapToWorkoutStructure(steps: List<WorkoutStep>): ThirdPartyWorkoutStructureDTO {
        val stepDTOs = steps.map { mapToStructureStep(it) }

        return ThirdPartyWorkoutStructureDTO(
            stepDTOs,
            "duration",
            "percentOfFtp",
            "range",
        )
    }

    private fun mapToStructureStep(workoutStep: WorkoutStep): ThirdPartyWorkoutStructureDTO.StructureStepDTO {
        if (workoutStep.isSingleStep()) {
            val singleStepDTO = mapToStepDTO(workoutStep)
            return ThirdPartyWorkoutStructureDTO.StructureStepDTO.singleStep(singleStepDTO)
        } else {
            val stepDTOs = workoutStep.steps.map { mapToStepDTO(it) }
            return ThirdPartyWorkoutStructureDTO.StructureStepDTO.multiStep(workoutStep.repetitions, stepDTOs)
        }
    }

    private fun mapToStepDTO(workoutStep: WorkoutStep) =
        ThirdPartyWorkoutStructureDTO.StepDTO(
            workoutStep.title,
            ThirdPartyWorkoutStructureDTO.LengthDTO.seconds(workoutStep.duration.seconds),
            workoutStep.targets.map { mapToTargetDTO(it) },
            ThirdPartyWorkoutStructureDTO.IntensityClass.findByIntensityType(workoutStep.intensity),
            null
        )

    private fun mapToTargetDTO(workoutStepTarget: WorkoutStepTarget) =
        when (workoutStepTarget.type) {
            WorkoutStepTarget.TargetType.POWER -> ThirdPartyWorkoutStructureDTO.TargetDTO.powerTarget(
                workoutStepTarget.min, workoutStepTarget.max,
            )

            WorkoutStepTarget.TargetType.CADENCE -> ThirdPartyWorkoutStructureDTO.TargetDTO.cadenceTarget(
                workoutStepTarget.min, workoutStepTarget.max,
            )
        }

}
