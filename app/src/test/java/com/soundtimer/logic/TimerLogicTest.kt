package com.soundtimer.logic

import com.soundtimer.data.TimerState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerLogicTest {

    @Test
    fun `test TimerState IDLE`() {
        val state = TimerState.IDLE
        assertFalse("IDLE state should not be running", state.isRunning)
        assertTrue("IDLE state should have empty muted categories", state.mutedCategories.isEmpty())
    }

    @Test
    fun `test TimerState isExpired logic`() {
        // Test expired state
        // We set endTime to 0, which is definitely in the past
        val expiredState = TimerState(
            isRunning = true,
            endTimeMillis = 0L
        )
        // Since endTime is 0 (1970), and now is 2026, it should be expired
        assertTrue("State with 0 endTime should be expired", expiredState.isExpired)

        // Test active state
        // We set endTime to significantly in the future (e.g., current time + 1 hour)
        // Since we can't easily mock Clock.System.now() without refactoring, 
        // we use a safe future timestamp check assuming system clock is reasonable.
        val futureTime = System.currentTimeMillis() + 3600000L // +1 hour
        val activeState = TimerState(
            isRunning = true,
            endTimeMillis = futureTime
        )
        assertFalse("State with future endTime should not be expired", activeState.isExpired)
        
        // Test not running state
        val notRunningState = TimerState(
            isRunning = false,
            endTimeMillis = 0L
        )
        assertFalse("State not running should not be expired even if time passed", notRunningState.isExpired)
    }
}
