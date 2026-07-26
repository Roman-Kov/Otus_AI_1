# Otus AI — Мини-анкета

Kotlin Multiplatform проект: backend (Ktor) + Android (Jetpack Compose).

## Требования

- JDK 17+
- Android SDK (platform 34, build-tools 34+)
- Android эмулятор или устройство

## Запуск backend

```bash
./gradlew :service:run
```

Сервер запустится на `http://localhost:8080`.
Доступные endpoints: `GET /questions`, `POST /answers`.

## Запуск Android приложения

```bash
./gradlew :android:installDebug
```

Приложение подключается к backend по адресу `http://10.0.2.2:8080` (стандартный alias эмулятора для localhost).

## Скриншоты

| Экран анкеты | Экран после отправки |
|---|---|
| <img src="screenshots/screen1_filled.png" width="300"> | <img src="screenshots/screen2_thankyou.png" width="300"> |

## Использованные промпты

1. **Генерация проекта** — Сгенерируй простое приложение «Мини-анкета» из backend и Android frontend. Технологии: Kotlin, Ktor (backend), Jetpack Compose (Android). Backend: GET /questions, POST /answers. Android: загружает вопросы, отображает через Compose, отправляет ответы, экран «Спасибо!».

2. **Заливка на GitHub** — Залей проект из текущей папки на GitHub в репозиторий https://github.com/XXX/XXX.git

3. **Проверка и скриншоты** — Проверь, что проект работает: запусти backend, открой эмулятор, установи приложение, заполни анкету, отправь, сделай скриншоты обоих экранов.

4. **Оформление README** — Оформи README проекта: инструкция по запуску, скриншоты в таблице, список использованных промптов.
