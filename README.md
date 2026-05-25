# Smart Placement

A Fabric mod for Minecraft 1.21.11 that **inverts the facing direction of directional blocks while sneaking**.

- Observer faces **toward** you instead of away
- Pistons, dispensers, droppers, and crafters all flip
- Works perfectly in singleplayer, multiplayer (with optional server companion), and vanilla servers (rotation trick fallback)

## Features

| Feature | Description |
|---|---|
| **Sneak Inversion** | Hold Sneak while right-clicking to flip block facing |
| **Per-block config** | Enable/disable inversion per block type |
| **Ghost preview** | Coloured wireframe shows the final facing before you click |
| **Action-bar feedback** | Subtle confirmation messages (configurable) |
| **Config screen** | Full in-game config via Mod Menu + Cloth Config |
| **Scroll Rotation** *(bonus)* | Sneak + scroll to cycle through all 6 facings |
| **Smart Stairs** *(bonus)* | Auto top/bottom stair half based on click position |
| **Placement Memory** *(bonus)* | Remembers last-used facing per block type |

## Requirements

- Minecraft **1.21.11**
- [Fabric Loader](https://fabricmc.net/use/) ≥ 0.16.0
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config API](https://modrinth.com/mod/cloth-config) (required)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional — for in-game config screen)

## Building

### Prerequisites

- **Java 21** (JDK, not JRE)
- Internet connection (Gradle downloads dependencies on first build)

> **Note on Yarn mappings:** `gradle.properties` pins `yarn_mappings=1.21.11+build.1`.
> If this build number does not exist yet, look up the correct build at
> https://fabricmc.net/versions.html and update `yarn_mappings` accordingly.

### Steps

```bash
# 1. Download the Gradle wrapper binary (one-time setup)
gradle wrapper --gradle-version 8.10

# 2. Build the mod JAR
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows
```

The compiled JAR is placed in `build/libs/smart-placement-1.0.0.jar`.
Drop it into your Minecraft `mods/` folder along with Fabric API and Cloth Config.

### Run in development

```bash
./gradlew runClient   # launches Minecraft with the mod loaded
./gradlew runServer   # launches a dedicated server
```

## Multiplayer / Server Companion

Smart Placement works in three modes depending on your server:

| Scenario | How it works |
|---|---|
| **Singleplayer / LAN** | Both client and server (same JVM) flip the block. Perfect accuracy. |
| **Dedicated server — with mod** | Client sends a packet before placing; server mixin flips FACING. Perfect accuracy. |
| **Vanilla dedicated server** | Client temporarily mirrors player rotation before the placement packet is sent (*rotation trick*). ~95% accurate; may rarely fail on very laggy connections. |

To use the companion on a dedicated server, simply drop the **same JAR** into the server's `mods/` folder — the JAR handles both environments via Fabric's `@Environment` separation.

## Configuration

Open the config screen via:
- **Mod Menu** → Smart Placement → ⚙ Config  
- Or edit `config/smart_placement.json` directly

### Config options

#### General
| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | Master on/off switch |
| `sneakInversion` | `true` | Sneak to invert |
| `invertOnKeybind` | `false` | Keybind toggle instead of sneak |

#### Per-Block
All default to `true`. Setting `perBlock_allDirectional = false` and enabling only specific entries lets you opt in selectively.

#### Preview
| Key | Default | Description |
|---|---|---|
| `showPreview` | `true` | Ghost-block outline |
| `previewColor` | `0xFF00FF00` | ARGB colour (green) |
| `previewOpacity` | `0.5` | 0.0 – 1.0 |

## Keybindings

All bindings are **unbound by default** (Options → Controls → Smart Placement):

| Binding | Description |
|---|---|
| Toggle Inversion | Enable/disable inversion mode without Sneak |
| Cycle Facing | Cycle through block facings (requires Scroll Rotation) |

## License

MIT — see [LICENSE](LICENSE).
