package esi.backend.service;

import esi.backend.exception.ResourceNotFoundException;
import esi.backend.model.*;
import esi.backend.repository.CarRepository;
import esi.backend.repository.CustomerRepository;
import esi.backend.repository.RentalRepository;
import esi.backend.repository.RequestRepository;
import esi.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private final CustomerService customerService = new CustomerService();

    public ResponseEntity<List<Request>> getAllRequests() {
        return new ResponseEntity<>(requestRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<List<Request>> getAllRequestsByCarId(UUID carId) {
        Car car = carRepository.findById(carId).orElse(null);
        if (car == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(requestRepository.findByCarId(carId), HttpStatus.OK);
    }

    public ResponseEntity<Request> getRequest(UUID id) {
        Request request = requestRepository.findById(id).orElse(null);
        return (request == null)
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(request, HttpStatus.OK);
    }

    public ResponseEntity<List<Request>> getAllRequestsByCustomerId(UserDetailsImpl currentUser, long customerId) {
        ResponseEntity<Customer> customerResponseEntity = customerService.authenticateCustomer(currentUser, customerId);
        if (customerResponseEntity.getBody() == null)
            return new ResponseEntity<>(customerResponseEntity.getStatusCode());
        List<Request> requests = new ArrayList<>(customerResponseEntity.getBody().getRequests());
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    public ResponseEntity<Request> getRequestByCustomerId(UserDetailsImpl currentUser, long customerId, UUID requestId) {
        ResponseEntity<Customer> customerResponseEntity = customerService.authenticateCustomer(currentUser, customerId);
        if (customerResponseEntity.getBody() == null)
            return new ResponseEntity<>(customerResponseEntity.getStatusCode());
        Request request = customerResponseEntity.getBody().getRequests().stream().filter(
                req -> req.getId().equals(requestId)).findFirst().orElse(null);
        return (request == null)
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(request, HttpStatus.OK);
    }

    public void addRequest(UserDetails currentUser, Request request, UUID carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isPresent()) {
            request.setCar(optionalCar.get());
        } else throw new ResourceNotFoundException("Car with id" + carId + "not found");
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(currentUser.getUsername());
        optionalCustomer.ifPresent(request::setCustomer);
        requestRepository.save(request);
    }

    public void updateRequest(UserDetailsImpl user, Request request, UUID cardId, UUID requestId) {
        Request req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request with id " + requestId + " not found"));
        if (request.getPickup_datetime() != null) {
            req.setPickup_datetime(request.getPickup_datetime());
        }
        if (request.getDropoff_datetime() != null) {
            req.setDropoff_datetime(request.getDropoff_datetime());
        }
        if (request.getDropoff_location() != null) {
            req.setDropoff_location(request.getDropoff_location());
        }
        if (request.getStatus() != null) {
            if (request.getStatus().equals(RequestStatus.CANCELLED) || request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.PENDING)) {
                req.setStatus(request.getStatus());
            }
            if (request.getStatus().equals(RequestStatus.ACCEPTED) && user.isManager()) {
                req.setStatus(request.getStatus());
                createRental(req);
            }
        }
        requestRepository.save(req);
    }

    public void deleteRequest(UUID id) {
        requestRepository.deleteById(id);
    }


    public void createRental(Request request) {
        Rental rental = new Rental(request.getId(), request.getPickup_datetime(), request.getDropoff_datetime(), request.getPickup_location(), request.getDropoff_location(), RentalStatus.UPCOMING, request.getCar(), request.getCustomer());
        rentalRepository.save(rental);
    }
}
