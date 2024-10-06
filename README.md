<div id="toc">
    <img src="assets/logo.png" alt="Celestial logo" align="right">
    <ul style="list-style: none;">
      <summary>
        <h1>Celestial</h1>
        <p>A fast and easy to use scoreboard library</p>
      </summary>
    </ul>
</div>
<br>

___

# Dependency

1) Add the repository to your _build.gradle.kts_:

```kotlin
repositories {
    maven("https://repo.breezora.net/releases")
}
```

2) Add the dependency to your _dependencies_ section in your _build.gradle.kts_. <br>
   Note: The current version can be obtained from above's release badge.

```kotlin
dependencies {
    implementation("net.breezora:celestial:[version]")
}
```

___

# Usage

There are two types of scoreboard provided by this library: `SidebarScoreboard` and `BelowNameScoreboard`. Both of them are obtainable through the builder. <br>
The title, lines and scores are all represented by a `Component`from the [AdventureAPI](https://docs.advntr.dev/text.html) witch comes per default with the Paper software.
For better readability in all examples below [MiniMessage](https://docs.advntr.dev/minimessage/index.html) (part of AdventureAPI) is used to create components. <br>

## Creating a scoreboard

The builder is simple to use and makes your life a lot easier when it comes to creating a scoreboard in comparison to the default bukkit api.
You can create a scoreboard like in the example below (written in Kotlin; when using Java use explicit type declaration or the `var` keyword)

```kotlin
val scoreboard = Scoreboard.builder(DisplayType.SIDEBAR) // Creates a builder for a SidebarScoreboard
    .player(player) // Select the target player to which the scoreboard will be displayed
    .title(miniMessage.deserialize("<green>Example Title")) // Define the title (needed)
    .appendEmptyLine() // Adds an empty line
    .appendLine(miniMessage.deserialize("<blue>Example Text")) // Adds a line without a custom score
    .appendEmptyLine()
    .build() // Creates the SidebarScoreboard
```

Instead of calling `#append(...)` x-times you can also create a scoreboard by running `#appendLines(Collection<Component> lines)`. Passing `null` for `Component.empty()` might also work.
But then you should prefer using `Arrays#asList()` over `List#of()`

```kotlin
val scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
    .player(player)
    .title(miniMessage.deserialize("<green>Example Title"))
    .appendLines(
        List.of(
            Component.empty(),
            miniMessage.deserialize("<blue>Example Text"),
            Component.empty()
        )
    )
    .build()
```

## Displaying/Destroying a scoreboard

It's very easy to display or destroy a scoreboard. Just call `Scoreboard#display()` or `Scoreboard#destroy()`.

```kotlin
scoreboard.display() // Displays the scoreboard to the player
scoreboard.destroy() // Destroys the scoreboard
```

## Updating contents

There are several ways to update the scoreboard's content.

### Title

When you want to update the title, just call `Scoreboard#updateTitle(Component)`.

```kotlin
val title = miniMessage.deserialize("<gold>New title")
scoreboard.updateTitle(title)
```

### Single line

If only one specific line has to be updated use `Scoreboard#updateLine(int, Component)`.

```kotlin
val newLine = miniMessage.deserialize("<red>Updated text")
scoreboard.updateLine(1, newLine) // In the example above we've created a scoreboard with three lines. Since we count from zero, the middle line is at position 1
```

### Multiple lines

It's recommended to only use this when you want to update many rows because this will first reset the scoreboard and set all lines new.
That means you will have to pass all lines - even that ones that don't need to be updated - but also that you can add lines with this.

```kotlin
scoreboard.updateLines(
    miniMessage.deserialize("<green>First line"),
    miniMessage.deserialize("<dark_red>Second line"),
    Component.empty(),
    miniMessage.deserialize("<gold>Fourth line") // Adds a new line
)
```

**Note:** In this example we used `Scoreboard#updateLines(Component...)`.
Passing `null` instead of `Component.empty()` creates a new line but is **NOT RECOMMENDED** since this might lead to issues when updating the scoreboard. <br><br>

The proper and recommend way up update multiple lines is the following:

```kotlin
scoreboard.updateLines(
    List.of(
        miniMessage.deserialize("<green>First line"),
        miniMessage.deserialize("<dark_red>Second line"),
        Component.empty(),
        miniMessage.deserialize("<gold>Fourth line") // Adds a new line
    )
)
```

Here we've used `Scoreboard#updateLines(Collection<Component>)`. It's more safe to use and passing `null` fails due to non-null annotated parameters in the Collection-hierarchy.

### Remove a line

Yet, you cannot remove multiple lines. Only one per method-call is currently supported.

```kotlin
scoreboard.removeLine(3) // Remove the fourth line (added in the example above). We start counting at zero here as well
```

___

# Custom scores

Celestial supports custom scores. They're represented as a `Component` just like the regular text of a scoreboard. <br>
When creating a scoreboard, the builder also provides two methods to add scores to the lines: `ScoreboardBase#appendLine(Component, Component)` and `ScoreboardBase#appendLines(Collection<Component>, Collection<Component>)`.
They're working the exact way the methods without scores do. <br>

**Note:** Updating a score only works if a score has been set previously with the builder.

___

# Issue tracking

If you have found an issue/bug or want to request a new feature, please create a new issue ticket [here](https://github.com/AlphaLightning-net/Celestial/issues/new).
