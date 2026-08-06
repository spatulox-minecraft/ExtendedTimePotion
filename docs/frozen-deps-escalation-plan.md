# Geler loader / fabric-api, et ne les bumper qu'en escalade

## Contexte

Aujourd'hui, `scripts/update-mc-version.py` bumpe **quatre** versions d'un coup dès
qu'une nouvelle Minecraft sort : `minecraft_version`, `loader_version`,
`fabric_api_version` et `loom_version`. La matrice
(`.github/scripts/test-matrix.sh`) rejoue ensuite *toutes* les sous-versions de la
série (26.1, 26.1.1, 26.1.2) avec ce jeu de dépendances.

Problème : quand la matrice casse, on ne sait pas *quoi* a cassé. Un bump de Fabric
API ou du loader change le comportement de **toutes** les sous-versions à la fois,
alors que seule la version Minecraft était censée bouger. On mélange trois variables
dans une seule expérience.

**Objectif** : une seule variable bouge par défaut — Minecraft. Le loader et Fabric
API restent **figés** sur la valeur déjà présente dans `gradle.properties`, identique
pour toutes les sous-versions. Ils ne bougent qu'en **escalade**, quand la matrice est
rouge, et un par un :

```
matrice avec les deps figées
  └─ rouge → bump fabric_api_version → matrice complète (toutes les sous-versions)
       └─ rouge → bump loader_version → matrice complète
            └─ rouge → échec : --revert-compat, PR draft, issue
```

Si une escalade corrige, le job continue normalement **et** `fabric.mod.json` grave le
nouveau plancher : `"fabric-api": ">=0.156.1+26.2"` (version brute, suffixe `+mc`
compris). En cas d'échec final, les bumps restent sur la branche comme point de départ
d'un fix manuel, conformément à la philosophie actuelle ; seules les *claims* de
compat sont revert.

`loom_version` n'est pas concerné : c'est un plugin de build, pas une dépendance
runtime, il continue de suivre le dernier stable.

---

## 1. `scripts/update-mc-version.py`

### 1a. L'update par défaut n'écrit plus loader ni fabric-api

Dans `update_gradle_properties()` (~:502), supprimer les deux `set_property` sur
`loader_version` et `fabric_api_version`. La fonction ne touche plus que
`minecraft_version`, `loom_version`, `java_version`, `mod_version` et le reset de
série sur `supported_minecraft_versions`.

Dans `main()` (~:762-782), `loader_for(target)` et `latest_fabric_api(target)` sont
**conservés tels quels** comme sondes de disponibilité : ils gardent le `return
stop("unsupported", …, 2)` quand Fabric n'a encore rien publié pour la cible. Mais
leurs valeurs ne sont plus écrites. À la place :

- `result["loader_version"]` / `result["fabric_api_version"]` = les valeurs **lues
  dans `gradle.properties`** (les figées). Tous les consommateurs existants (message
  de commit, corps de PR, issue, `release.yml`) continuent donc d'afficher la vérité.
- deux nouvelles sorties `available_loader_version` / `available_fabric_api_version` =
  ce que Fabric propose, pour que la PR puisse dire « figé sur X, dernier dispo Y ».

Adapter les `log()` en conséquence (`loader_version = 0.19.3 (frozen, latest
available: 0.20.0)`).

### 1b. Deux nouveaux modes d'escalade

```
--bump-fabric-api   résout la dernière Fabric API pour le minecraft_version courant
                    et l'écrit dans gradle.properties
--bump-loader       idem avec le dernier loader stable
```

Une seule fonction partagée, paramétrée par (clé gradle, résolveur) :

