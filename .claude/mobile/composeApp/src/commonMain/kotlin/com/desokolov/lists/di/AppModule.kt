package com.desokolov.lists.di

import com.desokolov.lists.data.repository.FirebaseListRepository
import com.desokolov.lists.data.repository.FirebaseUserRepository
import com.desokolov.lists.data.repository.SettingsNameRepository
import com.desokolov.lists.data.repository.SettingsNotificationsRepository
import com.desokolov.lists.data.repository.SettingsThemeRepository
import com.desokolov.lists.data.repository.StubListRepository
import com.desokolov.lists.data.repository.StubUserRepository
import com.desokolov.lists.domain.repository.ListRepository
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.domain.repository.UserRepository
import com.desokolov.lists.domain.usecase.AddItemUseCase
import com.desokolov.lists.domain.usecase.CheckItemUseCase
import com.desokolov.lists.domain.usecase.ClaimItemUseCase
import com.desokolov.lists.domain.usecase.CreateListUseCase
import com.desokolov.lists.domain.usecase.DeleteItemUseCase
import com.desokolov.lists.domain.usecase.GetItemsUseCase
import com.desokolov.lists.domain.usecase.GetListsUseCase
import com.desokolov.lists.domain.usecase.UnclaimItemUseCase
import com.desokolov.lists.presentation.auth.AuthViewModel
import com.desokolov.lists.presentation.detail.ListDetailViewModel
import com.desokolov.lists.presentation.lists.ListsViewModel
import com.desokolov.lists.presentation.settings.SettingsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// Switch to false once GoogleService-Info.plist is added to the iOS project
const val USE_STUBS = true

val dataModule = module {
    if (USE_STUBS) {
        singleOf(::StubListRepository) bind ListRepository::class
        singleOf(::StubUserRepository) bind UserRepository::class
    } else {
        single { Firebase.firestore }
        single { Firebase.auth }
        singleOf(::FirebaseListRepository) bind ListRepository::class
        singleOf(::FirebaseUserRepository) bind UserRepository::class
    }
}

val domainModule = module {
    factoryOf(::GetListsUseCase)
    factoryOf(::GetItemsUseCase)
    factoryOf(::CreateListUseCase)
    factoryOf(::AddItemUseCase)
    factoryOf(::ClaimItemUseCase)
    factoryOf(::UnclaimItemUseCase)
    factoryOf(::CheckItemUseCase)
    factoryOf(::DeleteItemUseCase)
}

val presentationModule = module {
    factory { (userId: String) ->
        ListsViewModel(
            getLists = get(),
            createList = get(),
            userId = userId
        )
    }
    factory { (listId: String, userId: String, userName: String) ->
        ListDetailViewModel(
            listId = listId,
            currentUserId = userId,
            currentUserName = userName,
            getItems = get(),
            addItem = get(),
            claimItem = get(),
            unclaimItem = get(),
            checkItem = get(),
            deleteItem = get()
        )
    }
    factoryOf(::AuthViewModel)
}

val settingsModule = module {
    singleOf(::SettingsThemeRepository) bind ThemeRepository::class
    singleOf(::SettingsNameRepository) bind NameRepository::class
    singleOf(::SettingsNotificationsRepository) bind NotificationsRepository::class
    singleOf(::SettingsViewModel)
}

val appModules = listOf(platformSettingsModule, dataModule, domainModule, presentationModule, settingsModule)
