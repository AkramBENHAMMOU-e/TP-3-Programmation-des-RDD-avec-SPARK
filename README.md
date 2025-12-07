
# Rapport TP3 : Programmation Distribuée avec Spark RDD

## 1. Objectifs du TP

L'objectif de ce travail pratique est de maîtriser la programmation **Spark RDD (Resilient Distributed Datasets)** en Java. Les tâches principales sont :

1. Mettre en place un cluster **Hadoop/Spark** via Docker.
    
2. Développer une application Java pour analyser des ventes (données structurées).
    
3. Développer une application Java pour analyser des logs de serveur web (données semi-structurées) en utilisant des expressions régulières.
    
4. Compiler, déployer et exécuter les jobs sur un conteneur Spark Master.
    

## 2. Architecture et Prérequis

### Environnement Technique

- **Langage :** Java (JDK 21)
    
- **Framework :** Apache Spark Core 3.x
    
- **Build Tool :** Maven
    
- **Virtualisation :** Docker & Docker Compose
    

## 3. Mise en place de l'environnement

Nous avons utilisé `docker-compose` pour orchestrer les conteneurs nécessaires.

Étape 1 : Démarrage du Cluster

Les services Hadoop (HDFS) et Spark (Master & Worker) sont lancés.

Bash

```
docker-compose up -d
```

![Démarrage des conteneurs](<screens/lance-cantainers.png>)

Étape 2 : Accès au Spark Master

Nous ouvrons un terminal interactif dans le conteneur maître pour soumettre nos jobs.

Bash

```
docker exec -it spark-master bash
```

![Entrer dans le conteneur](<screens/Entrez dans le conteneur spark-master.png>)

---

## 4. Construction et Déploiement

Le code source a été développé en local, puis compilé avec Maven pour produire un fichier JAR contenant toutes les dépendances nécessaires.

**Compilation du projet :**

Bash

```
mvn clean package
```

![Compilation Maven](<screens/generer-jar.png>)

Une fois le build terminé, le fichier `rdd-spark.jar` est disponible dans le dossier `target/`.

![Jar généré](<screens/la-jar.png>)

Transfert des fichiers vers le Cluster :

Puisque nous travaillons dans un environnement conteneurisé, nous copions les données et le binaire d'exécution dans le dossier temporaire /tmp du conteneur maître.

Bash

```
# Copie des jeux de données
docker cp ventes.txt spark-master:/tmp/ventes.txt
docker cp access.log spark-master:/tmp/access.log

# Copie de l'application
docker cp target/rdd-spark.jar spark-master:/tmp/rdd-spark.jar
```

![Copie ventes.txt](<screens/copier-ventes-txt.png>)
![Copie access.log](<screens/copier-access-log.png>)
![Copie du jar](<screens/copier-jar-dans -container.png>)

_Copies des fichiers :_

**Vérification de la présence des fichiers dans le conteneur :**

![Fichiers disponibles dans /tmp](<screens/lister-lesfichiers-copié.png>)

---

## 5. Exécution et Résultats

### A. Analyse des Logs Web (`LogAnalysis`)

Ce job parse un fichier de logs Apache, extrait les champs (IP, Date, Méthode, URL, Code, Taille) via Regex et effectue des agrégations.

**Commande d'exécution :**

Bash

```
/opt/spark/bin/spark-submit \
  --class com.tp.LogAnalysis \
  --master local[*] \
  /tmp/rdd-spark.jar
```

![Lancer l'analyse des logs](<screens/lancer-Analyse des Logs.png>)

**Résultats obtenus :**

1. **Statistiques globales :** Calcul du taux d'erreur (Codes 4xx et 5xx).
    
2. **Top 5 Adresses IP :** Identification des utilisateurs les plus actifs.
    
3. **Top 5 Ressources :** Les pages les plus visitées (ex: `/index.html`).
    
4. **Répartition HTTP :** Comptage des codes de statut (200 OK, 404 Not Found, etc.).
    

![Total requêtes et erreurs](<screens/nombre total de requêtes-erreur.png>)
![Top 5 IPs](<screens/top5-ips.png>)
![Top 5 ressources](<screens/top5-ressources.png>)
![Répartition des codes HTTP](<screens/Répartition des requêtes par code HTTP .png>)

---

### B. Analyse des Ventes (`SalesAnalysis`)

Ce job traite un fichier structuré (Date, Ville, Produit, Prix) pour calculer le chiffre d'affaires.

**Commande d'exécution :**

Bash

```
/opt/spark/bin/spark-submit \
  --class com.tp.SalesAnalysis \
  --master local[*] \
  /tmp/rdd-spark.jar
```

![Lancer l'analyse des ventes](<screens/Lancez l'analyse des ventes.png>)

**Résultats obtenus :**

1. **Total des ventes par ville :** Utilisation de `reduceByKey` pour sommer les prix.
    
2. **Ventes par ville et par année :** Création d'une clé composite (Ville-Année) pour une agrégation plus fine.
    

![Ventes par ville](<screens/totale-ventes-ville.png>)
![Ventes par ville et année](<screens/total-vente-ville-annee.png>)

---

## 6. Conclusion et Points Clés

Ce TP nous a permis de valider les compétences suivantes :

- **Infrastructure :** Déploiement rapide d'un environnement Big Data fonctionnel avec Docker.
    
- **Développement RDD :** Utilisation des transformations (`map`, `filter`, `mapToPair`) et des actions (`count`, `collect`, `take`) pour manipuler la donnée.
    
- **Traitement de données :** Capacité à traiter des données non structurées (logs) en utilisant des expressions régulières couplées à Spark.
    
- **Workflow :** Maîtrise du cycle complet : Code -> Compile -> Deploy -> Submit.