- résout via `latest_fabric_api()` / `loader_for()` déjà présents, sur le
  `minecraft_version` **lu dans `gradle.properties`** (pas d'argument à passer) ;
- si la valeur résolue est identique à celle en place → **exit code 3** («  rien de
  plus récent, escalade inutile »), rien n'est écrit ;
- sinon, `set_property` + `write_preserving_final_newline`, exit 0 ;
- si Fabric ne publie rien → `Failure` (exit 1).
- **Ne touche pas à `fabric.mod.json`** : les planchers ne sont gravés qu'en cas de
  succès (§1c).
- Sorties : `status` (`bumped-fabric-api` / `bumped-loader` / `already-latest`),
  la nouvelle valeur et `previous_*`.

Ajouter les deux flags au bloc de mutuelle exclusion de `main()` (~:665) et le code 3
au docstring « Exit codes » (~:72).

### 1c. `mark_supported()` grave les planchers escaladés

`save_update_state()` (~:426) snapshotte déjà la compat pré-update. L'étendre avec
quatre clés : `loader_version`, `fabric_api_version`, `depends_fabric_api`,
`depends_fabricloader`.

`mark_supported()` (~:543), en plus de ce qu'elle fait déjà, compare
`gradle.properties` au snapshot :

- `fabric_api_version` a changé → `depends["fabric-api"] = ">=" + fabric_api_version`
  (**version brute**, `>=0.156.1+26.2`) ;
- `loader_version` a changé → `depends["fabricloader"] = ">=" + loader_version`.

Rien n'a changé (cas nominal, aucune escalade) → `fabric.mod.json` n'est pas touché,
`"fabric-api"` reste `"*"`. Pas de fichier d'état (appel manuel isolé) → aucune
escalade constatable, on ne touche rien non plus.

Généraliser `write_depends_minecraft()` en `write_depends(key, value, dry_run)` —
elle fait déjà exactement ça pour `minecraft`.

### 1d. `revert_compat()` restaure aussi les planchers

Restaurer `depends.fabric-api` et `depends.fabricloader` depuis le snapshot, en plus
de `supported_minecraft_versions` et `depends.minecraft`. Sur le chemin d'échec ils
n'auront jamais été écrits (§1c), mais ça rend la fonction idempotente et robuste à un
re-run. `loader_version` / `fabric_api_version` dans `gradle.properties` restent
**bumpés** (décision validée).

### 1e. `--run-tests` hérite de l'échelle

`run_tests()` (~:575) appelle `bash .github/scripts/test-matrix.sh`. La faire pointer
sur le nouveau wrapper `.github/scripts/test-with-escalation.sh` (§2) : le local et la
CI exécutent alors strictement la même séquence, sans dupliquer l'échelle en Python.

---

## 2. `.github/scripts/test-with-escalation.sh` (nouveau)

Wrapper autour de `test-matrix.sh`, dans le style des scripts existants (en-tête
commenté expliquant le *pourquoi*, `set -uo pipefail`, `REPO_ROOT` + `cd`) :

```bash
run_matrix() { bash .github/scripts/test-matrix.sh; }

run_matrix && exit 0

for FLAG in --bump-fabric-api --bump-loader; do
    python3 scripts/update-mc-version.py "$FLAG"; rc=$?
    [ "$rc" -eq 3 ] && continue      # deja au plus recent, escalade sans effet
    [ "$rc" -ne 0 ] && exit 1        # erreur reelle (reseau, cle manquante)
    echo "$FLAG <ancienne> <nouvelle>" >> "$ESCALATION_FILE"
    run_matrix && exit 0
done
exit 1
```

- `ESCALATION_FILE` (défaut `test-escalation.txt`) : une ligne par bump appliqué, lue
  par le corps de PR. Tronqué au démarrage, comme `STATUS_FILE` dans `test-matrix.sh`.
- `test-matrix.sh` tronque déjà `test-matrix-status.txt` à chaque passe : le fichier
  reflète donc la **dernière** matrice, ce que le tableau de la PR doit montrer.
- Chaque passe relance bien **toutes** les sous-versions (`--list-test-versions`
  inchangé), pas seulement celle qui a cassé.
- Aucune modification de `test-matrix.sh` ni de `headless-server-test.sh`.

---

## 3. `.github/workflows/check-new-minecraft.yml`

Diff volontairement minimal — l'échelle vit dans le script, pas dans le YAML.

1. **Step `tests`** (:131) : `bash .github/scripts/test-matrix.sh` →
   `bash .github/scripts/test-with-escalation.sh`. Le `continue-on-error: true` et
   tous les `if: steps.tests.outcome == 'success'` en aval restent inchangés.
2. **Nouveau step `final`**, juste après `tests`, `if: status == 'updated'` : relit
   `loader_version` et `fabric_api_version` dans `gradle.properties` et les met dans
   `$GITHUB_OUTPUT`. Réutiliser le helper `prop()` de `release.yml:189`. Nécessaire
   car `steps.update.outputs.*` porte les valeurs **pré-escalade**.
3. **Corps de PR** (:183) et **issue d'échec** (:326) : consommer
   `steps.final.outputs.*` au lieu de `steps.update.outputs.*` pour Fabric API et le
   loader. Ajouter dans le tableau « Versions résolues » la mention `figé` /
   `escaladé depuis X` en lisant `test-escalation.txt`, et une ligne
   `depends.fabric-api` quand un plancher a été gravé.
4. **Message de commit** de `Mark the version as compatible` (:144) : mentionner
   l'escalade quand `test-escalation.txt` est non vide (ce step commite déjà
   `gradle.properties` + `fabric.mod.json`, donc il ramasse naturellement les valeurs
   escaladées et les nouveaux planchers). Idem pour
   `Revert unproven compatibility` (:159), qui ramasse les bumps conservés.
