package com.example;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {

        // Initialisation de la fabrique de gestion des entités
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("gestion-reservations");

        try {

            System.out.println("\n=== Vérification des relations + Cascade ===");
            scenarioRelationsCascade(factory);

            System.out.println("\n=== Vérification du mécanisme OrphanRemoval ===");
            scenarioOrphanRemoval(factory);

            System.out.println("\n=== Vérification relation ManyToMany (Salle - Equipement) ===");
            scenarioManyToMany(factory);

        } finally {
            // Libération des ressources JPA
            factory.close();
        }
    }

    private static void scenarioRelationsCascade(EntityManagerFactory factory) {

        EntityManager manager = factory.createEntityManager();

        try {

            manager.getTransaction().begin();

            // ----- Création des objets métiers -----

            Utilisateur userTest = new Utilisateur("El Idrissi", "Othmane", "othmane.test@example.com");

            Salle roomTest = new Salle("Salle X201", 35);
            roomTest.setDescription("Salle équipée pour réunions professionnelles");

            Reservation bookingTest = new Reservation(
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    "Session Projet"
            );

            // ----- Association des objets -----
            userTest.addReservation(bookingTest);
            roomTest.addReservation(bookingTest);

            // Sauvegarde avec propagation en cascade
            manager.persist(userTest);
            manager.persist(roomTest);

            manager.getTransaction().commit();

            System.out.println("Insertion réussie avec relations établies ✔");

            // Nettoyage du contexte pour forcer la relecture depuis la base
            manager.clear();

            Utilisateur userLoaded = manager.find(Utilisateur.class, userTest.getId());
            System.out.println("Utilisateur chargé : " + userLoaded);
            System.out.println("Total réservations : " + userLoaded.getReservations().size());

            Salle roomLoaded = manager.find(Salle.class, roomTest.getId());
            System.out.println("Salle chargée : " + roomLoaded);
            System.out.println("Total réservations : " + roomLoaded.getReservations().size());

        } catch (Exception ex) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            ex.printStackTrace();

        } finally {
            manager.close();
        }
    }

    private static void scenarioOrphanRemoval(EntityManagerFactory factory) {

        EntityManager manager = factory.createEntityManager();

        try {

            manager.getTransaction().begin();

            Utilisateur user = new Utilisateur("Benali", "Sara", "sara.benali@example.com");

            Salle roomA = new Salle("Salle Y101", 20);
            Salle roomB = new Salle("Salle Z303", 15);

            manager.persist(roomA);
            manager.persist(roomB);

            Reservation booking1 = new Reservation(
                    LocalDateTime.now().plusDays(2),
                    LocalDateTime.now().plusDays(2).plusHours(1),
                    "Réunion interne"
            );

            Reservation booking2 = new Reservation(
                    LocalDateTime.now().plusDays(3),
                    LocalDateTime.now().plusDays(3).plusHours(2),
                    "Workshop"
            );

            user.addReservation(booking1);
            user.addReservation(booking2);

            roomA.addReservation(booking1);
            roomB.addReservation(booking2);

            manager.persist(user);

            manager.getTransaction().commit();

            System.out.println("Utilisateur avec 2 réservations enregistré ✔");

            // ---- Suppression d'un enfant (orphanRemoval) ----
            manager.getTransaction().begin();

            Utilisateur userToEdit = manager.find(Utilisateur.class, user.getId());
            System.out.println("Avant suppression : " + userToEdit.getReservations().size());

            Reservation bookingToDelete = userToEdit.getReservations().get(0);
            userToEdit.removeReservation(bookingToDelete);

            manager.getTransaction().commit();

            manager.clear();

            Utilisateur userAfter = manager.find(Utilisateur.class, user.getId());
            System.out.println("Après suppression : " + userAfter.getReservations().size());

            Reservation deletedBooking = manager.find(Reservation.class, bookingToDelete.getId());
            System.out.println("Réservation toujours présente ? " + (deletedBooking != null));

        } catch (Exception ex) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            ex.printStackTrace();

        } finally {
            manager.close();
        }
    }

    private static void scenarioManyToMany(EntityManagerFactory factory) {

        EntityManager manager = factory.createEntityManager();

        try {

            manager.getTransaction().begin();

            // ----- Création équipements -----
            Equipement videoProj = new Equipement("Vidéo Projecteur", "Projecteur Full HD");
            Equipement smartBoard = new Equipement("Tableau Intelligent", "Écran tactile 75 pouces");
            Equipement visioSystem = new Equipement("Visio Conférence", "Caméra + micro HD");

            // ----- Création salles -----
            Salle meetingRoom = new Salle("Salle Business A", 30);
            Salle trainingRoom = new Salle("Salle Training B", 45);

            meetingRoom.addEquipement(videoProj);
            meetingRoom.addEquipement(visioSystem);

            trainingRoom.addEquipement(videoProj);
            trainingRoom.addEquipement(smartBoard);

            manager.persist(meetingRoom);
            manager.persist(trainingRoom);

            manager.getTransaction().commit();

            System.out.println("Relations ManyToMany enregistrées ✔");

            manager.clear();

            Salle loadedMeeting = manager.find(Salle.class, meetingRoom.getId());
            System.out.println("Salle : " + loadedMeeting.getNom());
            for (Equipement eq : loadedMeeting.getEquipements()) {
                System.out.println("- " + eq.getNom());
            }

            // Récupération d’un équipement précis
            Equipement loadedProjector = manager.createQuery(
                            "SELECT e FROM Equipement e WHERE e.nom = :nom", Equipement.class)
                    .setParameter("nom", "Vidéo Projecteur")
                    .getSingleResult();

            System.out.println("\nEquipement : " + loadedProjector.getNom());
            for (Salle s : loadedProjector.getSalles()) {
                System.out.println("- Présent dans : " + s.getNom());
            }

            // Suppression d’une liaison (pas suppression BD)
            manager.getTransaction().begin();

            loadedMeeting.removeEquipement(loadedProjector);

            manager.getTransaction().commit();

            manager.clear();

            Salle afterUpdate = manager.find(Salle.class, meetingRoom.getId());
            System.out.println("\nAprès modification : ");
            for (Equipement eq : afterUpdate.getEquipements()) {
                System.out.println("- " + eq.getNom());
            }

            Equipement checkEquip = manager.find(Equipement.class, loadedProjector.getId());
            System.out.println("Equipement toujours existant ? " + (checkEquip != null));

        } catch (Exception ex) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            ex.printStackTrace();

        } finally {
            manager.close();
        }
    }
}