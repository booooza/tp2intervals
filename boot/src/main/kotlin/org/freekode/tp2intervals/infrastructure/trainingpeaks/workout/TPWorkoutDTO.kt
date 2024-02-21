package org.freekode.tp2intervals.infrastructure.trainingpeaks.workout

import org.freekode.tp2intervals.infrastructure.trainingpeaks.workout.structure.TPWorkoutStructureDTO
import java.time.LocalDateTime
import org.freekode.tp2intervals.domain.TrainingType

class TPWorkoutDTO(
    val workoutId: String,
    val workoutDay: LocalDateTime,
    val workoutTypeValueId: Int?,
    val title: String,
    val totalTimePlanned: Double?,
    val tssPlanned: Double?,
    val description: String?,
    val coachComments: String?,
    val structure: TPWorkoutStructureDTO?
) {
    fun getWorkoutType(): TrainingType? = workoutTypeValueId?.let { TPWorkoutTypeMapper.getByValue(it) }
}