5. **`Upload logs`** (:170) : ajouter `test-escalation.txt` au `path`.

---

## 4. Docs

`docs/mc-update-plan.md`, en français (les commentaires de code restent en anglais) :

- §« Matrice de versions » (:382) : la phrase d'ouverture « Une mise à jour bumpe
  `fabric_api` et le loader vers leur dernière version » est désormais fausse —
  réécrire avec le modèle figé + escalade et le schéma des trois passes.
- §Vérification (:374) : le chemin d'échec attendu change (bumps figés, puis
  escaladés).
- :66 et :262 : listes de clés bumpées à corriger.
- Ajouter une courte section décrivant l'échelle et les codes de sortie (dont le 3).

`README.md` : ajouter les deux nouveaux flags si la commande locale y est documentée.

---

## Vérification

Pré-requis : le repo est sur `minecraft_version=26.1.2`, `fabric_api_version=0.155.2+26.1.2`.

**a. L'update ne bouge plus que Minecraft**

```bash
git stash list                       # rien en cours
python3 scripts/update-mc-version.py 26.1.1 --force --dry-run
```
Attendu : `minecraft_version`, `mod_version`, `java_version`, `loom_version` bougent ;
`loader_version` et `fabric_api_version` sont affichés en `(frozen, latest available:
…)` et **ne sont pas** dans le diff. Puis sans `--dry-run` :
`git diff gradle.properties` ne doit contenir aucune de ces deux clés.

**b. Les modes d'escalade**

```bash
python3 scripts/update-mc-version.py --bump-fabric-api --json; echo "rc=$?"
```
Attendu, si le repo est déjà sur la dernière Fabric API de 26.1.2 : `rc=3`,
`status=already-latest`, `gradle.properties` inchangé. Sinon `rc=0` et la clé bumpée
seule. Vérifier que `fabric.mod.json` n'a **pas** bougé dans les deux cas.
Même chose avec `--bump-loader`.

**c. Le plancher n'est gravé qu'au succès**

```bash
python3 scripts/update-mc-version.py --bump-fabric-api
python3 scripts/update-mc-version.py --mark-supported
git diff src/main/resources/fabric.mod.json
```
Attendu : `"fabric-api": ">=<nouvelle version brute>"`, suffixe `+26.1.2` compris, et
`depends.minecraft` recalculé comme avant. Sans le `--bump-fabric-api` préalable,
`--mark-supported` doit laisser `"fabric-api": "*"` intact.

Puis le miroir : `--bump-fabric-api` suivi de `--revert-compat` → `fabric.mod.json`
revenu à `"*"`, mais `fabric_api_version` **toujours bumpé** dans `gradle.properties`.

**d. L'échelle de bout en bout, en local**

```bash
bash .github/scripts/test-with-escalation.sh; echo "rc=$?"
cat test-escalation.txt test-matrix-status.txt
```
Attendu quand tout passe du premier coup : `rc=0`, `test-escalation.txt` vide, une
ligne `<version> ok` par sous-version. Forcer une escalade en reculant à la main
`fabric_api_version` sur une version trop vieille pour 26.1.2 : la 1ʳᵉ matrice casse,
`--bump-fabric-api` s'applique, la 2ᵉ matrice passe, `rc=0`, et
`test-escalation.txt` contient la ligne `--bump-fabric-api`. Vérifier que la 2ᵉ passe
a bien rejoué **26.1, 26.1.1 et 26.1.2**, pas seulement la version cassée.

**e. Workflow complet**

`workflow_dispatch` de `check-new-minecraft.yml` avec `minecraft_version=26.1.2` +
`force=true`, depuis la branche `feat/automatic_CI` (la PR cible la ref du run).
Attendu : PR non-draft, tableau « Versions résolues » avec Fabric API marquée `figé`,
tableau matrice à trois lignes vertes, artefacts contenant `test-escalation.txt`.
