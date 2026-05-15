# 📖 Namma-Kathey

### Regional Hero Storytelling App 🇮🇳

---

## 🚀 Overview

**Namma-Kathey** is an Android application built using **Kotlin and Jetpack Compose** that introduces children to local heroes, freedom fighters, and cultural icons from various districts of Karnataka.

The app delivers a **storybook-like reading experience**, combined with **bilingual support, narration, quizzes, and map-based exploration**, making learning interactive and engaging.

---

## 🎯 Features

* 🗺️ **Interactive District Map**

  * Karnataka SVG map using WebView
  * Clickable districts to explore heroes

* 📚 **Storybook Experience**

  * Swipe-based pages (ViewPager)
  * Kindle-style immersive reading UI

* 🌐 **Bilingual Support**

  * English & Kannada toggle
  * Dynamic content switching

* 🔊 **Text-to-Speech (TTS)**

  * Narration for stories
  * Supports Kannada & English

* 🧠 **Quiz System**

  * 3-question quiz per hero
  * Score-based completion

* 🏆 **Progress Tracking**

  * Hero-level completion tracking
  * District marked complete only when all heroes are completed

* 📍 **Memorial Navigation**

  * Opens Google Maps for hero locations

---

## 🛠️ Tech Stack

| Category   | Technology                |
| ---------- | ------------------------- |
| Language   | Kotlin                    |
| UI         | Jetpack Compose           |
| Navigation | Jetpack Navigation        |
| State      | ViewModel + Compose State |
| Data       | Local JSON (assets)       |
| Map        | WebView + SVG             |
| TTS        | Android TextToSpeech      |

---

## 📁 Project Structure

```text
app/
├── manifests/

├── kotlin+java/com.example.nammakathey/
│
│   ├── data/
│   │   ├── local/
│   │   │   ├── BadgeStore.kt
│   │   │   └── JsonLoader.kt
│   │   │
│   │   └── model/
│   │       └── HeroModels.kt
│
│   ├── navigation/
│   │   ├── AppNavGraph.kt
│   │   ├── BottomBar.kt
│   │   └── MainScreen.kt
│
│   ├── ui/
│   │   ├── components/
│   │   │   └── DistrictBadge.kt
│   │   │
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── DistrictScreen.kt
│   │   │   ├── HeroListScreen.kt
│   │   │   ├── StoryScreen.kt
│   │   │   ├── QuizScreen.kt
│   │   │   └── ProfileScreen.kt
│   │   │
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│
│   ├── viewmodel/
│   │   └── AppViewModel.kt
│
│   └── MainActivity.kt
│
├── assets/
│   ├── heroes.json
│   ├── index.html
│   └── karnataka_map.svg
│
├── res/
│   ├── drawable/   (hero images + icons)
│   ├── mipmap/
│   ├── values/
│   └── xml/
```

---

## 📱 App Workflow

```text
Home Screen
   ↓
District Map (SVG)
   ↓
Hero List
   ↓
Story Screen (Swipe + TTS)
   ↓
Quiz Screen
   ↓
Completion & Progress Tracking
```

---

## 📦 Data Handling

All content is stored locally in:

```text
assets/heroes.json
```

Includes:

* Districts
* Heroes
* Stories (EN + KN)
* Quiz data
* Location coordinates

---

## 🎨 UI Highlights

* Material 3 design
* Gradient headers
* Kindle-style reading screen
* Card-based hero UI
* Child-friendly visuals
* Responsive layouts

---

## 🧠 Key Concepts Implemented

* MVVM Architecture
* State Management in Compose
* Dynamic Navigation with arguments
* WebView + JavaScript bridge
* JSON parsing & local data storage
* Text-to-Speech integration
* UI/UX design for children

---

## 🎯 Impact

* 🇮🇳 Promotes local heritage awareness
* 📚 Encourages reading habits
* 🧠 Supports value-based learning
* 🌍 Builds connection with regional history

---

## 🔮 Future Enhancements

* 🌙 Dark mode (night reading)
* 🎧 Background audio narration
* 🏅 Advanced badge system
* ☁️ Cloud sync for progress
* 📊 Analytics dashboard

---

## 👨‍💻 Author

**Nikhil N Achar**
USN: 1DA22AI021
AI & ML Department
Dr. Ambedkar Institute of Technology

---

## 🙏 Acknowledgements

* MindMatrix (Internship Organization)
* Faculty Mentors & Guides
* GenAI tools for content generation

---

## ⭐ Support

If you like this project:

⭐ Star the repository
📢 Share feedback
🚀 Suggest improvements

---
