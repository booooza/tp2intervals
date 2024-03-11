package org.freekode.tp2intervals.infrastructure.platform.trainerroad.workout

import java.time.LocalDate
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.plan.Plan
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutRepository
import org.freekode.tp2intervals.infrastructure.PlatformException
import org.freekode.tp2intervals.infrastructure.platform.trainerroad.TrainerRoadApiClient
import org.springframework.stereotype.Repository


@Repository
class TrainerRoadWorkoutRepository(
    private val trainerRoadApiClient: TrainerRoadApiClient,
) : WorkoutRepository {
    override fun platform() = Platform.TRAINER_ROAD

    override fun scheduleWorkout(workout: Workout) {
        throw PlatformException(Platform.TRAINER_ROAD, "TR doesn't support workout planning")
    }

    override fun saveWorkoutToPlan(workout: Workout, plan: Plan) {
        throw PlatformException(Platform.TRAINER_ROAD, "TR doesn't support workout copying")
    }

    override fun findWorkoutsByName(name: String): List<Workout> {
        TODO("Not yet implemented")
    }

    override fun getScheduledWorkouts(startDate: LocalDate, endDate: LocalDate): List<Workout> {
        TODO("Not yet implemented")
    }

    override fun getWorkouts(plan: Plan): List<Workout> {
        TODO("Not yet implemented")
    }

    fun getWorkout(id: String): Workout {
        val workoutDetails = trainerRoadApiClient.getWorkoutDetails(id)
        return TRWorkoutConverter(workoutDetails).toWorkout()
    }
}
