# Extented Time Potion

This simple mod allows you to create certain potions with an extended duration of use.

Take the potion with the max vanilla time : 8:00 (crafted with redstone).

Now you can add an extra golden nugget to extend to time up to 11:00 minutes !
If you add a golden carrot to this extend potion, you can bring it up to 15:00 !

Have fun !
- https://modrinth.com/mod/extended_time_potion
- https://www.curseforge.com/minecraft/mc-mods/extended-time-potion

## Mettre à jour Minecraft

Tous les mercredis, `.github/workflows/check-new-minecraft.yml` passe à la dernière version de
Minecraft, build, lance un serveur headless et ouvre une PR assignée. Si le mod ne fonctionne pas
sur la nouvelle version, une issue est ouverte avec les logs.

**Une seule variable bouge : Minecraft.** `loader_version` et `fabric_api_version` restent **gelés**
sur la valeur déjà dans `gradle.properties`, identique pour toutes les sous-versions d'une série.
Les bumper en même temps que Minecraft rendait une matrice rouge illisible — trois suspects, et le
loader comme l'API changent le comportement de *toutes* les sous-versions d'un coup.

Ils ne bougent donc qu'en **escalade**, en réaction à un échec, un à la fois, chacun suivi d'une
matrice complète (`.github/scripts/test-with-escalation.sh`) :

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

En local, la même séquence tient en une commande :

```bash
python3 scripts/update-mc-version.py --run-tests          # dernière release Mojang
python3 scripts/update-mc-version.py 26.2 --run-tests     # version précise
```

Elle met à jour `gradle.properties` et `fabric.mod.json`, puis lance
`.github/scripts/test-with-escalation.sh` — qui build et démarre un serveur pour **chaque** version
annoncée, pas seulement la plus récente, et escalade les dépendances gelées si besoin. La
compatibilité n'est annoncée que si tout passe ; sinon les bornes précédentes sont restaurées, en
conservant les bumps de dépendances.

### Déclencher la mise à jour à la main

Depuis GitHub : onglet **Actions** → *New Minecraft version* → **Run workflow**. Le menu déroulant
permet de choisir la branche, et deux champs sont proposés : `minecraft_version` (vide = dernière
release Mojang) et `force` (continuer même si le dépôt est déjà sur cette version).

En ligne de commande, si `gh` est installé :

```bash
gh workflow run check-new-minecraft.yml --ref master
gh workflow run check-new-minecraft.yml --ref master -f minecraft_version=26.2 -f force=true
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

Détail du modèle de compatibilité et du découpage : `docs/mc-update-plan.md`.
`python3 scripts/update-mc-version.py --help` liste les autres modes (`--dry-run`, `--json`,
`--mark-supported`, `--revert-compat`, et les deux barreaux de l'escalade `--bump-fabric-api` /
`--bump-loader`, qui sortent en code 3 quand il n'y a rien de plus récent).

## Publier une version

Rien à lancer à la main : `.github/workflows/release.yml` publie sur Modrinth et CurseForge dès
qu'un merge sur `master` touche `gradle.properties` avec un `mod_version` jamais sorti. Il enchaîne
trois jobs :

1. **`check`** — le tag `v<mod_version>` n'existe pas encore, et `minecraft_version` figure bien dans
   `supported_minecraft_versions`. Ce second garde-fou refuse de publier une compatibilité que la
   matrice n'a jamais prouvée, cas d'une PR *draft* mergée à la main.
2. **`publish`** — la matrice de versions complète, puis `./gradlew publishMod`.
3. **`tag`** — le tag, posé **après** l'upload seulement.

Le tag signifie donc « version sortie », et non plus « version mergée ». C'est ce qui rend un échec
rattrapable : publication ratée → pas de tag → relancer le workflow réessaie.

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

`publishMod` n'est pas transactionnel : si CurseForge échoue après un Modrinth réussi, aucun tag
n'est posé et une relance rejouerait Modrinth, qui refusera le numéro en doublon. Reprise :
`./gradlew publishCurseForge` seul, puis re-déclencher le workflow pour le tag.
