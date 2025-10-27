<div align="center">

<img src="https://img.shields.io/badge/Forge-1.20.x-3C6E71?style=for-the-badge" />
<img src="https://img.shields.io/badge/Minecraft-Java-6AA84F?style=for-the-badge" />
<img src="https://img.shields.io/badge/Status-Active-6C63FF?style=for-the-badge" />
<img src="https://img.shields.io/badge/License-MIT-111111?style=for-the-badge" />

<h2>Recycler Mod</h2>

<p>Turn selected items into <b>Scrap</b>, a straightforward currency for modpacks and servers.</p>

</div>

---

## Overview

Recycler adds a single, focused block that converts items into an internal currency called Scrap. It aims to make economy balancing simple: feed in items (for example, TACZ weapons or iron blocks) and get Scrap back. All values are configurable via TOML and can be shipped via datapack.

What you get:

- A Recycler block with five input slots and five output slots
- Scrap as a currency item
- Flexible configuration for items and tags
- Optional datapack support for shipping defaults

---

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Using the Recycler](#using-the-recycler)
- [Configuration (TOML)](#configuration-toml)
- [Datapack Support](#datapack-support-optional)
- [Resolution Order](#resolution-order-how-the-final-scrap-value-is-chosen)
- [TACZ Compatibility](#tacz-compatibility)
- [Admin Tips](#admin-tips)
- [FAQ](#faq)
- [Roadmap](#roadmap)
- [License](#license)
- [Contact](#contact)

---

## Requirements

- Minecraft Java, Forge 1.20.x
- Optional: TACZ (to recycle TACZ weapons)

---

## Installation

1) Install Forge 1.20.x
2) Drop the Recycler Mod `.jar` into the `mods/` folder
3) (Optional) Install TACZ
4) Launch the game or server

On first run, the mod creates `config/recycler-common.toml`.

---

## Using the Recycler

- Place the block in the world
- Provide FE/RF power
- Insert items in the input slots; Scrap appears in the outputs
- The energy bar and particles indicate activity

By default, behavior is driven by your configuration and/or datapacks.

---

## Configuration (TOML)

File: `config/recycler-common.toml`

At a glance:

| Key             | Type               | Example entry                                  | Notes |
|-----------------|--------------------|------------------------------------------------|-------|
| `item_allow`    | list of strings    | `"minecraft:iron_block"`                      | Explicitly recyclable items |
| `tag_allow`     | list of strings    | `"recycler:recyclables/guns"`                 | Tags whose members can be recycled |
| `item_overrides`| list of strings    | `"tacz:modern_kinetic_gun=50..120"`          | Scrap per item (fixed or range) |
| `tag_defaults`  | list of strings    | `"recycler:recyclables/tools=10..15"`        | Scrap per tag (fixed or range) |

Examples (each entry is a string inside the corresponding list):

```toml
item_allow = [
  "minecraft:iron_block",
  "tacz:modern_kinetic_gun"
]

tag_allow = [
  "recycler:recyclables/guns",
  "recycler:recyclables/tools"
]

item_overrides = [
  "minecraft:iron_block=9",
  "tacz:modern_kinetic_gun=50..120"
]

tag_defaults = [
  "recycler:recyclables/guns=50..120",
  "recycler:recyclables/tools=10..15"
]
```

Notes:

- Ranges return a random value between `min` and `max` per processed item
- Inside TOML lists you may use `#` comment lines if you need them

---

## Datapack Support (optional)

You can ship defaults via datapack instead of TOML. The mod reads `data/recycler/recycler/scrap_values.json` with:

- `defaults_by_tag`: `tagId -> { min, max }` or a number
- `overrides_by_item`: `itemId -> { min, max }` or a number

Example `scrap_values.json`:

```json
{
  "defaults_by_tag": {
    "recycler:recyclables/guns": { "min": 50, "max": 120 },
    "recycler:recyclables/tools": 12
  },
  "overrides_by_item": {
    "minecraft:iron_block": 9
  }
}
```

Datapack layout:

```
<datapack>/
  pack.mcmeta
  data/
    recycler/
      recycler/
        scrap_values.json
```

---

## Resolution Order (how the final Scrap value is chosen)

When an item is processed, the mod resolves its Scrap amount in this order:

1) If the item is in `item_allow`:
   - Use `item_overrides` if present
   - Otherwise, use the best `tag_defaults` among tags listed in `tag_allow`
2) If the item is in any `tag_allow` (even if not in `item_allow`):
   - Try `item_overrides` first
   - Otherwise, use the best matching `tag_defaults`
3) `item_overrides` (always takes precedence over `tag_defaults` when present)
4) `tag_defaults`
5) Datapack `defaults_by_tag`

“Best” means the tag with the greatest `max` range.

---

## TACZ Compatibility

TACZ weapons can be recycled into Scrap. You can:

- Allow the weapon IDs in `item_allow`
- Assign explicit amounts in `item_overrides`, or
- Cover them via `tag_allow` and `tag_defaults`

---

## Admin Tips

- Start with conservative values (lower ranges) and adjust after observing your economy
- Prefer tag defaults for broad categories, then override a few standouts per item
- Keep a simple rule-of-thumb for players (for example: iron = 9 Scrap per block)

---

## FAQ

- Can I use Scrap as a server currency? Yes. That’s the intended use.
- Can I add any item? Yes. Add it to `item_allow` and set an amount in `item_overrides`.
- Can I work by tag instead? Yes. Use `tag_allow` and `tag_defaults`.
- Can I ship balance in a datapack? Yes. Use `scrap_values.json` as shown above.

---

## Roadmap

- Additional economy integrations
- Broader balance presets by category
- Optional in-game metrics to help tune values

---

## License

See `LICENSE.txt` in this repository.

---

## Contact

Discord: **apollook**


