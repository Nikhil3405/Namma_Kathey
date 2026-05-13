package com.example.nammakathey.data.local

import android.content.Context
import com.example.nammakathey.data.model.Root
import com.google.gson.Gson

fun loadHeroes(context: Context): Root {
    val json = context.assets.open("heroes.json")
        .bufferedReader()
        .use { it.readText() }

    return Gson().fromJson(json, Root::class.java)
}