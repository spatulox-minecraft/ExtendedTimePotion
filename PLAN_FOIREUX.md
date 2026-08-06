# Rendre le gestionnaire de versions agnostique (1.x obfusqué ↔ 26.x non obfusqué)

## Contexte

`scripts/update-mc-version.py` suppose implicitement l'ère 26.x : code Minecraft non
obfusqué, donc aucun `mappings` dans `build.gradle`, Java 25, `implementation`. Dès qu'on
redescend sur une version `1.x` (code obfusqué), cette configuration ne builde plus.

Le dépôt contient déjà la preuve de ce qu'il faut, en comparant `ab8e4f3` (1.21.10, mappings
Mojang) à `HEAD` (26.x) :

| | 1.21.10 (obfusqué) | 26.x (non obfusqué) |
|---|---|---|
| `mappings` | `mappings loom.officialMojangMappings()` | absent |
| loader / API | `modImplementation` | `implementation` |
| Java | 21 (`release`, `source/targetCompatibility`) | 25 |
| `compatibilityLevel` (mixins) | `JAVA_21` | `JAVA_25` |
| `loom_version` | `1.13-SNAPSHOT` | `1.17-SNAPSHOT` |
| clé Fabric API | `fabric_version` | `fabric_api_version` |
| source | `ResourceLocation`, `FabricBrewingRecipeRegistryBuilder` | `Identifier`, `FabricPotionBrewingBuilder` |

**Décisions prises :**
- Le script gère **uniquement `gradle.properties`** : `minecraft_version`, `loader_version`,
  `fabric_api_version`, `loom_version`, `supported_minecraft_versions`.
- Le script **ne touche jamais au code** — les 2 symboles divergents se changent à la main.
- **Aucun support de yarn**, jamais.
- Le script doit être un gestionnaire de versions **agnostique**, sans connaissance codée en
  dur d'une version particulière.

**Correction factuelle à intégrer :** la dernière version obfusquée est **1.21.11**, pas
1.21.10 (vérifié dans le manifeste Mojang ; Fabric API y est publiée en `0.141.6+1.21.11`).
Toute règle du type « jusqu'à 1.21.10 » serait fausse.

---

## Étape 0 — Vérifications qui déterminent la suite

À faire **avant** d'écrire quoi que ce soit : elles décident s'il faut des conditions dans
`build.gradle` ou si l'on peut s'en passer entièrement. Tester sur une copie du dépôt.

1. **`mappings loom.officialMojangMappings()` est-il accepté sur 26.2 ?**
   Si oui → on le met **inconditionnellement**, et `build.gradle` devient agnostique sans
   la moindre condition. C'est le résultat le plus souhaitable.
2. **`modImplementation` fonctionne-t-il sur 26.2 ?**
   (`implementation` y fonctionne déjà — vérifié : le serveur démarre avec 43 mods.)
   Si oui → `modImplementation` partout, sans condition. Il est de toute façon obligatoire
   sur les versions obfusquées.
3. **Loom 1.17.17 peut-il builder 1.21.11 ?**
   Loom est généralement rétrocompatible ; `1.13-SNAPSHOT` était simplement le dernier loom
   de l'époque, pas une exigence. Si 1.17 builde 1.21.11, `loom_version` n'a pas besoin de
   varier selon l'ère et le script peut simplement résoudre le dernier loom stable.

Si (1) ou (2) échoue, la condition correspondante se dérive de `minecraft_version`
(`version.startsWith("1.")` → obfusqué), à isoler dans **une seule** fonction en haut de
`build.gradle`.

---

## Changements

### 1. `scripts/update-mc-version.py` — gestionnaire de versions pur

Conserver tel quel ce qui est déjà validé : détection de régression (`is_regression`),
remise à zéro sur changement de major, et surtout le découplage `--mark-supported`
(la compatibilité n'est actée qu'après un build + test serveur réussis).

À ajouter / modifier :

