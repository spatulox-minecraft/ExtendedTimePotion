# Auto-update Minecraft : borne de compat prouvée, issue de crash, usage local

> **Étape 0 (préférence dépôt)** : ce fichier doit être recopié dans `docs/mc-update-plan.md`
> au démarrage de l'implémentation, et remplacer `docs/ci-plan.md` (non commité), qu'il
> supersède. `PLAN_FOIREUX.md` reste, il traite d'un autre sujet (support de l'ère 1.x obfusquée).
>
> **Langue** : tout le code et le texte généré passent en **anglais** (§6). Les docs — ce plan,
> la section README — restent en français.

## Contexte

Le dépôt a déjà une chaîne d'auto-update (`check-new-minecraft.yml` + `scripts/update-mc-version.py`)
qui tourne le mercredi, résout les versions Fabric, build, smoke-teste un serveur headless et ouvre
une PR. Trois choses manquent par rapport au besoin exprimé :

1. **Rien ne notifie** quand le mod casse sur une nouvelle version : PR draft sans assigné, aucune
   issue. Le job reste vert (`continue-on-error: true` partout).
2. **`depends.minecraft` ne suit pas.** Il vaut `">=26.1.0 <27"` — le jar prétend se charger sur
   toute la 26.x — alors que `supported_minecraft_versions=26.1,26.1.1,26.1.2` ne prouve que la
   26.1.x. Le script ne le recalcule que si le **major** change, donc quasi jamais.
3. **Le loader n'est pas vérifié contre la version MC cible.** `latest_stable_loader()`
   (`scripts/update-mc-version.py:107`) prend le dernier loader stable *global*, alors que
   `/v2/versions/loader/{mc}` — déjà appelé juste à côté par `fabric_supports()` — contient
   précisément l'info « ce loader est-il listé pour cette version ».

Deux bugs bloquants s'y ajoutent, vérifiés dans le code : `get_json` (`:79-86`) renvoie le corps de
**toute** `HTTPError` qui parse en JSON comme une réponse valide — un 5xx de Fabric meta devient
donc « Fabric ne supporte pas encore Minecraft X », exit 2, **workflow vert**, updates arrêtées en
silence ; et `modrinth { uploadFile = tasks.jar }` (`build.gradle:101`, `:120`) publie le jar **dev**
(mappings `named`) au lieu de `remapJar`, soit un jar qui ne se charge pas chez les joueurs.

**Résultat visé** — la borne monte le temps du test et n'est conservée que si le mod démarre :

```
gradle.properties     minecraft_version=26.1  ->  26.1.1
fabric.mod.json       "minecraft": "=26.1"    ->  ">=26.1 <=26.1.1"   (si build + serveur OK)
                                                  "=26.1"  restauré    (sinon, + issue assignée)
```

---

## Modèle de compatibilité

Une **série** = les deux premiers composants (`26.1.2` → `26.1`). Un jar = une série
(acté en `7abcde4`). Les deux clés cessent enfin de se contredire :

| `minecraft_version` | `supported_minecraft_versions` | `depends.minecraft` |
|---|---|---|
| `26.1`   | `26.1`                   | `"=26.1"` |
| `26.1.1` | `26.1,26.1.1`            | `">=26.1 <=26.1.1"` |
| `26.1.2` | `26.1,26.1.1,26.1.2`     | `">=26.1 <=26.1.2"` |
| `26.2`   | `26.2` *(reset série)*   | `"=26.2"` |
| `26.2.1` | `26.2,26.2.1`            | `">=26.2 <=26.2.1"` |

La borne haute = la plus haute version **prouvée** de la série. Borne basse = la série elle-même.

**Ordre imposé, et sa contrepartie.** `depends.minecraft` doit être élargi **avant** le test :
sinon Fabric Loader refuse de charger le mod sur le serveur de la nouvelle version,
`headless-server-test.sh:125` matche `Incompatible mod set`, et aucune version ne peut jamais être
validée. La borne est donc montée dans le commit 1, puis :

- build + serveur **OK** → on la garde, et `--mark-supported` ajoute la version à la liste (commit 2) ;
- **KO** → `--revert-compat` restaure l'état antérieur des **deux** clés de compatibilité (commit 2),
  PR draft, issue assignée.

