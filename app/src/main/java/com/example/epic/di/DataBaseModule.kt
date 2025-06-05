package com.example.epic.di

import com.example.epic.db.AppDatabase
import org.koin.dsl.module

val dataBaseModule = module {
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().hostsSourceDao() }
    single { get<AppDatabase>().hostEntryDao() }
    single { get<AppDatabase>().hostsListItemDao() }
}