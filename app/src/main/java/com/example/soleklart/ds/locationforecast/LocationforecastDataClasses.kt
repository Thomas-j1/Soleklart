@file:Suppress("ClassName")

package com.example.soleklart.ds.locationforecast


data class Locationforecast(val type: String, val geometry: Geometry, val properties: Properties)

data class Geometry(val type: String, val coordinates: List<Number>)

data class Properties(val meta: Meta, val timeseries: List<Timeseries>)

data class Meta(val updated_at: String, val units: Units)

data class Units(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val cloud_area_fraction: String,
    val precipitation_amount: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String
)

data class Timeseries(val time: String, val data: Data)

data class Data(
    val instant: Instant,
    val next_12_hours: Next_12_hours,
    val next_1_hours: Next_1_hours,
    val next_6_hours: Next_6_hours?
)

data class Instant(val details: Details)

data class Details(
    val air_pressure_at_sea_level: Number?,
    val air_temperature: Number?,
    val cloud_area_fraction: Number?,
    val precipitation_amount: Number?,
    val relative_humidity: Number?,
    val wind_from_direction: Number?,
    val wind_speed: Number?
)

data class Next_1_hours(val summary: Summary, val details: Details)

data class Next_6_hours(val summary: Summary, val details: Details)

data class Next_12_hours(val summary: Summary)

data class Summary(val symbol_code: String)

/**
 * dataClass containing main data from Locationforecast API
 */
data class ForecastData(
    val time: String, val temperature: String, val cloudCover: String,
    val wind_speed: String, val precipitation6Hours: String
)



