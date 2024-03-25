package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.plan

import java.time.LocalDate
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainerRepository
import org.freekode.tp2intervals.infrastructure.PlatformException
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.library.TPWorkoutLibraryRepository
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@CacheConfig(cacheNames = ["libraryItemsCache"])
@Repository
class TPPlanContainerRepository(
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
    private val tpWorkoutLibraryRepository: TPWorkoutLibraryRepository,
    private val trainingPeaksPlanApiClient: TrainingPeaksPlanApiClient,
) : LibraryContainerRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    override fun createLibraryContainer(name: String, startDate: LocalDate?, isPlan: Boolean): LibraryContainer {
        throw PlatformException(platform(), "Doesn't support plan creation")
    }

    @Cacheable(key = "'TRAINING_PEAKS'")
    override fun getLibraryContainers(): List<LibraryContainer> {
        val plans = trainingPeaksPlanApiClient.getPlans()
            .map { toLibraryContainer(it) }
            .sortedBy { it.name }
        val libraries = tpWorkoutLibraryRepository.getLibraries()
            .sortedBy { it.name }
        return (plans + libraries)
            .toList()
    }

    fun getPlan(planId: String): TPPlanDto {
        return trainingPeaksPlanApiClient.getPlan(planId)
    }

    fun applyPlan(planId: String, startDate: LocalDate): ApplyTPPlanResponseDTO {
        val request = ApplyTPPlanRequestDTO(
            trainingPeaksUserRepository.getUserId(),
            planId,
            startDate.toString(),
            "1"
        )
        return trainingPeaksPlanApiClient.applyPlan(listOf(request)).first()
    }

    fun removeAppliedPlan(appliedPlanId: String) {
        val request = mapOf(
            "appliedPlanId" to appliedPlanId
        )
        trainingPeaksPlanApiClient.removePlan(request)
    }

    private fun toLibraryContainer(planDto: TPPlanDto): LibraryContainer {
        return LibraryContainer.planFromMonday(
            planDto.title,
            planDto.workoutCount,
            ExternalData.empty().withTrainingPeaks(planDto.planId)
        )
    }
}
