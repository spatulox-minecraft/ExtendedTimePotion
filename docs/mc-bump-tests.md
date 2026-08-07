# Tester mc-bump en conditions réelles

Cette branche (`test/mc-bump`) est un banc d'essai. Elle ajoute trois workflows
`[mc-bump] *` **à côté** des anciens, sans les remplacer : les deux pipelines
tournent sur le même commit et se comparent.

Rien ici n'est destiné à être mergé sur `master`.

---

## Prérequis bloquant : mc-bump est privé

`GITHUB_TOKEN` n'a accès qu'au dépôt qui l'a émis. Les 10 `actions/checkout` de
`spatulox-minecraft/mc-bump` échoueront tant que le dépôt est privé.

**Le plus simple : passer mc-bump en public.** C'est de l'outillage CI, il ne
contient aucun secret (les tokens des stores vivent dans les secrets
d'ExtendedTimePotion, pas dans mc-bump).

L'alternative, si tu tiens au privé : créer un PAT `repo:read`, l'ajouter en
secret `MC_BUMP_TOKEN` dans ExtendedTimePotion, et passer `token:` à chaque
checkout de mc-bump. Dix endroits à modifier, un secret à faire tourner — pour
protéger du code qui décrit publiquement comment builder un mod.

À vérifier aussi, côté mc-bump : *Settings → Actions → General → Access* doit
autoriser l'appel depuis les dépôts de l'organisation.

---

## Pourquoi une branche, et pas un dépôt de mod de test

L'argument attendu pour un dépôt séparé, c'est la vitesse. **Il ne tient pas.**
Le coût d'un run est dominé par Loom : téléchargement de Minecraft, des mappings
et de Fabric API, puis remap. Un mod d'un seul fichier paie exactement la même
facture qu'ExtendedTimePotion. Un faux mod coûte donc autant et prouve moins.

Ce qu'une branche apporte en plus :

- le vrai mod, le vrai build, les vraies assertions de log (50 potions) ;
- `.github/mc-bump.yml` y est déjà ;
- c'est jetable — `git branch -D` contre un dépôt qui reste à vie.

Le seul point que la branche ne couvre pas est l'upload réel vers les stores. La
réponse n'est pas un dépôt de test mais un **projet Modrinth jetable** : voir
l'en-tête de `mcbump-release.yml`.

`act` n'est pas une option ici : il gère mal les workflows réutilisables
provenant d'un autre dépôt, et pas du tout l'aller-retour d'artefacts dont
dépend tout le rapport d'échec.

---

## Ordre de passage

Chaque étape est un `workflow_dispatch` depuis l'onglet Actions, sur la branche
`test/mc-bump`.

### 1. La plomberie (2 minutes, échoue vite)

Lancer **[mc-bump] CI**. Ce qui compte n'est pas le vert final mais le job
`config` : il valide d'un coup le checkout cross-repo, la résolution de
`./.mc-bump/.github/actions/setup`, la lecture de la config et le `fromJson` de
la matrice.

- [ ] `config` passe et sort `versions` en JSON
- [ ] les jobs `matrix` se déploient, un par version annoncée
- [ ] `unit-tests` est vert (5 tests)
- [ ] `gametest` est **rouge et non bloquant** (il ne compile plus depuis 26.2)
- [ ] `verdict` est vert malgré le gametest rouge
- [ ] une issue est ouverte, assignée, avec le tableau et un `<details>` gametest

### 2. Casser exprès, un à la fois

Commiter la casse sur cette branche, relancer, annuler.

| casse | attendu |
|---|---|
| `assertEquals("11:00", …)` → `"99:00"` dans `PotionDurationTest` | `unit-tests` rouge, `verdict` rouge, issue avec un `<details>` JUnit |
| `expect.pattern` → `"Ceci n'apparaitra jamais"` dans `mc-bump.yml` | serveur rouge sur **chaque** version, un `<details>` par version |
| `count-pattern` → `"= registerPotionXX("` | message « never appears in … no longer knows how to count it » |
| relancer sans rien changer | l'issue existante est **commentée**, pas dupliquée |
| casser deux choses ensemble | **une** issue, deux `<details>`, échecs triés avant les verts |

### 3. L'auto-update (le plus long)

**[mc-bump] Auto-update** avec `force: true` et `minecraft-version` vide, ou la
version courante pour un aller-retour rapide.

- [ ] la branche `chore/mc-<version>` est poussée et **survit** à l'échec
- [ ] la PR cible `test/mc-bump`, pas `master`
- [ ] son corps contient le tableau : unitaires + une ligne par version + gametest
- [ ] les échecs y ont leur `<details>`
- [ ] échec bloquant → PR en draft **et** issue ; gametest seul → PR only

### 4. La release en dry-run

**[mc-bump] Release**, `dry-run: true`.

- [ ] `check` refuse tôt si un secret de store manque
- [ ] `check` refuse si `minecraft_version` n'est pas dans `supported_minecraft_versions`
- [ ] les deux jobs de publication démarrent en parallèle et sautent l'upload
- [ ] aucun tag n'est posé

Puis, avec un projet Modrinth jetable et `dry-run: false` :

- [ ] l'upload passe, le marqueur `published/modrinth/<tag>` est posé
- [ ] **relancer** : le job affiche *skipped*, sans erreur de version dupliquée
- [ ] supprimer le marqueur CurseForge et relancer : seul CurseForge repart

C'est le scénario qui bloquait définitivement l'ancien `publishMod`. Il mérite
d'être vu fonctionner au moins une fois.

---

## Nettoyage

```bash
# côté GitHub
gh issue list --label ci-failure --state open       # puis fermer
gh pr list --head chore/mc- --state open            # puis fermer
git push origin --delete chore/mc-<version>
git push origin --delete published/modrinth/<tag>   # si un vrai upload a eu lieu

# côté local
git branch -D test/mc-bump
```

Les tags `published/*` n'apparaissent pas dans la liste des releases, mais ils
restent dans `git tag`. Les supprimer évite qu'un futur run les lise comme « déjà
publié ».
