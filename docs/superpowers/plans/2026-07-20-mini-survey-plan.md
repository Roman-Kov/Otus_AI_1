# Мини-анкета — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Multi-module Gradle project with Ktor backend + Compose Android client for a mini survey app.

**Architecture:** Single Gradle project with three modules — `:common` (shared DTOs), `:service` (Ktor server), `:android` (Compose UI). Serialization via kotlinx.serialization.

**Tech Stack:** Kotlin 2.0.0, Ktor 3.0.0, Jetpack Compose (Material3), AGP 8.2.2, kotlinx-serialization 1.6.3

---

### Task 0: Create root Gradle project

**Files:**
- Create: `P:\OtusAI\settings.gradle.kts`
- Create: `P:\OtusAI\build.gradle.kts`
- Create: `P:\OtusAI\gradle.properties`
- Create: `P:\OtusAI\gradle\wrapper\gradle-wrapper.properties`
- Create: `P:\OtusAI\local.properties`
- Create: `P:\OtusAI\gradlew`
- Create: `P:\OtusAI\gradlew.bat`

- [ ] **Step 1: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OtusAI"
include(":common", ":service", ":android")
```

- [ ] **Step 2: Create root build.gradle.kts**

```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
}
```

- [ ] **Step 3: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 4: Create gradle-wrapper.properties**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 5: Generate Gradle wrapper**

```bash
cd P:\OtusAI
gradle wrapper --gradle-version 8.4
```

If Gradle is not installed, manually create `gradlew` and `gradlew.bat` by copying from an existing project (e.g. `C:\Users\rojer\otus-ii\android\gradlew.bat`).

- [ ] **Step 6: Create local.properties pointing to Android SDK**

```properties
sdk.dir=C:\\Users\\rojer\\AppData\\Local\\Android\\Sdk
```

- [ ] **Step 7: Verify project syncs**

Run: `cd P:\OtusAI && .\gradlew projects`
Expected: lists root, :common, :service, :android

---

### Task 1: common module — shared DTOs + serialization

**Files:**
- Create: `P:\OtusAI\common\build.gradle.kts`
- Create: `P:\OtusAI\common\src\main\kotlin\com\otusai\common\Question.kt`
- Create: `P:\OtusAI\common\src\main\kotlin\com\otusai\common\AnswersRequest.kt`
- Create: `P:\OtusAI\common\src\test\kotlin\com\otusai\common\SerializationTest.kt`

- [ ] **Step 1: Create common/build.gradle.kts**

```kotlin
plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Write the failing serialization test**

File `P:\OtusAI\common\src\test\kotlin\com\otusai\common\SerializationTest.kt`:

```kotlin
package com.otusai.common

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testQuestionSerialization() {
        val question = Question(1, "Test?", QuestionType.TEXT)
        val encoded = Json.encodeToString(question)
        val decoded = Json.decodeFromString<Question>(encoded)
        assertEquals(question, decoded)
    }

    @Test
    fun testAnswersRequestSerialization() {
        val request = AnswersRequest(mapOf(1 to listOf("A"), 2 to listOf("B", "C")))
        val encoded = Json.encodeToString(request)
        val decoded = Json.decodeFromString<AnswersRequest>(encoded)
        assertEquals(request, decoded)
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `cd P:\OtusAI && .\gradlew :common:test`
Expected: FAIL — compilation errors (Question, AnswersRequest, QuestionType not defined)

- [ ] **Step 4: Create Question.kt**

File `P:\OtusAI\common\src\main\kotlin\com\otusai\common\Question.kt`:

```kotlin
package com.otusai.common

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val text: String,
    val type: QuestionType
)

@Serializable
enum class QuestionType {
    TEXT,
    SINGLE_CHOICE,
    MULTI_CHOICE
}
```

- [ ] **Step 5: Create AnswersRequest.kt**

File `P:\OtusAI\common\src\main\kotlin\com\otusai\common\AnswersRequest.kt`:

```kotlin
package com.otusai.common

import kotlinx.serialization.Serializable

@Serializable
data class AnswersRequest(
    val answers: Map<Int, List<String>>
)
```

- [ ] **Step 6: Run test to verify it passes**

Run: `cd P:\OtusAI && .\gradlew :common:test`
Expected: PASS (2 tests)

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "feat(common): add shared DTOs with serialization"
```

