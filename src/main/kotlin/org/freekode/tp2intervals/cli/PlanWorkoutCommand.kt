package org.freekode.tp2intervals.cli

import org.apache.commons.cli.Option
import org.freekode.tp2intervals.app.MainService
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PlanWorkoutCommand(val mainService: MainService) : CLICommand {
    override fun option(): Option = Option.builder().longOpt("plan-workout").build()

    override fun run(params: List<String>) {
        mainService.planTodayAndTomorrowWorkouts()
    }
}