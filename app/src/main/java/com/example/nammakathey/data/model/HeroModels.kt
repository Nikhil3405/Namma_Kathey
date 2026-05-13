package com.example.nammakathey.data.model

data class Root(
    val districts: List<District>
)

data class District(
    val id: String,
    val name_en: String,
    val name_kn: String,
    val heroes: List<Hero>
)

data class Hero(
    val id: String,
    val name_en: String,
    val name_kn: String,
    val short_desc_en: String,
    val short_desc_kn: String,
    val story: Story,
    val quiz: List<Quiz>,
    val location: Location,
    val image: String
)

data class Story(
    val en: List<String>,
    val kn: List<String>
)

data class Quiz(
    val question_en: String,
    val question_kn: String,
    val options_en: List<String>,
    val options_kn: List<String>,
    val answer: Int
)

data class Location(
    val name: String,
    val lat: Double,
    val lng: Double
)