---

### Task 2: service module — Ktor backend

**Files:**
- Create: `P:\OtusAI\service\build.gradle.kts`
- Create: `P:\OtusAI\service\src\main\kotlin\com\otusai\service\Application.kt`
- Create: `P:\OtusAI\service\src\main\kotlin\com\otusai\service\SurveyRoutes.kt`
- Create: `P:\OtusAI\service\src\main\kotlin\com\otusai\service\SurveyStorage.kt`
- Create: `P:\OtusAI\service\src\test\kotlin\com\otusai\service\SurveyRoutesTest.kt`

- [ ] **Step 1: Create service/build.gradle.kts**

```kotlin
plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

dependencies {
    implementation(project(":common"))
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

tasks.test {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Write the failing integration test**

File `P:\OtusAI\service\src\test\kotlin\com\otusai\service\SurveyRoutesTest.kt`:

```kotlin
package com.otusai.service

import com.otusai.common.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SurveyRoutesTest {

    @Test
    fun `GET questions returns list`() = testApplication {
        application { configureRouting() }
        val response = client.get("/questions")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        kotlin.test.assertTrue(body.contains("id"))
    }

    @Test
    fun `POST answers returns ok`() = testApplication {
        application { configureRouting() }
        val request = AnswersRequest(mapOf(1 to listOf("Test")))
        val response = client.post("/answers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `cd P:\OtusAI && .\gradlew :service:test`
Expected: FAIL — configureRouting() not defined

- [ ] **Step 4: Create SurveyStorage.kt**

File `P:\OtusAI\service\src\main\kotlin\com\otusai\service\SurveyStorage.kt`:

```kotlin
package com.otusai.service

import com.otusai.common.AnswersRequest

object SurveyStorage {
    private val answers = mutableListOf<AnswersRequest>()

    fun save(request: AnswersRequest) {
        answers.add(request)
    }

    fun all(): List<AnswersRequest> = answers.toList()
}
```

- [ ] **Step 5: Create SurveyRoutes.kt**

File `P:\OtusAI\service\src\main\kotlin\com\otusai\service\SurveyRoutes.kt`:

```kotlin
package com.otusai.service

import com.otusai.common.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/questions") {
            val questions = listOf(
                Question(1, "Как вас зовут?", QuestionType.TEXT),
                Question(2, "Ваш пол?", QuestionType.SINGLE_CHOICE),
                Question(3, "Какие языки программирования вы знаете?", QuestionType.MULTI_CHOICE),
                Question(4, "Сколько лет опыта?", QuestionType.TEXT),
                Question(5, "Что хотите изучить?", QuestionType.TEXT)
            )
            call.respond(questions)
        }

        post("/answers") {
            val request = call.receive<AnswersRequest>()
            SurveyStorage.save(request)
            call.respond(mapOf("status" to "ok"))
        }
    }
}
```

- [ ] **Step 6: Create Application.kt**

File `P:\OtusAI\service\src\main\kotlin\com\otusai\service\Application.kt`:

```kotlin
package com.otusai.service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        configureRouting()
    }.start(wait = true)
}
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `cd P:\OtusAI && .\gradlew :service:test`
Expected: PASS (2 tests)

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat(service): add Ktor backend with GET /questions and POST /answers"
```

---

### Task 3: android module — Compose frontend

**Files:**
- Create: `P:\OtusAI\android\build.gradle.kts`
- Create: `P:\OtusAI\android\src\main\AndroidManifest.xml`
- Create: `P:\OtusAI\android\src\main\kotlin\com\otusai\android\MainActivity.kt`
- Create: `P:\OtusAI\android\src\main\kotlin\com\otusai\android\network\SurveyApi.kt`
- Create: `P:\OtusAI\android\src\main\kotlin\com\otusai\android\SurveyViewModel.kt`
- Create: `P:\OtusAI\android\src\main\kotlin\com\otusai\android\ui\QuestionsScreen.kt`
- Create: `P:\OtusAI\android\src\main\kotlin\com\otusai\android\ui\ThankYouScreen.kt`

- [ ] **Step 1: Create android/build.gradle.kts**

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.otusai.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.otusai.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":common"))

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Ktor client
    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-okhttp:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

- [ ] **Step 2: Create AndroidManifest.xml**

File `P:\OtusAI\android\src\main\AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:label="Мини-анкета"
        android:supportsRtl="true"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar"
        android:usesCleartextTraffic="true">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 3: Create SurveyApi.kt**

File `P:\OtusAI\android\src\main\kotlin\com\otusai\android\network\SurveyApi.kt`:

```kotlin
package com.otusai.android.network

import com.otusai.common.AnswersRequest
import com.otusai.common.Question
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SurveyApi(private val baseUrl: String = "http://10.0.2.2:8080") {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getQuestions(): List<Question> {
        return client.get("$baseUrl/questions").body()
    }

    suspend fun submitAnswers(request: AnswersRequest) {
        client.post("$baseUrl/answers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
```

- [ ] **Step 4: Create SurveyViewModel.kt**

File `P:\OtusAI\android\src\main\kotlin\com\otusai\android\SurveyViewModel.kt`:

```kotlin
package com.otusai.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otusai.android.network.SurveyApi
import com.otusai.common.AnswersRequest
import com.otusai.common.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SurveyUiState {
    data object Loading : SurveyUiState
    data class Questions(val list: List<Question>) : SurveyUiState
    data object Submitted : SurveyUiState
    data class Error(val message: String) : SurveyUiState
}

class SurveyViewModel : ViewModel() {

    private val api = SurveyApi()

    private val _state = MutableStateFlow<SurveyUiState>(SurveyUiState.Loading)
    val state: StateFlow<SurveyUiState> = _state.asStateFlow()

    private val answers = mutableMapOf<Int, MutableSet<String>>()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _state.value = SurveyUiState.Loading
            try {
                val questions = api.getQuestions()
                _state.value = SurveyUiState.Questions(questions)
            } catch (e: Exception) {
                _state.value = SurveyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateAnswer(questionId: Int, value: String) {
        answers[questionId] = mutableSetOf(value)
    }

    fun toggleMultiAnswer(questionId: Int, value: String) {
        val current = answers.getOrPut(questionId) { mutableSetOf() }
        if (current.contains(value)) current.remove(value) else current.add(value)
    }

    fun submit() {
        viewModelScope.launch {
            try {
                val request = AnswersRequest(answers.mapValues { it.value.toList() })
                api.submitAnswers(request)
                _state.value = SurveyUiState.Submitted
            } catch (e: Exception) {
                _state.value = SurveyUiState.Error(e.message ?: "Submit failed")
            }
        }
    }
}
```

- [ ] **Step 5: Create QuestionsScreen.kt**

File `P:\OtusAI\android\src\main\kotlin\com\otusai\android\ui\QuestionsScreen.kt`:

```kotlin
package com.otusai.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otusai.android.SurveyViewModel
import com.otusai.android.SurveyUiState
import com.otusai.common.Question
import com.otusai.common.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(viewModel: SurveyViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Мини-анкета") })
    }) { padding ->
        when (val s = state) {
            is SurveyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SurveyUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${s.message}")
                        Button(onClick = { viewModel.loadQuestions() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is SurveyUiState.Questions -> {
                QuestionsForm(s.list, viewModel, padding)
            }
            is SurveyUiState.Submitted -> { /* handled by navigation */ }
        }
    }
}

@Composable
private fun QuestionsForm(
    questions: List<Question>,
    viewModel: SurveyViewModel,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        questions.forEach { q ->
            QuestionCard(q, viewModel)
        }
        Button(
            onClick = { viewModel.submit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun QuestionCard(question: Question, viewModel: SurveyViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(question.text, style = MaterialTheme.typography.titleSmall)

            when (question.type) {
                QuestionType.TEXT -> {
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            viewModel.updateAnswer(question.id, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                QuestionType.SINGLE_CHOICE -> {
                    val options = listOf("Мужской", "Женский", "Не указано")
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = false,
                                onClick = { viewModel.updateAnswer(question.id, option) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
                QuestionType.MULTI_CHOICE -> {
                    val options = listOf("Kotlin", "Java", "Python", "JavaScript", "C++")
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { viewModel.toggleMultiAnswer(question.id, option) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 6: Create ThankYouScreen.kt**

File `P:\OtusAI\android\src\main\kotlin\com\otusai\android\ui\ThankYouScreen.kt`:

```kotlin
package com.otusai.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThankYouScreen(onRestart: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Спасибо!",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRestart) {
                Text("Пройти ещё раз")
            }
        }
    }
}
```

- [ ] **Step 7: Create MainActivity.kt**

File `P:\OtusAI\android\src\main\kotlin\com\otusai\android\MainActivity.kt`:

```kotlin
package com.otusai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.otusai.android.ui.QuestionsScreen
import com.otusai.android.ui.ThankYouScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SurveyApp()
            }
        }
    }
}

@Composable
fun SurveyApp() {
    val navController = rememberNavController()
    val viewModel: SurveyViewModel = viewModel()

    NavHost(navController, startDestination = "questions") {
        composable("questions") {
            QuestionsScreen(viewModel)
            val state = viewModel.state.collectAsState().value
            if (state is SurveyUiState.Submitted) {
                navController.navigate("thankyou") {
                    popUpTo("questions") { inclusive = true }
                }
            }
        }
        composable("thankyou") {
            ThankYouScreen(onRestart = {
                viewModel.loadQuestions()
                navController.navigate("questions") {
                    popUpTo("thankyou") { inclusive = true }
                }
            })
        }
    }
}
```

- [ ] **Step 8: Verify android module compiles**

Run: `cd P:\OtusAI && .\gradlew :android:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9: Commit**

```bash
git add -A
git commit -m "feat(android): add Compose survey UI with Ktor client"
```

---

## Self-Review

### Spec coverage
1. **Backend GET /questions** → Task 2 (SurveyRoutes.kt)
2. **Backend POST /answers** → Task 2 (SurveyRoutes.kt)
3. **Storage in memory** → Task 2 (SurveyStorage.kt)
4. **Android loads questions** → Task 3 (SurveyApi.kt + SurveyViewModel.kt)
5. **Android displays questions** → Task 3 (QuestionsScreen.kt)
6. **Android sends answers** → Task 3 (SurveyViewModel.kt.submit())
7. **"Спасибо!" screen** → Task 3 (ThankYouScreen.kt)
8. **Multi-module Gradle** → Task 0 (settings.gradle.kts)
9. **Shared DTOs** → Task 1 (common module)

### Placeholder scan
No TODOs, TBDs, or incomplete sections found.

### Type consistency
All types (Question, QuestionType, AnswersRequest, SurveyUiState) are defined and used consistently across tasks.

---

## Code Review: Task 1 (common module)

### ✅ Spec compliant

**Verified by reading code + running tests:**

| Requirement | Status | Evidence |
|---|---|---|
| `common/build.gradle.kts` with kotlin("jvm"), kotlin("plugin.serialization"), kotlinx-serialization-json, kotlin("test"), useJUnitPlatform | ✅ | `common/build.gradle.kts:1-13` |
| `Question.kt` with @Serializable data class Question(id, text, type) + @Serializable enum QuestionType { TEXT, SINGLE_CHOICE, MULTI_CHOICE } | ✅ | `common/.../Question.kt:1-17` |
| `AnswersRequest.kt` with @Serializable data class AnswersRequest(answers: Map<Int, List<String>>) | ✅ | `common/.../AnswersRequest.kt:1-8` |
| `SerializationTest.kt` with round-trip tests for both DTOs | ✅ | `common/.../SerializationTest.kt:1-26` (2 tests) |
| Tests pass (2/2) | ✅ | `.\gradlew :common:cleanTest :common:test` — BUILD SUCCESSFUL |

**Deviations from plan spec (all acceptable improvements, not issues):**
- `common/build.gradle.kts` omits `version "2.0.0"` on plugin declarations — versions are managed centrally in root `build.gradle.kts`, which is better practice
- Root `build.gradle.kts` adds `org.jetbrains.kotlin.jvm` plugin (not in Task 0 spec) — required for `:common` to resolve `kotlin("jvm")`

**No missing requirements, no extra/unneeded work, no misunderstandings.**
