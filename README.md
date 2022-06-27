# IA-algo-demo-theoreme
Developper en JAVA un algorithme de démonstration de théorèmes en utilisant les lois logiques.

## Description

Ce programme utilise le raisonnement par l'absurde à l'aide des lois logiques pour 
démontrer un théorème.
Les clauses sont mises dans leur forme normale conjonctive (CNF).
La négation de la clause à démontrer est ajouté à l'ensemble des clauses.
On utilise les clauses résolvantes pour mettre à jour la base de connaissances.
Si l'on retrouve la clause à démontrer, ainsi que sa négation dans la base de connaissances,
alors il y a contradiction. Donc le fait est vérifié.

## Règles

Il faut utiliser
! : pour la négation
| : disjonction
& : conjonction

Exemple : !Perdrix|OiseauPerdrix, Perdrix&PlumesPerdrix

L'application possède un menu facile d'utilisation.
