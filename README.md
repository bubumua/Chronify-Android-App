# Chronify

## Introduction

This is a simple schedule Android App that allows you to add, delete, and view your task/reminder/todo/check. The APP is written in Kotlin with Jetpack Compose UI and uses the Room database to store data.

Many functions are developing, basic adding, deleting and editing is completed.

## Installation





## Manual

Schedule type: CYCLICAL, REMINDER, MISSION, CHECK_IN, DEFAULT

| Schedule Type | Description                        | non-null Attributes | nullable Attributes  |
| ------------- | ---------------------------------- | ------------------- | -------------------- |
| REMINDER      | Reminds you at a specific time     | end                 | begin, interval      |
| CHECK_IN      | Check in at a specific time        | begin/end           | begin/end, interval  |
| CYCLICAL      | Repeats every day at the same time | begin, interval     | begin, end           |
| MISSION       | A mission that needs to be done    | begin               | interval, end        |
| DEFAULT       | A default schedule                 |                     | begin, end, interval |

## Project structure

定义接口 ItemsRepository 并用 OfflineItemsRepository 实现它有以下几个好处：  
- 解耦：接口将具体实现与使用者解耦，使得代码更灵活。你可以在不改变使用者代码的情况下，轻松替换 OfflineItemsRepository 的实现。  
- 可测试性：使用接口可以更容易地编写单元测试。你可以创建一个 ItemsRepository 的模拟实现来测试依赖它的代码，而不需要依赖实际的数据库操作。  
- 扩展性：如果将来需要添加其他数据源（例如网络数据源），你只需创建一个新的实现类（例如 OnlineItemsRepository），而不需要修改现有的代码。  
- 遵循SOLID原则：接口和实现分离符合面向对象设计中的SOLID原则，特别是依赖倒置原则（DIP），这有助于创建更健壮和可维护的代码。 
通过这种方式，你的代码将更具灵活性、可维护性和可测试性。

