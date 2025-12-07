# TP3 Big Data - Rapport

## 1. Contexte
Mise en place d'un environnement Hadoop/Spark via Docker, compilation d'une application Spark (RDD) et exécution de deux jobs : analyse des logs web et analyse des ventes.

## 2. Environnement Docker/Spark
- Démarrage de la stack (Hadoop + Spark master/worker) avec Docker Compose.  
  ![Démarrage des conteneurs](<screens/lance-cantainers.png>)
- Ouverture d'un shell dans le conteneur `spark-master` pour exécuter les commandes Spark.  
  ![Entrer dans le conteneur](<screens/Entrez dans le conteneur spark-master.png>)

## 3. Construction du jar Spark
- Compilation et packaging Maven du projet `rdd-sparkk`.  
  ![Génération du jar](<screens/generer-jar.png>)
- Jar produit dans `target/` prêt à être copié vers le conteneur.  
  ![Jar généré](<screens/la-jar.png>)

## 4. Analyse des logs web (`LogAnalysis`)
- Préparation des fichiers dans le conteneur : jar et log Apache copiés dans `/tmp`, puis vérification.  
  ![Copie access.log](<screens/copier-access-log.png>)  
  ![Copie du jar](<screens/copier-jar-dans -container.png>)  
  ![Fichiers disponibles dans /tmp](<screens/lister-lesfichiers-copié.png>)
- Lancement du job via `spark-submit` en mode local.  
  ![Lancer l'analyse des logs](<screens/lancer-Analyse des Logs.png>)
- Résultats de l'analyse : volumes et erreurs, top IP, top ressources, répartition des codes HTTP.  
  ![Total requêtes et erreurs](<screens/nombre total de requêtes-erreur.png>)  
  ![Top 5 IPs](<screens/top5-ips.png>)  
  ![Top 5 ressources](<screens/top5-ressources.png>)  
  ![Répartition par code HTTP](<screens/Répartition des requêtes par code HTTP .png>)

## 5. Analyse des ventes (`SalesAnalysis`)
- Préparation des données de ventes copiées dans `/tmp/ventes.txt`.  
  ![Copie ventes.txt](<screens/copier-ventes-txt.png>)
- Exécution du job avec `spark-submit` depuis le conteneur.  
  ![Lancer l'analyse des ventes](<screens/Lancez l'analyse des ventes.png>)
- Résultats : total des ventes par ville, puis par ville et année.  
  ![Ventes par ville](<screens/totale-ventes-ville.png>)  
  ![Ventes par ville et année](<screens/total-vente-ville-annee.png>)

## 6. Points clés retenus
- Le cluster local (Hadoop + Spark standalone) démarre et expose les ports nécessaires.
- Les données (logs et ventes) ainsi que le jar Spark sont copiés dans `/tmp` du `spark-master` avant l'exécution.
- Les jobs Spark se lancent via `spark-submit` en `local[*]` et produisent les agrégations attendues pour les logs et les ventes.
