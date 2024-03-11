package org.freekode.tp2intervals.app.workout

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.plan.PlanRepository
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutRepository
import org.springframework.stereotype.Service

@Service
class WorkoutService(
    workoutRepositories: List<WorkoutRepository>,
    planRepositories: List<PlanRepository>,
) {
    private val workoutRepositoryMap = workoutRepositories.associateBy { it.platform() }
    private val planRepositoryMap = planRepositories.associateBy { it.platform() }

    fun copyPlannedWorkouts(request: ScheduleWorkoutsRequest): ScheduleWorkoutsResponse {
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!

        val allWorkoutsToPlan = sourceWorkoutRepository.getScheduledWorkouts(request.startDate, request.endDate)
        var filteredWorkoutsToPlan = allWorkoutsToPlan
            .filter { request.types.contains(it.type) }
        if (request.skipSynced) {
            val plannedWorkouts = targetWorkoutRepository.getScheduledWorkouts(request.startDate, request.endDate)
                .filter { request.types.contains(it.type) }

            filteredWorkoutsToPlan = filteredWorkoutsToPlan
                .filter { !plannedWorkouts.contains(it) }
        }

        val response = ScheduleWorkoutsResponse(
            filteredWorkoutsToPlan.size,
            allWorkoutsToPlan.size - filteredWorkoutsToPlan.size,
            request.startDate,
            request.endDate
        )
        filteredWorkoutsToPlan.forEach { targetWorkoutRepository.scheduleWorkout(it) }
        return response
    }

    fun copyPlannedWorkouts(request: CopyWorkoutsRequest): CopyWorkoutsResponse {
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!
        val targetPlanRepository = planRepositoryMap[request.targetPlatform]!!

        val allWorkouts = sourceWorkoutRepository.getScheduledWorkouts(request.startDate, request.endDate)
        val filteredWorkouts = allWorkouts.filter { request.types.contains(it.type) }

        val plan = targetPlanRepository.createPlan(request.name, request.startDate, request.isPlan)
        filteredWorkouts.forEach { targetWorkoutRepository.saveWorkoutToPlan(it, plan) }
        return CopyWorkoutsResponse(
            filteredWorkouts.size, allWorkouts.size - filteredWorkouts.size, request.startDate, request.endDate
        )
    }

    fun findWorkoutsByName(platform: Platform, name: String): List<Workout> {
        val workoutRepository = workoutRepositoryMap[platform]!!
        return workoutRepository.findWorkoutsByName(name)
    }
}
