TRUNCATE cars,roles,users,rentals,requests CASCADE;


INSERT INTO cars (id,mark, model, nr_of_seats, transmission_type, fuel_type, daily_cost, year, licence_plate) VALUES
          ('a81bc81b-dead-4e5d-abff-90865d1e13b1','Ford','Focus 1.6 Aut',5,'Automatic','Petrol',71,2002,'123 ABC'),
          ('c8203520-c8b0-11ec-9d64-0242ac120002','Mercedes-Benz','C 220 AMG',5,'Automatic','Diesel',50,2012,'456 DEF');

INSERT INTO roles(id,name) VALUES
           (1,'ROLE_CUSTOMER'),
           (2,'ROLE_MANAGER');

INSERT INTO users(dtype,id,username, email, password) VALUES
            ('user',1,'customer2','customer2@email.com','$2a$10$o3pbWzJM3cHq2O//i1oyduYarn6eMytd1M1bS9Buzn/27J0tFnrsS'),
            ('user',2, 'manager2','manager2@email.com','$2a$10$ldLnBKd/8B0tjRwVxFeAO.xyi/NCBTRjmEuFQ8udAh43t6J6mX4Ua');

INSERT INTO user_roles(user_id,role_id) VALUES
            (1,1),
            (2,2);

INSERT INTO requests(id,pickup_datetime, dropoff_datetime, pickup_location, dropoff_location, status, car_id,customer_id) VALUES
            ('a82bc31b-dead-6a5d-ad65-90865d1e13b2','2022-01-10T17:09:42.411','2022-01-10T17:09:42.411','Lõunakeskus','Kesklinn', 1, 'a81bc81b-dead-4e5d-abff-90865d1e13b1',1);

INSERT INTO rentals (id, pickup_datetime, dropoff_datetime, pickup_location, dropoff_location, status, car_id,customer_id) VALUES
            ('a81bc81b-dead-6e5d-ad75-90865d1e13b1','2022-01-10T17:09:42.411','2022-01-10T17:09:42.411','Lõunakeskus','Kesklinn', 1, 'a81bc81b-dead-4e5d-abff-90865d1e13b1',1);