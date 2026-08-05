# Extented Time Potion

This simple mod allows you to create certain potions with an extended duration of use.

Take the potion with the max vanilla time : 8:00 (crafted with redstone).

Now you can add an extra golden nugget to extend to time up to 11:00 minutes !
If you add a golden carrot to this extend potion, you can bring it up to 15:00 !

Have fun !
- https://modrinth.com/mod/extended_time_potion
- https://www.curseforge.com/minecraft/mc-mods/extended-time-potion

## Mettre à jour Minecraft

Tous les mercredis, `.github/workflows/check-new-minecraft.yml` résout les dernières versions
Minecraft / Fabric Loader / Fabric API / fabric-loom, build, lance un serveur headless et ouvre une
PR assignée. Si le mod ne fonctionne pas sur la nouvelle version, une issue est ouverte avec les logs.

`loom_version` suit la dernière version **stable** de fabric-loom. Si un build casse dans loom
lui-même, on l'épingle avec `--loom <version>` (utile pour une vieille version de Minecraft qu'un
loom récent ne sait plus builder).

En local, la même séquence tient en une commande :

```bash
python3 scripts/update-mc-version.py --run-tests          # dernière release Mojang
python3 scripts/update-mc-version.py 26.2 --run-tests     # version précise
```

Elle met à jour `gradle.properties` et `fabric.mod.json`, lance `./gradlew build` puis
`.github/scripts/headless-server-test.sh`, et n'annonce la compatibilité que si les deux passent —
sinon elle restaure les bornes de compatibilité précédentes en conservant le bump de dépendances.

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
`--mark-supported`, `--revert-compat`).
