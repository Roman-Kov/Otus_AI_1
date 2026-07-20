# Мини-анкета — Design Spec

## Цель
Простое приложение «Мини-анкета»: backend на Ktor + Android-клиент на Jetpack Compose.

## Структура проекта

```
P:\OtusAI\
├── build.gradle.kts            # root build (plugins + common config)
├── settings.gradle.kts         # includes :common, :android, :service
├── gradle.properties
├── gradle/
│   └── wrapper/
├── gradlew
├── gradlew.bat
├── common/                     # shared DTO + serialization
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/otusai/common/
│       ├── Question.kt
│       └── AnswersRequest.kt
├── service/                    # Ktor backend
│   ├── build.gradle.kts
│   └── src/
│       ├── main/kotlin/com/otusai/service/
│       │   ├── Application.kt
│       │   └── SurveyStorage.kt
│       └── test/kotlin/com/otusai/service/
│           └── SurveyRoutesTest.kt
└── android/                    # Compose frontend
    ├── build.gradle.kts
    └── src/main/
        ├── kotlin/com/otusai/android/
        │   ├── MainActivity.kt
        │   ├── SurveyViewModel.kt
        │   ├── network/
        │   │   └── SurveyApi.kt
        │   └── ui/
        │       ├── QuestionsScreen.kt
        │       └── ThankYouScreen.kt
        └── AndroidManifest.xml
```

## API Контракт

### GET /questions
```json
[
  { "id": 1, "text": "Как вас зовут?", "type": "TEXT" },
  { "id": 2, "text": "Ваш пол?", "type": "SINGLE_CHOICE" },
  { "id": 3, "text": "Какие языки знаете?", "type": "MULTI_CHOICE" }
]
```

### POST /answers
Request:
```json
{ "answers": { "1": ["Иван"], "2": ["Мужской"], "3": ["Kotlin", "Java"] } }
```
Response: `{ "status": "ok" }`

## Компоненты

### common (shared DTO)
- `Question(id: Int, text: String, type: QuestionType)` — `@Serializable`
- `QuestionType` — enum: TEXT, SINGLE_CHOICE, MULTI_CHOICE
- `AnswersRequest(answers: Map<Int, List<String>>)` — `@Serializable`
- Использует `kotlinx.serialization`

### service (Ktor backend)
- **Application.kt**: старт Ktor на порту 8080, установка ContentNegotiation (JSON), регистрация роутов
- **SurveyStorage.kt**: object c MutableList<AnswersRequest> для хранения ответов в памяти
- Роуты:
  - `GET /questions` → список из 3-5 hardcoded вопросов
  - `POST /answers` → валидация (непустой body), сохранение, `{ "status": "ok" }`

### android (Compose frontend)
- **SurveyApi.kt**: Ktor-клиент для GET /questions и POST /answers
- **SurveyViewModel.kt**: ViewModel, загружает вопросы, отправляет ответы, управляет состоянием (Loading/Success/Error/Submitted)
- **QuestionsScreen.kt**: список вопросов с полями ввода (TextField для TEXT, RadioButton для SINGLE_CHOICE, Checkbox для MULTI_CHOICE), кнопка "Отправить"
- **ThankYouScreen.kt**: экран с сообщением "Спасибо!"
- **MainActivity.kt**: точка входа, навигация между QuestionsScreen и ThankYouScreen

## Тестирование

- **service**: тест через Ktor test host — проверка GET возвращает 200 + список, POST возвращает 200 + "ok"
- **common**: unit-тесты сериализации JSON
- **android**: без UI-тестов в первой итерации

## Технологии

- Kotlin 2.0 / JVM 17
- Ktor 3.x (server + client)
- kotlinx.serialization
- Jetpack Compose (Material3, Navigation)
- Gradle 8.x (Kotlin DSL)
- Ktor test host для тестов
