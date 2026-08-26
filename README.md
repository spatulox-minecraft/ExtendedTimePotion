# Extented Time Potion

This simple mod allows you to create certain potions with an extended duration of use.

Take the potion with the max vanilla time : 8:00 (crafted with redstone).

Now you can add an extra golden nugget to extend to time up to 11:00 minutes !
If you add a golden carrot to this extend potion, you can bring it up to 15:00 !

Have fun !
- https://modrinth.com/mod/extended_time_potion
- https://www.curseforge.com/minecraft/mc-mods/extended-time-potion

## La CI

Toute la mécanique de CI vit dans [mc-bump](https://github.com/spatulox-minecraft/mc-bump), un dépôt
partagé entre les mods. Ici il ne reste que **`.github/mc-bump.yml`** — la seule chose qui décrit ce
mod en particulier (son id, ses fichiers de métadonnées, ce qu'un serveur sain doit imprimer au
démarrage, les stores où il est publié) — et trois workflows d'une dizaine de lignes qui appellent
les pipelines partagés :

| workflow | appelle | quand |
|---|---|---|
| `ci.yml` | `mc-bump/.github/workflows/ci.yml` | push sur `master`, PR |
| `check-new-minecraft.yml` | `mc-bump/.github/workflows/auto-update.yml` | mercredi 06:00 UTC, ou à la main |
| `release.yml` | `mc-bump/.github/workflows/release.yml` | merge sur `master` touchant `gradle.properties` |

Ajouter une potion, changer un message de log : ça se règle dans `.github/mc-bump.yml`. Changer la
façon dont un serveur est testé : ça se règle dans mc-bump, et tous les mods en profitent.

Sur chaque PR, la CI lance trois choses indépendantes, une par job, pour qu'un rouge se nomme
lui-même : les tests unitaires JUnit, la **matrice de versions** (build + démarrage d'un serveur,
**un job par version annoncée**, en parallèle) et le gametest client sous xvfb — déclaré non
bloquant, mais il rapporte quand même. Un échec ouvre (ou complète) une issue avec un bloc repliable
par job rouge.

## Mettre à jour Minecraft

Tous les mercredis, la chaîne passe à la dernière version de Minecraft, build, lance un serveur
headless sur chaque version annoncée et ouvre une PR assignée. Si le mod ne fonctionne pas sur la
nouvelle version, une issue est ouverte avec les logs.

**Une seule variable bouge : Minecraft.** `loader_version` et `fabric_api_version` restent **gelés**
sur la valeur déjà dans `gradle.properties`, identique pour toutes les sous-versions d'une série.
Les bumper en même temps que Minecraft rendait une matrice rouge illisible — trois suspects, et le
loader comme l'API changent le comportement de *toutes* les sous-versions d'un coup.

Ils ne bougent donc qu'en **escalade**, en réaction à un échec, un à la fois, chacun suivi d'une
matrice complète (`mc-bump/scripts/test-with-escalation.py`) :

```
matrice avec les dépendances gelées
  └─ rouge → bump fabric-api → matrice complète
       └─ rouge → bump loader → matrice complète
            └─ rouge → échec : compat annulée, PR draft, issue
```

Une escalade qui corrige est gravée dans `fabric.mod.json` en plancher de dépendance
(`"fabric-api": ">=0.156.1+26.2"`), pour qu'un joueur sur une version plus ancienne soit invité à
mettre à jour au lieu de crasher. Sans escalade, `"fabric-api"` garde son `"*"`.

`loom_version` échappe au gel : c'est le plugin de build, pas une dépendance du jar, et il suit la
dernière version **stable** de fabric-loom. Si un build casse dans loom lui-même, on l'épingle avec
`--loom <version>` (utile pour une vieille version de Minecraft qu'un loom récent ne sait plus
builder).

En local, avec mc-bump cloné **à côté** de ce dépôt, la même séquence tient en une commande :

```bash
MCB=../mc-bump

python3 $MCB/scripts/mc-bump.py --run-tests          # dernière release Mojang
python3 $MCB/scripts/mc-bump.py 26.2 --run-tests     # version précise
```

Elle met à jour `gradle.properties` et `fabric.mod.json`, puis lance
`$MCB/scripts/test-with-escalation.py` — qui build et démarre un serveur pour **chaque** version
annoncée, pas seulement la plus récente, et escalade les dépendances gelées si besoin. La
compatibilité n'est annoncée que si tout passe ; sinon les bornes précédentes sont restaurées, en
conservant les bumps de dépendances.

Les autres entrées, toutes lançables depuis ce dépôt sans GitHub :

```bash
python3 $MCB/scripts/mc-bump.py --list-test-versions   # ce que la matrice va démarrer
python3 $MCB/scripts/headless-server-test.py           # un seul serveur
python3 $MCB/scripts/test-matrix.py                    # chaque version annoncée
PYTHONPATH=$MCB python3 -m lib.config --json           # la config, une fois résolue
```

### Déclencher la mise à jour à la main

Depuis GitHub : onglet **Actions** → *New Minecraft version* → **Run workflow**. Le menu déroulant
permet de choisir la branche, et deux champs sont proposés : `minecraft-version` (vide = dernière
release Mojang) et `force` (continuer même si le dépôt est déjà sur cette version).

En ligne de commande, si `gh` est installé :

```bash
gh workflow run check-new-minecraft.yml --ref master
gh workflow run check-new-minecraft.yml --ref master -f minecraft-version=26.2 -f force=true
gh run watch
```

Deux prérequis :

- le workflow doit exister sur **`master`** : GitHub n'expose ni `workflow_dispatch` ni `schedule`
  pour un fichier absent de la branche par défaut. Une fois qu'il y est, on peut le lancer depuis
  n'importe quelle branche (c'est la version du fichier de cette branche qui s'exécute) ;
- *Settings → Actions → General* → **Allow GitHub Actions to create and approve pull requests**,
  sans quoi l'étape d'ouverture de PR échoue.

La PR est ouverte vers la branche depuis laquelle le workflow a été lancé, pour qu'un essai depuis
une branche de travail ne produise pas une PR contenant tout le diff de cette branche.

`python3 $MCB/scripts/mc-bump.py --help` liste les autres modes (`--dry-run`, `--json`,
`--mark-supported`, `--revert-compat`, et les deux barreaux de l'escalade `--bump-fabric-api` /
`--bump-loader`, qui sortent en code 3 quand il n'y a rien de plus récent).

Les plans dans `docs/` (`mc-update-plan.md`, `frozen-deps-escalation-plan.md`, `mc-bump-plan.md`)
racontent comment ce modèle de compatibilité a été construit. Ils décrivent l'implémentation
d'**avant** la bascule vers mc-bump, quand les scripts vivaient dans ce dépôt : le raisonnement
reste valable, les chemins de fichiers non.

## Publier une version

Rien à lancer à la main : `.github/workflows/release.yml` publie sur Modrinth et CurseForge dès
qu'un merge sur `master` touche `gradle.properties` avec un `mod_version` jamais sorti. Il enchaîne :

1. **`check`** — le tag `v<mod_version>` n'existe pas encore, `minecraft_version` figure bien dans
   `supported_minecraft_versions`, et les deux secrets sont non vides. Le garde-fou du milieu refuse
   de publier une compatibilité que la matrice n'a jamais prouvée, cas d'une PR *draft* mergée à la
   main ; celui des secrets coûte deux secondes ici au lieu d'une découverte après 90 minutes de
   matrice.
2. **`matrix`** — build + serveur pour chaque version annoncée, un job par version.
3. **`publish-modrinth`** et **`publish-curseforge`** — **un job par store**, indépendants.
4. **`tag`** — le tag, posé **après** que chaque store est fini.

Le tag signifie donc « version sortie », et non plus « version mergée ». C'est ce qui rend un échec
rattrapable : publication ratée → pas de tag → relancer le workflow réessaie.

Chaque store pose son propre tag marqueur `published/<store>/v<mod_version>` juste après son upload,
et une relance saute ce qui est déjà en ligne. C'est ce qui corrige l'ancien piège : avec un
`publishMod` unique, un Modrinth réussi suivi d'un CurseForge raté laissait le dépôt sans tag, et
toute relance mourait sur « version already exists » côté Modrinth.

Un lancement à la main propose `dry-run` : tout se déroule sauf les uploads et le tag.

Deux secrets sont requis dans *Settings → Secrets and variables → Actions* :

| secret | où l'obtenir |
|---|---|
| `MODRINTH_TOKEN` | Modrinth → Settings → PATs, portée *Create versions* |
| `CURSEFORGE_TOKEN` | CurseForge → My Account → API Tokens |

### Le changelog publié

Par ordre de priorité :

1. `-Pchangelog="..."` s'il est passé ;
2. l'entrée `## ` en tête de `CHANGELOG.md` **si elle nomme exactement le `mod_version` publié** —
   c'est ainsi qu'on écrit des notes de version à la main ;
3. sinon un texte généré depuis `gradle.properties`, ce que publient les bumps automatiques.

Une entrée périmée est donc ignorée plutôt que collée à la mauvaise version. Pour voir le texte
exact avant de merger :

```bash
./gradlew printChangelog
```