Les **bumps de version** (`minecraft_version`, `loader_version`, `fabric_api_version`, `mod_version`),
eux, sont conservés dans tous les cas : c'est le diff de dépendances déjà fait, dont part la personne
qui reprend la PR à la main. Seules les **affirmations de compatibilité** sont annulées. Rien de non
prouvé ne subsiste, même sur la branche de PR.

---

## 1. `scripts/update-mc-version.py`

### 1a. `get_json` — ne plus masquer les pannes réseau (bug bloquant)

Ajouter `allow_status: tuple[int, ...] = ()`. Sur `HTTPError` : renvoyer le corps **uniquement** si
`exc.code in allow_status`, sinon `raise Failure(f"HTTP {exc.code} from {url}")` → exit 1 → workflow
rouge. Le commentaire des lignes 80-81 (« Fabric meta répond 400 pour une version inconnue »)
descend chez le seul appelant qui passe `allow_status=(400,)`.

### 1b. Un seul appel pour « Fabric supporte-t-il, et avec quel loader ? »

Fusionner `fabric_supports()` (`:102`) et `latest_stable_loader()` (`:107`) en :

```python
def loader_for(minecraft_version: str) -> str | None:
    """Latest STABLE loader listed by Fabric for this Minecraft version.
    None = Fabric does not support it yet, or no stable loader => exit 2."""
```

`GET {FABRIC_META}/loader/{mc}` avec `allow_status=(400,)`. Liste vide → `None`. Sinon premier
`entry["loader"]["stable"] is True` → `entry["loader"]["version"]`. Aucun stable → `None`
(message distinct : « only unstable loaders are listed »). C'est le « si compatible avec la
dernière version minecraft, sinon stop » demandé, et ça supprime un appel réseau.

### 1c. Helpers de série et calcul de la borne

```python
def series_of(version: str) -> str          # "26.1.2" -> "26.1" ; "26.2" -> "26.2" ; "27" -> "27"
def compat_range(target: str, supported: list[str]) -> str
```

`compat_range` : ne garde de `supported + [target]` que les versions de `series_of(target)`
numériquement parsables, prend le max via `parse_version` (`:151`, **conservé** — c'est lui qui
ordonne), puis renvoie `"={série}"` si ce max vaut la série, sinon `">={série} <={max}"`.

`major_of()` et `is_regression()` (`:147`, `:161`) deviennent inutiles : un changement de série
couvre le changement de major **et** la régression. Les supprimer, ainsi que la branche `regression`
de `update_gradle_properties()` (`:204-214`) et les paramètres `major_changed` / `regression`.

### 1d. `update_gradle_properties` — reset sur la série, et `mod_version`

