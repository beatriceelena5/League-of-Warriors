# League-of-Warriors
Turn‑based RPG built with Java Swing (JDK 17+) – explore, battle, level‑up

---

## Overview

**League of Warriors** began as a terminal‑only assignment and evolved into a fully‑fledged GUI application.
The game demonstrates practical use of classic **object‑oriented design patterns** while keeping a clean separation between game logic and presentation.

---

## Features

* **Login screen** – email + password with quick‑select dropdown and inline error messages
* **Character selection** – choose Mage, Rogue or Warrior; user profile displayed in a side panel
* **Procedurally generated maps** from level 2 onward; arrow buttons activate only on valid moves
* **Turn‑based combat** – normal attacks or powerful abilities, live HP/MP bars, combat log
* **Skill manager** – list of spells with mana cost & damage; one‑click `USE` button
* **Level summary** – XP gained, enemies defeated, next‑level prompt
* **Polished visuals** – centralised `GameColors` palette; all assets AI‑generated & auto‑scaled
* **Robust error handling** – user‑friendly messages instead of stack traces

---

## Design Patterns

| Pattern            | Purpose                                                 | File                    |
| ------------------ | ------------------------------------------------------- | ----------------------- |
| **Factory Method** | create character instances (`Mage`, `Rogue`, `Warrior`) | `CharacterFactory.java` |
| **Singleton**      | guarantee a single game instance                        | `Game.java`             |
| **Observer**       | update GUI elements (HP/MP bars, combat log)            | `EventBus`, `BattleLog` |
| **Strategy**       | switch between normal attack and spell casting          | `AttackStrategy.java`   |
| **Visitor**        | apply spell effects to entities                         | `Spell.java`            |

---

## Gameplay

1. **Log in** – choose an email or type a new one, enter password
2. **Select hero** – each class has unique stats & spells
3. **Explore** – move on the grid; fog‑of‑war lifts as you advance
4. **Fight** – decide between a quick hit or a costly spell
5. **Level up** – check summary, start next level
