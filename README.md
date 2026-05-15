# 📖 Namma-Kathey

### Regional Hero Storytelling App 🇮🇳

---

## 🚀 Overview

**Namma-Kathey** is an Android application designed to introduce children to local heroes, freedom fighters, poets, and cultural icons from different districts of Karnataka.

The app combines **interactive storytelling, bilingual support, and quizzes** to make learning history engaging and meaningful.

---

## 🎯 Features

* 🗺️ **District Map Exploration**

  * Interactive Karnataka map
  * Tap a district to view its heroes

* 📚 **Storybook Experience**

  * Swipe-based story pages
  * Clean, distraction-free reading (Kindle-style UI)

* 🌐 **Bilingual Support**

  * English 🇬🇧 and Kannada 🇮🇳 toggle
  * Content available in both languages

* 🔊 **Text-to-Speech Narration**

  * Listen to stories
  * Supports Kannada and English

* 🧠 **Quiz System**

  * 3-question quiz per hero
  * Reinforces learning

* 🏆 **Progress Tracking**

  * Hero completion tracking
  * District completion (all heroes done → district marked complete)

* 📍 **Memorial Locator**

  * Open Google Maps to visit hero locations

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Navigation:** Jetpack Navigation
* **State Management:** ViewModel + State
* **Data Source:** Local JSON
* **Media:** Android TextToSpeech (TTS)
* **Map Integration:** WebView + SVG (Karnataka Map)

---

## 🧱 Project Structure

```text
app/
├── data/
│   ├── model/        # Data models (Hero, Quiz, District)
│   ├── local/        # JSON loader
│
├── ui/
│   ├── screens/
│   │   ├── HomeScreen
│   │   ├── DistrictScreen
│   │   ├── HeroListScreen
│   │   ├── StoryScreen
│   │   ├── QuizScreen
│   │   └── ProfileScreen
│
├── navigation/
│   ├── AppNavGraph
│   └── BottomBar
│
├── viewmodel/
│   └── AppViewModel
```

---

## 📱 App Flow

```text
Home Screen
   ↓
District Map
   ↓
Hero List
   ↓
Story Screen
   ↓
Quiz
   ↓
Completion & Progress Tracking
```

---

## 🎨 UI Highlights

* Modern **Material 3 UI**
* Gradient headers and soft color palette
* Kindle-style reading experience
* Card-based hero discovery UI
* Child-friendly design

---

## 📦 Data Format

Data is stored locally in JSON:

```json
{
  "districts": [
    {
      "name_en": "Bengaluru Urban",
      "heroes": [
        {
          "id": "kempe_gowda",
          "name_en": "Kempe Gowda",
          "story": {
            "en": ["Page 1", "Page 2"],
            "kn": ["Page 1", "Page 2"]
          },
          "quiz": [...],
          "location": {...}
        }
      ]
    }
  ]
}
```

---

## 🎯 Impact

* 🇮🇳 Promotes **local heritage awareness**
* 📚 Encourages **reading habits**
* 🧠 Builds **value-based learning**
* 🌍 Connects children to **regional history**

---

## 🔮 Future Enhancements

* 🎧 Background music & narration improvements
* 🌙 Dark mode (night reading)
* 🏅 Badge & achievements system
* ☁️ Cloud sync for progress
* 📊 Analytics dashboard

---

## 👨‍💻 Author

**Nikhil N Achar**
AI & ML Student
Dr. Ambedkar Institute of Technology

---

## 🙏 Acknowledgements

* MindMatrix (Internship Organization)
* Faculty Guides & Mentors
* OpenAI / GenAI tools for content generation

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and share your feedback!

---
