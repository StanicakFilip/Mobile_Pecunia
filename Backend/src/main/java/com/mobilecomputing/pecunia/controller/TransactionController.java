package com.mobilecomputing.pecunia.controller;

import com.mobilecomputing.pecunia.model.Notification;
import com.mobilecomputing.pecunia.model.Transaction;
import com.mobilecomputing.pecunia.model.Trip;
import com.mobilecomputing.pecunia.repository.NotificationRepository;
import com.mobilecomputing.pecunia.repository.TransactionRepository;
import com.mobilecomputing.pecunia.repository.TripRepository;
import com.mobilecomputing.pecunia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TripRepository tripRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @GetMapping("/getTransactionById")
    public ResponseEntity getTransactionById(@RequestParam String id) {
        try {
            transactionRepository.findAll().forEach(transaction -> {
                System.out.println(transaction);
            });
            return ResponseEntity.ok(transactionRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }
    }

    @GetMapping("/getAllTransactionsByTrip")
    public ResponseEntity getAllTransactionsByTrip(@RequestParam String tripId) {
        try {
            return ResponseEntity.ok(tripRepository.findById(tripId).get().getTransactions());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trip not found");
        }
    }

    @PostMapping("/addTransaction")
    public ResponseEntity addTransaction(@RequestBody Transaction transaction, @RequestParam String userId,
                                         @RequestParam String notificationMessage, @RequestParam String tripId) {
        try {
            Notification notification = new Notification();
            notification.setNotificationMessage(notificationMessage);
            notification.setNotificationType(0);
            notification.setUserId(userId);
            notification.setTransactionId(transactionRepository.save(transaction).getTransactionId());
            notification.setTripId(tripId);
            notificationRepository.save(notification);
            return ResponseEntity.ok(HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error while creating Transaction");
    }

    @PostMapping("/addTransactionToTrip")
    public ResponseEntity addTransactionToTrip(@RequestParam String transactionId, @RequestParam String tripId,
                                               @RequestParam String notificationId) {
        try {
            Trip trip = tripRepository.findById(tripId).get();
            trip.getTransactions().add(transactionId);
            tripRepository.save(trip);
            notificationRepository.deleteById(notificationId);
            return ResponseEntity.ok(trip);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trip not found");
        }

    }

    @DeleteMapping("/deleteTransaction")
    public ResponseEntity deleteTransaction(@RequestParam String transactionId, @RequestParam String tripId,
                                            @RequestParam String notificationId) {
        try {
            Trip trip = tripRepository.findById(tripId).get();
            transactionRepository.deleteById(transactionId);
            for (int i = 0; i < trip.getTransactions().size(); i++) {
                if (trip.getTransactions().get(i).equals(transactionId)) {
                    trip.getTransactions().remove(i);
                }
            }
            notificationRepository.deleteById(notificationId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element not found");
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteTransactionInvite")
    public ResponseEntity deleteTransactioninvite(@RequestParam String transactionId,
                                                  @RequestParam String notificationId) {
        try {
            transactionRepository.deleteById(transactionId);
            notificationRepository.deleteById(notificationId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element not found");
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
