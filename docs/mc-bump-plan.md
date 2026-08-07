# Extraire la CI dans `mc-bump`, la rendre configurable et prête pour NeoForge

## Contexte

La CI d'`ExtendedTimePotion` est bonne sur le fond — source de vérité unique pour les
versions (`scripts/update-mc-version.py`), scripts relançables en local, échelle
d'escalade des dépendances, ordre publish→tag correct — mais **elle est fusionnée avec
ce mod précis**. L'identité du mod est éparpillée dans six fichiers : chemin de la classe
principale et `grep -c '= registerPotion('` (`headless-server-test.sh:39-41`), id du mod
grepé dans le log (`:136`), marqueurs métier (`:166`, `:170`), `Spatulox` /
`minecraft-update` / `chore/mc-` (`check-new-minecraft.yml:31,397,410,505`), user-agent et
chemin du mixins (`update-mc-version.py:134,145`), `extended-time-potion-test`
(`build.gradle:40`). Réutiliser cette CI sur un autre mod veut dire relire et éditer les
six.

S'y ajoutent des trous réels : **aucun test Java** (pas de JUnit dans `build.gradle`, donc
`./gradlew build` exécute `test` avec zéro test et passe au vert), un `src/gametest` qui ne
compile plus depuis 26.2 sans qu'aucun workflow ne le remarque, un `ci.yml` qui ne teste
qu'une version quand `release.yml` en teste trois, une matrice séquentielle dans un seul
job, des secrets de publication jamais vérifiés avant 90 min de matrice, et un `publishMod`
non idempotent qui **bloque définitivement** la chaîne si Modrinth réussit et CurseForge
échoue.

