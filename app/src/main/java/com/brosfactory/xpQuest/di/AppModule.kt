package com.brosfactory.xpQuest.di

import com.brosfactory.xpQuest.data.local.UserDao
import com.brosfactory.xpQuest.data.repository.FirebaseAuthRepositoryImpl
import com.brosfactory.xpQuest.data.repository.UserRepositoryImpl
import com.brosfactory.xpQuest.domain.repository.AuthRepository
import com.brosfactory.xpQuest.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return FirebaseAuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(firestore, userDao)
    }
}