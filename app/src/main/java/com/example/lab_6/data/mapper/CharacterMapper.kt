package com.example.lab_6.data.mapper

import com.example.lab_6.data.Character
import com.example.lab_6.data.local.entity.CharacterEntity

fun CharacterEntity.toModel(): Character {
    return Character(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
        isFavorite = this.isFavorite
    )
}

fun Character.toEntity(isFavorite: Boolean = false): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
        isFavorite = isFavorite
    )
}