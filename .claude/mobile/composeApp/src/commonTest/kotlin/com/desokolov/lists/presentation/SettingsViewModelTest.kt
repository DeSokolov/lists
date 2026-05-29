package com.desokolov.lists.presentation

import app.cash.turbine.test
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.presentation.settings.SettingsViewModel
import com.desokolov.lists.ui.theme.ThemeType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsViewModelTest {

    private val fakeThemeRepo = object : ThemeRepository {
        private var stored = ThemeType.WHITE
        override fun getTheme() = stored
        override fun saveTheme(t: ThemeType) { stored = t }
    }

    private val fakeNameRepo = object : NameRepository {
        private var stored = "Денис"
        override fun getName() = stored
        override fun saveName(n: String) { stored = n }
    }

    private val fakeNotifRepo = object : NotificationsRepository {
        private var stored = true
        override fun isEnabled() = stored
        override fun setEnabled(value: Boolean) { stored = value }
    }

    private val viewModel = SettingsViewModel(fakeThemeRepo, fakeNameRepo, fakeNotifRepo)

    @Test
    fun `initial state loads from repositories`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(ThemeType.WHITE, state.theme)
            assertEquals("Денис", state.name)
            assertTrue(state.notificationsEnabled)
        }
    }

    @Test
    fun `setTheme updates uiState and persists to repository`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setTheme(ThemeType.BLACK)
            val state = awaitItem()
            assertEquals(ThemeType.BLACK, state.theme)
            assertEquals(ThemeType.BLACK, fakeThemeRepo.getTheme())
        }
    }

    @Test
    fun `setName updates uiState but does not persist until saveName`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setName("Маша")
            val state = awaitItem()
            assertEquals("Маша", state.name)
            assertEquals("Денис", fakeNameRepo.getName()) // ещё не сохранено
        }
    }

    @Test
    fun `saveName persists name to repository`() = runTest {
        viewModel.setName("Маша")
        viewModel.saveName()
        assertEquals("Маша", fakeNameRepo.getName())
    }

    @Test
    fun `setNotifications updates uiState and persists`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setNotifications(false)
            val state = awaitItem()
            assertFalse(state.notificationsEnabled)
            assertFalse(fakeNotifRepo.isEnabled())
        }
    }
}
