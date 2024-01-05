package org.freekode.tp2intervals.rest

import org.freekode.tp2intervals.app.ConfigService
import org.freekode.tp2intervals.domain.config.AppConfig
import org.freekode.tp2intervals.domain.config.IntervalsConfig
import org.freekode.tp2intervals.domain.config.TrainingPeaksConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigController(
    private val configService: ConfigService
) {

    @GetMapping("test-connections")
    fun testConnections() = configService.testConnections()

    @GetMapping("config")
    fun getConfig(): AppConfigDTO {
        val config = configService.getConfig()
        return AppConfigDTO(
            config.tpConfig.authCookie,
            config.intervalsConfig.password,
            config.intervalsConfig.athleteId
        )
    }

    @PutMapping("config")
    fun updateConfig(appConfigDTO: AppConfigDTO) {
        val appConfig = AppConfig(
            TrainingPeaksConfig(appConfigDTO.tpAuthCookie),
            IntervalsConfig(appConfigDTO.intervalsLogin, appConfigDTO.intervalsAthleteId)
        )
        configService.updateConfig(appConfig)
    }

}
