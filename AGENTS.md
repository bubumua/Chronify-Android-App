# Repository Guidelines

## Project Structure & Module Organization

Chronify is a single-module Android app. Root Gradle configuration lives in `settings.gradle.kts`, `build.gradle.kts`, and `gradle.properties`; app configuration is in `app/build.gradle.kts`. Main Kotlin sources are under `app/src/main/java/myapp/chronify`, split into `data`, `ui`, `ui/navigation`, `ui/theme`, `ui/viewmodel`, and `utils`. Android resources live in `app/src/main/res`, bundled assets in `app/src/main/assets`, and exported Room schemas in `app/schemas/myapp.chronify.data.AppDatabase`. Instrumented tests live in `app/src/androidTest/kotlin`.

## Build, Test, and Development Commands

Use the checked-in Gradle wrapper from PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

`assembleDebug` builds the debug APK and matches the current GitHub Actions check. `installDebug` deploys to a connected emulator or device. `connectedDebugAndroidTest` runs instrumented Android tests and requires a running device. `lintDebug` runs Android lint for the debug variant.

## Coding Style & Naming Conventions

Use Kotlin with Java 17 target settings. Follow the existing Android Kotlin style: 4-space indentation, trailing commas in multi-line argument lists when useful, `PascalCase` for classes, composables, and screens, and `camelCase` for functions, properties, and parameters. Keep packages under `myapp.chronify`. Compose UI functions should accept `modifier: Modifier = Modifier` near the end of the parameter list and use `@Preview` only for previewable UI.

## Testing Guidelines

Place local JVM tests in `app/src/test` if added later, and instrumented tests in `app/src/androidTest/kotlin`. Name test classes after the unit under test, for example `NifeDaoTest`, and use descriptive method names such as `daoInsert_insertsSE`. For Room changes, prefer in-memory databases in tests and keep exported schemas updated in `app/schemas`.

## Commit & Pull Request Guidelines

The current history uses short, direct summaries, often in Chinese, such as `完成直方图和折线图组件的编写` or `优化了信息显示`. Keep commits focused and describe the user-visible or architectural change. Pull requests should include a concise description, linked issue when applicable, screenshots or screen recordings for UI changes, migration notes for database/schema changes, and the commands you ran. PRs targeting `main` or `starter` must pass `:app:assembleDebug`.

## Security & Configuration Tips

Do not commit local SDK paths, signing secrets, or generated build outputs. Keep machine-specific settings in `local.properties`; use Gradle or CI secrets for release credentials.
