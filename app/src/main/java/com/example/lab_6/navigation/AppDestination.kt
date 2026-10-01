package com.example.lab_6.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Characters

@Serializable
object Main

@Serializable
object CharactersGraph

@Serializable
object CharactersList

@Serializable
data class CharacterDetails(val id: Int)

@Serializable
object LocationsGraph

@Serializable
object LocationsList

@Serializable
data class LocationDetails(val id: Int)

@Serializable
object Profile