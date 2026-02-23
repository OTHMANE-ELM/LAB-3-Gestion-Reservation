

# LAB-3-Gestion-Reservation


# 🏢 Lab JPA – Gestion des Réservations

## 📌 Description

Ce TP a pour objectif de pratiquer JPA (Hibernate) en implémentant :

- Les entités Salle, Réservation et Utilisateur
- Les relations entre entités (OneToMany, ManyToOne)
- Une relation ManyToMany entre Salle et Equipement
- Les stratégies de cascade
- Le mécanisme orphanRemoval

Le projet simule un système simple de gestion de réservation de salles.

---

## 🧱 Entités

### 👤 Utilisateur
- id
- nom
- prenom
- email  
Relation : OneToMany avec Réservation

### 🏢 Salle
- id
- nom
- capacite
- description  
Relations :
- OneToMany avec Réservation
- ManyToMany avec Equipement

### 📅 Réservation
- id
- dateDebut
- dateFin
- motif  
Relations :
- ManyToOne vers Utilisateur
- ManyToOne vers Salle

### 🖥 Equipement
- id
- nom
- description  
Relation : ManyToMany avec Salle

---
---

## 📊 Diagramme de classe

Le diagramme de classe représente les entités du projet ainsi que leurs relations :

- Utilisateur (OneToMany → Réservation)
- Salle (OneToMany → Réservation)
- Salle (ManyToMany ↔ Equipement)

### 🖼 Diagramme

[Diagramme de classe]

<img width="867" height="667" alt="Diagramme de Classe " src="https://github.com/user-attachments/assets/5f157b65-f55e-4119-b348-ee8f18ca6ba6" />



---

## ⚙️ Fonctionnalités testées

✔ Création des entités  
✔ Association des relations  
✔ Cascade persist  
✔ Suppression avec orphanRemoval  
✔ Test relation ManyToMany  

---

## 🛠 Technologies utilisées

- Java
- JPA (Hibernate)
- Maven
- MySQL

---

## 🎥 Vidéo d'exécution

Vous pouvez voir la démonstration du projet ici :

👉 [Voir la vidéo d'exécution](

https://github.com/user-attachments/assets/750d86ff-e3ee-4c0e-8461-d5a27b0ac930




)