**Résultat visé** : un repo `mc-bump` (`git@github.com:spatulox-minecraft/mc-bump.git`,
branche `main`, aujourd'hui un seul commit avec un README vide) qui porte toute la CI sous
forme de workflows réutilisables + action composite ; le repo du mod ne garde qu'un fichier
de config et trois workflows appelants de dix lignes. Le pilotage Fabric est isolé derrière
une interface pour qu'un `neoforge.py` puisse être ajouté par duplication.

## Décisions actées

| Sujet | Décision |
|---|---|
| Migration | **Cherry-pick** de l'historique CI d'ExtendedTimePotion vers mc-bump. ExtendedTimePotion **garde son code actuel** ; la suppression et le remplacement par les refs `mc-bump` feront l'objet d'un commit ultérieur, hors de ce plan. |
| Publish partiel | Modrinth et CurseForge en **deux jobs séparés**, chacun vérifiant d'abord si la version est déjà en ligne et se skippant le cas échéant. Tag posé quand les deux sont OK ou skipped. |
| Gametest client | Job **séparé, activé, non bloquant** (`continue-on-error`). Il ne compile plus en 26.2 : il sera donc rouge-non-bloquant jusqu'à réparation, ce qui est le but (le rendre visible). Corollaire : **ne pas** câbler `check.dependsOn compileGametestJava`, cela rendrait le build entier rouge et contredirait le « non bloquant ». |
| Portée | Fabric uniquement pour l'instant. NeoForge = un `lib/loaders/neoforge.py` à écrire plus tard ; ce plan livre la couture, pas l'implémentation. |
| Échec d'un test | Qu'il s'agisse des tests unitaires, du serveur headless ou du gametest : la branche créée est **conservée**, et une **issue** assignée est ouverte, avec les logs de chaque job en échec dans un bloc `<details><summary>[TITRE TEST ECHOUE]</summary></details>`. |

---

## Phase 0 — Amorcer `mc-bump` avec l'historique (à faire en premier)

Rejouer l'historique de `.github/` et `scripts/` d'ExtendedTimePotion dans mc-bump.
17 commits les touchent, dont beaucoup touchent aussi d'autres fichiers : on ne rejoue pas
les diffs (conflits garantis) mais **l'état de ces deux répertoires à chaque commit**, ce
qui ne peut pas conflictuer.

```bash
cd ~/Documents/projetcs/mc-bump
git remote add etp ../ExtendedTimePotion
git fetch etp

for sha in $(git --no-pager log --reverse --no-merges --format=%H etp/master -- .github scripts); do
    git rm -rq --ignore-unmatch .github scripts
    git checkout "$sha" -- .github scripts 2>/dev/null || true
    git add -A .github scripts
    git commit -q --allow-empty \
        -m "$(git log -1 --format=%B "$sha")" \
        -m "(imported from ExtendedTimePotion $sha)" \
        --author="$(git log -1 --format='%an <%ae>' "$sha")" \
        --date="$(git log -1 --format=%aI "$sha")"
done

git remote remove etp
```

Vérifier ensuite que `HEAD` reproduit à l'identique l'état actuel :

```bash
diff -r ~/Documents/projetcs/mc-bump/.github ~/Documents/projetcs/ExtendedTimePotion/.github
diff -r ~/Documents/projetcs/mc-bump/scripts ~/Documents/projetcs/ExtendedTimePotion/scripts \
     --exclude=__pycache__
```

Les commits suivants (restructuration) sont des commits normaux dans mc-bump.
**ExtendedTimePotion n'est pas modifié en Phase 0.**

---

## Phase 1 — Structurer `mc-bump`

### Arborescence cible

```
mc-bump/
├── action.yml                     # action composite : expose les scripts
├── README.md                      # les deux modes de consommation + schéma de config
├── .github/workflows/
│   ├── ci.yml                     # on: workflow_call
│   ├── auto-update.yml            # on: workflow_call
│   ├── release.yml                # on: workflow_call
│   └── self-test.yml              # on: push — mc-bump se teste lui-même
├── lib/
│   ├── config.py                  # lecteur unique : --sh | --json | --github-output
│   ├── versions.py                # série, bornes, labels, gabarit de mod_version
│   ├── report.py                  # corps de PR et corps d'issue
│   └── loaders/
│       ├── base.py                # interface Loader (ABC)
│       ├── fabric.py              # TOUT le Fabric vit ici
│       └── __init__.py            # get_loader(name) -> Loader
├── scripts/
│   ├── mc-bump.py                 # ex update-mc-version.py, CLI
│   ├── headless-server-test.sh
│   ├── test-matrix.sh
│   └── test-with-escalation.sh
└── tests/
    ├── test_versions.py
    ├── test_config.py
    └── test_fabric.py             # ex test_update_mc_version.py, éclaté
```

### La couture loader — le point structurant

`lib/loaders/base.py` définit une ABC ; `fabric.py` en est la seule implémentation
aujourd'hui, `neoforge.py` sera sa duplication.

```python
class Loader(ABC):
    name: str
    gradle_keys: dict          # {"loader": "loader_version", "api": "fabric_api_version",
                               #  "buildtool": "loom_version"}
    def supports(mc) -> bool               # sinon exit 2 "pas encore supporté"
    def resolve(mc) -> Resolved            # loader / api / buildtool
    def render_range(low, high) -> str     # ">=26.1 <=26.1.2"  vs  "[1.21,1.22)"
    def write_metadata(root, resolved, rng)# fabric.mod.json  vs  neoforge.mods.toml
    def escalation_rungs() -> list         # [("fabric_api_version", "--bump-fabric-api"),
                                           #  ("loader_version", "--bump-loader")]
    def server_task() -> str               # "runServer"
    def mod_loaded_pattern(mod_id) -> str  # regex prouvant le chargement réel
    def store_loader_name() -> str         # "fabric" / "neoforge" pour Modrinth & CF
```

Ce qui **reste générique** dans `lib/versions.py` : manifeste Mojang (liste des versions,
`java_version`), `series_of`, `parse_version`, `mc_label`, calcul des bornes basses/hautes
de compatibilité, gabarit de `mod_version`. Seul le **rendu** de la plage est loader-spécifique.

Ce qui **part dans `fabric.py`** : `meta.fabricmc.net`, la résolution de `fabric-api` via
Modrinth, `maven.fabricmc.net` pour loom, l'écriture de `fabric.mod.json` et du
`compatibilityLevel` du mixins.json, les barreaux d'escalade.

Les trois `.sh` restent **loader-agnostiques** : ils lisent leur comportement via
`eval "$(python3 lib/config.py --sh)"`, qui expose déjà la tâche gradle et les patterns
résolus par le loader.

### Fichier de config du mod : `.github/mc-bump.yml`

```yaml
loader: fabric

mod:
  id: extended-time-potion
  package: com.spatulox
  metadata: src/main/resources/fabric.mod.json
  mixins: src/main/resources/extended-time-potion.mixins.json

workflows:            # pilote quels jobs tournent
  ci: true
  auto-update: true
  release: true
  unit-tests: true
  gametest:
    enabled: true
    blocking: false   # décision actée

version:
  format: "{mc}-{mod}"      # alt. "{mod}+mc{mc}", "{mod}-{mc}", "{mod}"
  tag: "v{version}"

tests:
  unit:
    source: src/test/java
    task: test
    require-non-empty: true   # un src/test vide = rouge, pas un faux vert
  matrix:
    enabled: true
    parallel: true            # fan-out GitHub plutôt que boucle bash
  server:
    boot-timeout: 900
    stop-timeout: 60
    expect:                   # simple présence, N phrases
      - pattern: "Brewing mixes registered"
        message: "the Fabric API brewing callback never ran"
    expect-count:             # généralise le compte de potions
      - pattern: "Registered ([0-9]+) potions"
        count-source: src/main/java/com/spatulox/ExtendedTimePotion.java
        count-pattern: "= registerPotion("
        message: "potion count mismatch"
    fatal-extra: []           # s'ajoute aux signatures génériques du script

release:
  stores: [modrinth, curseforge]
  branch-prefix: chore/mc-
  artifact-retention-days: 30

notify:                   # rapport d'échec, commun aux trois types de tests
  assignee: Spatulox
  label: ci-failure       # l'auto-update garde en plus son label minecraft-update
  keep-branch: true
  on-pull-request: false  # cf. encadré ci-dessous
  log-tail: 100           # lignes de log par bloc <details>
```

Les patterns fatals actuels (`headless-server-test.sh:155`) sont génériques à tout mod
Fabric : ils **restent le défaut du script**, `fatal-extra` ne fait qu'ajouter.

`lib/config.py` : charge, valide contre un schéma explicite (message d'erreur nommant la
clé fautive), applique les défauts, et émet soit des exports shell, soit du JSON, soit des
lignes `$GITHUB_OUTPUT`. **PyYAML** est requis ; présent localement (6.0.1) et sur les
runners `ubuntu-latest`, mais `action.yml` fait un `pip install --quiet pyyaml` idempotent
pour ne pas dépendre de l'image.

### Point d'attention : `workflows.ci: false` ne peut pas empêcher un run

GitHub évalue le `on:` avant de lire quoi que ce soit. Le flag est donc appliqué par un
premier job `guard` dont tous les autres dépendent via
`if: needs.guard.outputs.enabled == 'true'` : le run apparaît, en *skipped*. C'est la seule
façon de garder une config unique.

---

## Phase 2 — Workflows réutilisables, jobs séparés

### `ci.yml` (`on: workflow_call`)

| Job | Dépend de | Rôle |
|---|---|---|
| `guard` | — | lit `.github/mc-bump.yml`, sort `enabled`, la liste JSON des versions, `java`, l'id du mod. Pas de JDK, quelques secondes. |
| `unit-tests` | `guard` | `./gradlew test` + upload de `build/reports/tests/` en `if: always()`. Échec si `require-non-empty` et aucun test. |
| `matrix` | `guard` | `strategy.matrix.mc: ${{ fromJson(needs.guard.outputs.versions) }}` — un job par version, build + `headless-server-test.sh`. Remplace la boucle bash séquentielle : trois versions en parallèle au lieu de 40 min en série. |
| `gametest` | `guard` | `continue-on-error: true`, `xvfb-run ./gradlew runClientGameTest`. Upload des screenshots. |
| `verdict` | `unit-tests`, `matrix` | agrège et échoue si l'un des deux est rouge (`gametest` exclu, non bloquant). |

Emplacement des sources — imposé par Loom, les deux jobs ne peuvent pas partager un
répertoire : `src/test/java` pour JUnit (JVM nue, `./gradlew test`) et `src/gametest/java`
pour le gametest client (source set créé par `fabricApi.configureTests`,
`build.gradle:38-47`, lancé dans un vrai client). Ils sont voisins et déclarés dans le même
bloc de config, mais restent deux source sets.

Corrige au passage l'incohérence de fond : `ci.yml:53` ne teste aujourd'hui qu'une version
alors que `depends.minecraft` en promet trois.

### Rapport d'échec commun aux trois types de tests

Un job `report-failure` partagé, `if: always() && contains(needs.*.result, 'failure')`,
branché en fin de `ci.yml`, `auto-update.yml` et `release.yml`. Il généralise ce que fait
aujourd'hui `check-new-minecraft.yml:420-506`, qui ne couvre que la matrice de l'auto-update.

Ce qu'il fait :

1. **Garde la branche.** Aucun nettoyage de branche nulle part, et
   `notify.keep-branch: true` interdit explicitement d'en ajouter un plus tard. Dans
   l'auto-update, `chore/mc-<version>` est déjà poussée en `--force` avant les tests
   (`check-new-minecraft.yml:388`) : elle survit donc à un échec — comportement à préserver,
   pas à créer.
2. **Ouvre ou met à jour une issue.** Titre déterministe
   (`[auto] <workflow> — tests en échec sur <ref>`), assignee et label pris dans `notify:`.
   Un re-run **commente l'issue existante** au lieu d'en empiler une nouvelle — même idiome
   que `check-new-minecraft.yml:494-498`, qui filtre par titre exact plutôt que par
   `--search` (les crochets sont mal tokenisés par la syntaxe de recherche GitHub).
3. **Un bloc repliable par job en échec**, dans l'ordre de sévérité :

   ```markdown
   <details><summary>[TITRE TEST ECHOUE]</summary>

   ```
   … notify.log-tail dernières lignes du log du job …
   ```

   </details>
   ```

   Titres attendus : `Tests unitaires (JUnit)`, `Serveur headless — Minecraft 26.1.1`
   (un bloc **par version** de la matrice, puisque ce sont des jobs distincts),
   `Gametest client`.

Mécanique : chaque job de test, en `if: always()`, dépose un artefact
`failure-report-<job>` contenant `title` + le log. `report-failure` télécharge tous ces
artefacts et `lib/report.py` assemble le corps — la même fonction que celle qui génère déjà
le corps de PR, donc testable hors CI.

Le gametest est non bloquant mais **ouvre quand même une issue** : c'est précisément ce qui
le rend visible sans casser la CI.

> **Portée par défaut.** Ouvrir une issue à chaque PR rouge est bruyant, et les checks de
> la PR rapportent déjà l'échec. `notify.on-pull-request: false` limite donc le rapport aux
> déclencheurs `push`, `schedule` et `workflow_dispatch`. Mettre la clé à `true` si tu
> préfères l'issue systématique.

Conséquence : `permissions: issues: write` (et `contents: read`) sur les workflows qui
appellent ce job.

### `release.yml` (`on: workflow_call`)

| Job | Dépend de | Rôle |
|---|---|---|
| `check` | — | tag pas déjà posé + `minecraft_version ∈ supported_minecraft_versions` (logique actuelle `release.yml:51-90`) **+ présence des secrets des stores activés** (nouveau) + input `dry_run`. |
| `matrix` | `check` | fan-out par version, comme en CI. |
| `publish-modrinth` | `check`, `matrix` | skip si `published/modrinth/<tag>` existe (`git ls-remote --tags`) ; sinon `./gradlew modrinth`, puis pose le marqueur. |
| `publish-curseforge` | `check`, `matrix` | idem avec `published/curseforge/<tag>` et `./gradlew publishCurseForge`. |
| `tag` | les trois | pose `v<mod_version>` seulement si les deux jobs sont `success` **ou** `skipped`. |

**Pourquoi des tags marqueurs plutôt qu'un appel API.** L'idempotence doit valoir pour les
deux stores de la même façon. L'API Modrinth qui liste les versions est publique et sans
token, mais l'équivalent CurseForge exige une clé API *core*, distincte du token d'upload —
donc un secret de plus à créer et à faire tourner. Le marqueur `git ls-remote` ne coûte
aucun secret, fonctionne à l'identique des deux côtés, et réutilise l'idiome déjà présent
dans le repo (`release.yml:68` teste déjà l'existence d'un tag). Fenêtre résiduelle assumée :
upload réussi puis push du marqueur échoué → le re-run retentera l'upload et se prendra un
« version déjà existante ». Rare, visible, et récupérable à la main en poussant le marqueur.

C'est ce qui répare le blocage actuel : aujourd'hui `publishMod` dépend des deux tâches, et
un échec CurseForge après un succès Modrinth empêche pour toujours le workflow d'aboutir
(re-run → duplicata refusé par Modrinth → jamais de tag).

Ajouter la vérif des secrets dans `check` évite de découvrir un token expiré **après** 90
minutes de matrice.

### `auto-update.yml` (`on: workflow_call`)

Reprend `check-new-minecraft.yml`, avec :
- les ~250 lignes de bash inline générant les corps de PR et d'issue extraites dans
  `lib/report.py`, donc testable comme l'updater l'est déjà ; son issue d'échec devient un
  cas particulier de `report-failure` (elle garde son label `minecraft-update` et son
  tableau version-par-version en plus des blocs `<details>`) ;
- assignee / label / préfixe de branche lus dans la config ;
- les barreaux d'escalade fournis par le module loader ;
- la matrice qui **reste séquentielle ici** : l'escalade rejoue la matrice entière après
  chaque bump, c'est séquentiel par construction.

### `self-test.yml` (interne à mc-bump)

`shellcheck` sur les trois `.sh`, `actionlint` sur les workflows,
`python3 -m unittest discover tests`. Non négociable pour un repo qui *est* la CI : la
logique de test vit dans des scripts shell aujourd'hui jamais lintés.

### Consommation

`action.yml` composite — GitHub checkoute mc-bump dans `${{ github.action_path }}`, ce qui
résout le fait qu'un `workflow_call` checkoute le repo **appelant** et pas celui du
workflow :

```yaml
runs:
  using: composite
  steps:
    - shell: bash
      run: bash "${{ github.action_path }}/scripts/${{ inputs.command }}"
```

Deux niveaux documentés dans le README :
1. `uses: spatulox-minecraft/mc-bump/.github/workflows/ci.yml@v1` — clé en main ;
2. `uses: spatulox-minecraft/mc-bump@v1` avec `command:` — pour un pipeline sur mesure.

Versionnage : tag `v1` mobile + `v1.x.y` immuables.

---

## Phase 3 — Correctifs de fond, dans mc-bump

- **Gabarit de `mod_version`.** `update-mc-version.py:472-480` est en dur sur
  `split("-", 1)` et **abandonne en silence** (simple warning) si le `-` manque. Passer par
  le gabarit de config pour le parsing *et* le rendu, et faire d'un format non reconnu une
  **erreur**. Idem pour `v$MOD_VERSION` en dur (`release.yml:63`) → `version.tag`.
- **Preuve de chargement du mod.** `grep -q 'extended-time-potion'`
  (`headless-server-test.sh:136`) est trop faible : l'id apparaît dans le classpath, donc le
  test passe même si le mod n'est pas chargé. Viser la ligne de récap du loader, via
  `loader.mod_loaded_pattern()`.
- **Contrat scripts ↔ workflows.** `test-matrix-status.txt` et `test-escalation.txt` posés à
  la racine marchent en local mais sont fragiles dans un workflow appelé : les exposer aussi
  en `$GITHUB_OUTPUT`.
- **`permissions`** explicites : `contents: read` + `issues: write` sur `ci.yml` (aucun bloc
  aujourd'hui, donc permissions par défaut du dépôt).
- **Identité du repo.** `USER_AGENT` (`update-mc-version.py:134`) dérivé de
  `$GITHUB_REPOSITORY` ; chemin du mixins (`:145`) lu dans la config.
- **Input `dry_run`** sur le `workflow_dispatch` de release : tout sauf l'upload, pour
  valider la chaîne sans publier.

---

## Phase 4 — Côté `ExtendedTimePotion` (sans rien supprimer)

Les scripts et workflows actuels **restent en place** ; on ajoute seulement ce qui manque au
mod lui-même et qui a de la valeur immédiate.

### `build.gradle`

```groovy
dependencies {
    testImplementation platform("org.junit:junit-bom:5.11.4")
    testImplementation "org.junit.jupiter:junit-jupiter"
    testRuntimeOnly "org.junit.platform:junit-platform-launcher"
}
tasks.named("test") { useJUnitPlatform() }
```

Aujourd'hui il n'y a **aucune** dépendance de test : la tâche `test` tourne à vide et passe
au vert. Portée réelle à assumer : hors du runtime Loom les classes Minecraft ne sont pas
initialisées, donc `src/test` ne couvre que de la **logique pure** (durées, parsing,
helpers). Tout ce qui touche aux registres reste du ressort du gametest.

Ne **pas** ajouter `check.dependsOn compileGametestJava` — cf. décisions actées.

### Nouveaux fichiers

- `src/test/java/com/spatulox/…` — au moins un test réel de logique pure, sinon
  `require-non-empty` fait rouge à raison.
- `.github/mc-bump.yml` — la config ci-dessus. Elle peut être commitée dès maintenant : les
  workflows actuels l'ignorent, elle sera active à la bascule.

---

## Phase 5 — Bascule (hors périmètre, commit ultérieur du user)

Suppression de `.github/scripts/`, `scripts/` et du corps des trois workflows dans
ExtendedTimePotion, remplacés par trois appelants :

```yaml
name: CI
on: { push: { branches: [master] }, pull_request:, workflow_dispatch: }
permissions: { contents: read }
concurrency: { group: ci-${{ github.ref }}, cancel-in-progress: true }
jobs:
  ci:
    uses: spatulox-minecraft/mc-bump/.github/workflows/ci.yml@v1
    secrets: inherit
```

---

## Vérification

**Phase 0** — les deux `diff -r` ci-dessus doivent être vides, et
`git log --oneline | wc -l` dans mc-bump doit rendre le nombre de commits importés + 1.

**Phase 1** — `python3 -m unittest discover tests` dans mc-bump (les tests existants de
`test_update_mc_version.py` doivent passer après éclatement) ;
`python3 lib/config.py --sh` sur le `.github/mc-bump.yml` d'ExtendedTimePotion doit émettre
des exports exploitables ; retirer une clé obligatoire doit produire une erreur nommant la
clé.

**Phase 2** — sur une branche jetable d'ExtendedTimePotion, pointer les appelants sur
`mc-bump@main` et lancer `ci.yml` en `workflow_dispatch` : vérifier qu'on obtient bien
`guard` + N jobs `matrix` parallèles + `unit-tests` + `gametest` en rouge non bloquant, et
un `verdict` vert. Puis `release.yml` en `dry_run: true`.

**Rapport d'échec** — casser volontairement chacun des trois, un par un (un test JUnit qui
`fail()`, un `expect` de la config qui ne matchera jamais, le gametest déjà cassé), et
vérifier à chaque fois : la branche est toujours là, une issue est ouverte et assignée, et
son corps contient un `<details><summary>…</summary>` par job en échec. Relancer le même
workflow : l'issue existante doit être **commentée**, pas dupliquée. Puis casser deux jobs
en même temps pour vérifier qu'on obtient bien deux blocs dans une seule issue.

**Idempotence du publish** — depuis une version déjà publiée, relancer `release.yml` : les
deux jobs de publication doivent afficher *skipped* sur marqueur trouvé, et `tag` ne rien
faire. Puis supprimer le marqueur `published/curseforge/<tag>` et relancer : seul CurseForge
doit repartir.

**Localement, sans GitHub** — les trois `.sh` restent lançables :
`bash scripts/test-matrix.sh` depuis la racine du mod, avec `MC_VERSIONS` pour restreindre.
C'est la propriété la plus importante à ne pas casser en extrayant les scripts.