- Reset de `supported_minecraft_versions` quand `series_of(target) != series_of(current)`
  (aujourd'hui : changement de major, `:210`).
- `update_mod_version(text, target)` : `mod_version` a la forme `<série MC>-<version mod>`
  (`26.1-1.1.0`, aujourd'hui figé à `26.1` alors que MC est en `26.1.2`). Découper sur le **premier**
  `-`, remplacer la partie gauche par `series_of(target)`. Pas de `-` → avertissement, rien écrit.
  `1.1.0` reste une décision de release manuelle.
- Toutes les clés dans la même passe → un seul `write_preserving_final_newline` (`:174`, qui gère
  déjà l'absence de newline final de `gradle.properties`).

### 1e. `update_fabric_mod_json` — appelée à chaque update

Remplacer le corps (`:244-261`) par un équivalent JSON utilisant `compat_range`. Supprimer le garde
`if major_changed:` (`:395`). `mark_supported()` (`:222`) recalcule et réécrit aussi la borne après
avoir étendu la liste, pour que les deux clés ne puissent jamais diverger.

### 1f. Snapshot de compat et `--revert-compat` (nouveau)

`--revert-compat` **restaure un état antérieur**, il ne recalcule rien et ne prend aucun argument.

**À l'écriture** (chemin d'update, avant toute modification), le script dépose à la racine du dépôt
un `.mc-update-state.json` (gitignoré) contenant les valeurs d'avant des deux clés de compatibilité,
plus la cible pour se garder d'un état périmé :

```json
{
  "target": "26.1.1",
  "supported_minecraft_versions": "26.1",
  "depends_minecraft": "=26.1"
}
```

**Au revert** : lire le fichier, vérifier que `target` correspond au `minecraft_version` courant
(sinon `Failure` → exit 1, l'état ne concerne pas cette update), réécrire
`supported_minecraft_versions` dans `gradle.properties` **et** `depends.minecraft` dans
`fabric.mod.json`, puis supprimer le fichier d'état. Absent → `Failure("no update state to revert")`.

Un fichier d'état plutôt qu'un argument CLI : ça marche identiquement en CI (même workspace d'une
étape à l'autre) et en local via `--run-tests`, sans dépendre de git ni obliger l'appelant à
transporter des valeurs. `mark_supported()` supprime aussi le fichier — la compat étant validée,
il n'y a plus rien à annuler.

### 1g. `--run-tests` (nouveau) — la séquence complète en local

Après une update réussie, enchaîne dans `REPO_ROOT` via `subprocess.run` (sortie streamée) :

```
./gradlew build --stacktrace          (gradlew.bat si os.name == "nt")
bash .github/scripts/headless-server-test.sh
```

Les deux passent → `mark_supported()`, exit 0. L'un échoue → `revert_compat()`, message expliquant
ce qui a cassé, exit 1. Incompatible avec `--dry-run` (erreur d'argparse). C'est exactement la
séquence de la CI, en une commande :

```bash
python3 scripts/update-mc-version.py --run-tests          # dernière release Mojang
python3 scripts/update-mc-version.py 26.2 --run-tests     # version forcée
```

### 1h. Sorties

Ajouter à `result` (donc à `$GITHUB_OUTPUT` via `emit_github_output`, `:267`) :
`mod_version`, `series`, `minecraft_range`. Toutes sans newline, la forme `key=value` de
`emit_github_output` reste valable. Pas besoin d'exporter la borne précédente : elle vit
désormais dans `.mc-update-state.json`.

---

## 2. `build.gradle`

- ~~`tasks.jar` → `tasks.remapJar`~~ — **faux, abandonné.** `docs/ci-plan.md` affirmait que
  `tasks.jar` produisait le jar dev ; vérification faite sur la vraie JVM,
  `tasks.findByName("remapJar")` vaut `null` et aucune tâche ne contient « remap ». Minecraft 26.x
  étant non obfusqué, Loom n'a rien à remapper et n'enregistre jamais la tâche. Le jar de `:jar`
  porte déjà `Fabric-Mapping-Namespace: official`. Les deux publications restent sur `tasks.jar`.
- **`:94`** : `project.supported_minecraft_versions.split(",")*.trim().findAll { it }` — en Groovy
  `"".split(",")` vaut `[""]`, donc après un reset de série `gameVersions` partait à `[""]`.
- Faire échouer `modrinth` et `publishCurseForge` dans un `doFirst` si la liste est vide. **Pas** à
  la configuration : ça casserait `./gradlew build` en CI (même piège que celui déjà contourné
  `:111` pour `curseforge_id`).

---

## 3. `.github/workflows/check-new-minecraft.yml`

Cron déjà correct (`0 6 * * 3`, mercredi 06:00 UTC). Ajouter `issues: write` aux `permissions`
(nécessaire pour l'issue **et** pour `gh label create`, que `peter-evans` créait implicitement).

Remplacer `peter-evans/create-pull-request@v7` (`:191-205`) par du git + `gh` explicite
(préinstallé sur `ubuntu-latest`), pour obtenir un assigné et deux commits lisibles.

| étape | condition | contenu |
|---|---|---|
| `update` | — | inchangée (`:39-59`) |
| **`commit-bump`** *(nouveau)* | `status == 'updated'` | `git config` bot → `git checkout -B chore/mc-$MC` → `git add gradle.properties src/main/resources/fabric.mod.json` → commit `chore(mc): bump to Minecraft $MC` |
| `build` | idem | `set -o pipefail; ./gradlew build --stacktrace 2>&1 \| tee build.log`, `continue-on-error` |
| `smoke` | build OK | inchangée |
| `mark` (`:108`) | `status == 'updated'` **et** build **et** smoke OK | `--mark-supported` puis `git add gradle.properties && git diff --cached --quiet \|\| git commit -m "chore(mc): validate Minecraft $MC (build + headless server)"` — le garde `status == 'updated'` manque aujourd'hui, l'étape ne tient que par l'`outcome == 'skipped'` des précédentes |
| **`revert`** *(nouveau)* | `status == 'updated'` **et** (build **ou** smoke KO) | `--revert-compat` + `git add -A` sur les 2 fichiers + commit `chore(mc): revert unproven compatibility for $MC` |
| `artifact` | `status == 'updated'` | ajouter `build.log` aux `path` |
| `body` | idem | ajouter `mod_version` et `depends.minecraft` au tableau ; la checklist manuelle perd la ligne `mod_version` (désormais auto, sauf `1.1.0`) |
| **`push-pr`** *(nouveau)* | idem | voir ci-dessous |
| **`issue`** *(nouveau)* | `status == 'updated'` **et** (build **ou** smoke KO) | voir ci-dessous |

**`push-pr`** (`env: GH_TOKEN: ${{ github.token }}`) :

```
git push --force origin "chore/mc-$MC"        # branch may survive a previous run
gh label create minecraft-update --color ededed --force
PR=$(gh pr list --head "chore/mc-$MC" --state open --json number -q '.[0].number')
si PR : gh pr edit  "$PR" --body-file pr-body.md --add-assignee Spatulox
        gh pr ready "$PR" [--undo si draft]
sinon : gh pr create --base master --head "chore/mc-$MC" --title ... --body-file pr-body.md \
                     --label minecraft-update --assignee Spatulox [--draft]
```

**`issue`** — titre déterministe pour la déduplication :
`[auto] Minecraft $MC: the mod does not work` (même titre que le build ou le serveur ait cassé ;
le corps précise l'étape). Corps : étape fautive, tableau des versions résolues, `tail -n 100` de
`build.log` **ou** de `server-test.log` selon le cas, lien vers le run et ses artifacts, lien vers
la PR draft, et mention que la borne de compat a été restaurée.

```
EXISTING=$(gh issue list --state open --search "in:title \"[auto] Minecraft $MC\"" --json number -q '.[0].number')
si EXISTING : gh issue comment "$EXISTING" --body-file issue-body.md
sinon       : gh issue create --title "..." --body-file issue-body.md \
                              --assignee Spatulox --label minecraft-update
```

**À écrire en commentaire dans le workflow** : une PR ouverte avec le `GITHUB_TOKEN` par défaut ne
déclenche pas les workflows `pull_request`, donc `ci.yml` ne tournera **pas** sur cette PR — sans
gravité, le workflow build et smoke-teste déjà lui-même. Nécessite aussi
*Settings → Actions → Allow GitHub Actions to create and approve pull requests*.

---

## 4. `.github/workflows/tag-mc-support.yml` (nouveau)

```yaml
on:
  push: { branches: [master], paths: [gradle.properties] }
  workflow_dispatch:
permissions: { contents: write }
```

`actions/checkout@v4` avec `fetch-depth: 0` (les tags sont nécessaires) → lire `mod_version` →
`TAG="v$MOD_VERSION"` → si `git rev-parse -q --verify "refs/tags/$TAG"` réussit, sortir sans rien
faire (idempotent, rejouable) → sinon tag annoté sur le SHA de master, message reprenant
`minecraft_version`, `supported_minecraft_versions`, `depends.minecraft`, `loader_version`,
`fabric_api_version`, puis `git push origin "$TAG"` et résumé dans `$GITHUB_STEP_SUMMARY`.

Critère = `mod_version` et non « la liste de compat a grandi » : c'est `mod_version` que porte le
tag, donc le seul champ qui garantisse un nom unique — et ça couvre en prime les releases manuelles
(`1.1.0` → `1.2.0` sans changement MC). **Limite assumée** : un bump de patch (`26.1.1` → `26.1.2`)
ne change pas la série, donc pas de nouveau tag ; `supported_minecraft_versions` et
`depends.minecraft` en gardent la trace.

---

## 5. `.gitignore` et docs

- Ajouter `build.log`, `issue-body.md` et `.mc-update-state.json` (`server-test.log` et `pr-body.md`
  y sont déjà).
- Docstring de `scripts/update-mc-version.py` : documenter `--run-tests`, `--revert-compat`, le
  modèle de série, et retirer la mention « et fabric.mod.json si le major change ».
- `README.md` : une section « Mettre à jour Minecraft » avec la commande locale unique.
- En-tête de `.github/scripts/headless-server-test.sh` : documenter `STOP_TIMEOUT` et
  `EXPECTED_POTIONS`, non mentionnés aujourd'hui.

---

## 6. Passage à l'anglais

Tout le code et le texte généré passent en anglais, y compris l'existant, pour ne laisser aucun
fichier mi-français mi-anglais :

| fichier | à traduire |
|---|---|
| `scripts/update-mc-version.py` | docstring de module, docstrings de fonctions, commentaires, textes `argparse` (`description`, `help`), messages `Failure`, tout ce que `log()` imprime |
| `.github/workflows/check-new-minecraft.yml` | `name:` du workflow et de chaque étape, commentaires, `$GITHUB_STEP_SUMMARY`, **corps de PR** (`pr-body.md` : verdict, tableaux, checklist manuelle) |
| `.github/workflows/ci.yml` | `name:` du job et des étapes |
| `.github/scripts/headless-server-test.sh` | en-tête d'usage, commentaires, `echo` de progression, messages de `fail()` |
| nouveau code | issue (titre + corps), messages de commit, `--run-tests`, `--revert-compat` |

Les messages de commit générés passent en anglais également (`chore(mc): bump to Minecraft X`,
`chore(mc): validate Minecraft X (build + headless server)`,
`chore(mc): revert unproven compatibility for X`).

Ce plan et la section README restent en français.

---

## Vérification

**Script, sans JVM** (sur une copie du dépôt, `rsync` hors `build/ run/ .git`) :

```bash
python3 scripts/update-mc-version.py 26.1.3 --dry-run --json
#   depends.minecraft  -> ">=26.1 <=26.1.3"   (serie inchangee)
#   mod_version        -> 26.1-1.1.0          (inchange)
#   supported          -> 26.1,26.1.1,26.1.2  (conserve, pas encore prouve)

python3 scripts/update-mc-version.py 26.2 --dry-run --json
#   depends.minecraft  -> "=26.2"             (nouvelle serie, sans sous-version)
#   mod_version        -> 26.2-1.1.0
#   supported          -> vide                (RESET de serie)

python3 scripts/update-mc-version.py 99.9 --dry-run; echo $?    # 2, pas 1
```

Séquence complète hors dry-run, pour voir la borne monter version par version :

```bash
python3 scripts/update-mc-version.py 26.2 && python3 scripts/update-mc-version.py --mark-supported
grep supported_minecraft_versions gradle.properties     # 26.2
python3 -c "import json;print(json.load(open('src/main/resources/fabric.mod.json'))['depends']['minecraft'])"
#   '=26.2'   — une chaine, jamais une liste

python3 scripts/update-mc-version.py 26.2.1 && python3 scripts/update-mc-version.py --mark-supported
#   supported -> 26.2,26.2.1   |   depends -> '>=26.2 <=26.2.1'
```

**Revert (1f)** — le point le plus important à vérifier :

```bash
git stash && python3 scripts/update-mc-version.py 26.2.2       # etat AVANT : 26.2,26.2.1 / '>=26.2 <=26.2.1'
cat .mc-update-state.json                                      # target 26.2.2 + les 2 valeurs d'avant
python3 scripts/update-mc-version.py --revert-compat
grep supported_minecraft_versions gradle.properties            # 26.2,26.2.1        (restaure)
python3 -c "import json;print(json.load(open('src/main/resources/fabric.mod.json'))['depends']['minecraft'])"
#   '>=26.2 <=26.2.1'   (restaure)
grep minecraft_version= gradle.properties                      # 26.2.2 — le BUMP est conserve
ls .mc-update-state.json                                       # absent
python3 scripts/update-mc-version.py --revert-compat; echo $?  # 1, "no update state to revert"
```

**Panne réseau (1a)** : pointer temporairement `FABRIC_META` sur une URL renvoyant 500 → sortie **1**
avec `HTTP 500`, plus l'exit 2 trompeur d'aujourd'hui.

**Loader (1b)** : `curl -s https://meta.fabricmc.net/v2/versions/loader/26.1.2 | head` et vérifier que
la valeur retenue est bien le premier `loader.stable == true` de cette liste, pas le dernier stable global.

**Build + serveur (nécessite une JVM)** :

```bash
python3 scripts/update-mc-version.py --run-tests     # chemin local complet
unzip -p build/libs/*.jar META-INF/MANIFEST.MF | grep Fabric-Mapping-Namespace   # official
unzip -p build/libs/*.jar fabric.mod.json | grep -A2 '"depends"'
EXPECTED_POTIONS=999 bash .github/scripts/headless-server-test.sh; echo $?       # 1 — le test garde ses dents
```

**Workflows** :

- `check-new-minecraft.yml` en `workflow_dispatch`, `minecraft_version=26.2` + `force=true`.
  Attendu : branche `chore/mc-26.2` à **deux** commits, PR assignée à `Spatulox`, label
  `minecraft-update`, non-draft si tout est vert. Relancer le même dispatch → la PR est mise à jour,
  pas dupliquée.
- Chemin d'échec : dispatch avec `EXPECTED_POTIONS=999` injecté, ou une version MC connue pour
  casser. Attendu : PR **draft**, commit 2 = revert de la compat, `fabric.mod.json` de la branche
  revenu à `">=26.1 <=26.1.2"` et `supported_minecraft_versions` à sa valeur d'avant, tandis que
  `minecraft_version` / `loader_version` / `fabric_api_version` restent bumpés,
  **issue ouverte assignée à Spatulox** avec le tail du log. Relancer → commentaire sur l'issue
  existante, pas de doublon.
- `tag-mc-support.yml` : après merge, `git fetch --tags && git tag | grep v26.2-1.1.0`. Relancer en
  `workflow_dispatch` → « tag déjà existant », sans échouer.

---

## Non inclus (écarté ou hors sujet)

- **Tests unitaires du script** (`scripts/test_update_mc_version.py`) — non retenu. La vérification
  ci-dessus reste manuelle ; si l'envie revient, `compat_range` et `series_of` sont des fonctions
  pures, testables sans réseau.
- **Robustesse locale de `headless-server-test.sh`** — non retenu, mais à savoir puisque
  `--run-tests` l'appelle : il **écrase** `run/server.properties` et `run/eula.txt` sans sauvegarde
  (`:50-64`), et `grep -c` (`:26`) tue le script sous `set -e` si le compte de potions tombe à 0.
- `fabric.mod.json` encore au template d'exemple (`description`, `authors: ["Me!"]`, homepage
  `fabricmc.net`, `license: CC0-1.0`, `suggests: another-mod`) — c'est ce qui part dans le jar publié.
- `curseforge_id=extended-time-potion` non numérique → `publishCurseForge` désactivé
  (`build.gradle:111`) : `publishMod` ne publie que sur Modrinth, en silence.
- `WorldReloadGameTest` (`runClientGameTest`) n'est lancé par aucun workflow — test mort, il faudrait
  `xvfb-run`.
- Actions non épinglées (`@v4`, `@v7`) et `minotaur "2.+"` — build non reproductible.
- ~~`loom_version=1.17-SNAPSHOT` non résolu automatiquement~~ — **fait** : `latest_stable_loom()`
  lit `maven-metadata.xml` de fabric-loom, écarte tout ce qui n'est pas purement numérique
  (`-alpha.`, `-SNAPSHOT`) et trie numériquement, car le métadata est en ordre de *publication*
  (`1.17.14` y apparaît après `1.18.0-alpha.4`). `--loom <version>` épingle. Loom n'ayant pas de
  table « quel loom builde quel Minecraft », c'est la dernière stable qui est prise ; en cas
  d'échec, le build casse, la PR reste en draft et l'issue est ouverte.
- Le job d'update reste vert même quand le build échoue (`continue-on-error`) ; l'issue assignée
  compense désormais, mais un ❌ serait plus lisible.