- **Gérer `loom_version`.** Résoudre le dernier loom **stable** depuis
  `https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml`, en excluant les
  `-alpha.` et les `-SNAPSHOT`. Ça corrige au passage la non-reproductibilité du
  `1.17-SNAPSHOT` actuel. Prévoir `--loom <version>` pour forcer, indispensable si une
  vieille version de Minecraft exige un loom plus ancien.
- **Accepter les deux noms de clé Fabric API.** `fabric_api_version` (26.x) et
  `fabric_version` (≤ 1.21.11), en réécrivant **celle qui existe déjà** dans le fichier.
  Sans ça le script échoue sur tout checkout antérieur à `a3e17de` — c'est exactement le
  point de confusion rencontré plus tôt.
- **Ne plus toucher à `src/main/resources/fabric.mod.json`.** Hors périmètre désormais
  (supprimer `update_fabric_mod_json` et son appel). Voir « Ce qui reste manuel ».
- **Yarn : jamais lu, jamais écrit.** Si `yarn_mappings` est présent dans le fichier,
  émettre un avertissement (le projet est en mappings Mojang uniquement) sans le modifier.
- Ne pas introduire de seuil de version codé en dur dans le script : il résout tout via les
  API Mojang / Fabric / Modrinth et ne décide rien sur l'ère.

### 2. `build.gradle` — s'adapter sans intervention du script

Objectif : que `./gradlew build` fonctionne quelle que soit la valeur de `minecraft_version`,
sans que le script ait à éditer ce fichier.

- Appliquer le résultat de l'étape 0 : idéalement `mappings loom.officialMojangMappings()`
  et `modImplementation` **inconditionnels**.
- Le niveau Java doit varier (1.21.x tourne sur Java 21, 26.x exige Java 25). Le dériver de
  `minecraft_version` en un seul endroit, et l'utiliser pour `options.release`,
  `sourceCompatibility` et `targetCompatibility`.

### 3. Workflow — inchangé

`.github/workflows/check-new-minecraft.yml` appelle déjà le script puis
`--mark-supported` uniquement si `build` **et** `smoke` passent. Rien à changer.

---

## Ce qui reste manuel (assumé)

À ajouter comme rappel dans le corps de PR généré par le workflow :

- Les 2 symboles du source : `Identifier` ↔ `ResourceLocation`,
  `FabricPotionBrewingBuilder` ↔ `FabricBrewingRecipeRegistryBuilder`.
- `fabric.mod.json` : `depends.minecraft` (`~1.21` ↔ `>=26.1.0 <27`) et `depends.java`
  (`>=21` ↔ `>=25`).
- `extended-time-potion.mixins.json` : `compatibilityLevel` (`JAVA_21` ↔ `JAVA_25`).

---

## Vérification

1. **Étape 0** sur une copie (`rsync` hors `build/`, `run/`, `.git`) — c'est elle qui valide
   ou invalide la conception ci-dessus.
2. **Non-régression 26.x** : `python3 scripts/update-mc-version.py 26.2` puis
   `./gradlew build` et `bash .github/scripts/headless-server-test.sh`. Doit rester vert,
   avec les 50 potions et le marqueur de brassage.
3. **Descente réelle vers 1.21.11** : `python3 scripts/update-mc-version.py 1.21.11`, puis
   appliquer les 2 symboles à la main, puis builder. Vérifier que
   `supported_minecraft_versions` a bien été **réinitialisé à `1.21.11`** (c'est une
   régression, donc pas de test de compatibilité requis).
4. **Compatibilité des noms de clé** : sur un `git checkout ab8e4f3` (où la clé s'appelle
   `fabric_version`), le script doit fonctionner et réécrire cette clé-là.
5. **Le test garde ses dents** : relancer avec `EXPECTED_POTIONS=999`, doit sortir en 1.
6. Tests unitaires du comparateur déjà en place à rejouer :
   `is_regression("1.21.11", "26.2") == True`, `("26w14a", "26.1") == False`.
