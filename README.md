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
Minecraft / Fabric Loader / Fabric API, build, lance un serveur headless et ouvre une PR assignée.
Si le mod ne fonctionne pas sur la nouvelle version, une issue est ouverte avec les logs.

En local, la même séquence tient en une commande :

```bash
python3 scripts/update-mc-version.py --run-tests          # dernière release Mojang
python3 scripts/update-mc-version.py 26.2 --run-tests     # version précise
```

Elle met à jour `gradle.properties` et `fabric.mod.json`, lance `./gradlew build` puis
`.github/scripts/headless-server-test.sh`, et n'annonce la compatibilité que si les deux passent —
sinon elle restaure les bornes de compatibilité précédentes en conservant le bump de dépendances.

Détail du modèle de compatibilité et du découpage : `docs/mc-update-plan.md`.
`python3 scripts/update-mc-version.py --help` liste les autres modes (`--dry-run`, `--json`,
`--mark-supported`, `--revert-compat`).
