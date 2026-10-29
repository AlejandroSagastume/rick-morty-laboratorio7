package com.example.lab_6.data.mapper

import com.example.lab_6.data.Location
import com.example.lab_6.data.local.entity.LocationEntity

fun LocationEntity.toModel(): Location {
    return Location(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension
    )
}

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension
    )
}