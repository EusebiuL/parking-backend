/************ Create schema: parking ***************/
CREATE SCHEMA IF NOT EXISTS parking;

/************ Create Tables ************************/

/************ Add Table: parking.user **************/
CREATE TABLE parking.user
(
user_id SERIAL PRIMARY KEY,
email VARCHAR(200) NOT NULL,
name VARCHAR(200) NOT NULL,
password VARCHAR(1000) NOT NULL,
is_active BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP NOT NULL
);


/************ Add Table: parking.car **************/
CREATE TABLE parking.car
(
car_id SERIAL PRIMARY KEY,
car_number VARCHAR(200) NOT NULL,
u_user_id INTEGER NOT NULL
);

/************ Add Table: parking.device **************/
CREATE TABlE parking.device
(
device_id SERIAL PRIMARY KEY,
u_user_id INTEGER NOT NULL
);

/************ Add Table: parking.authentication **************/
CREATE TABLE parking.authentication
(
u_user_id INTEGER NOT NULL,
d_device_id INTEGER NOT NULL,
authentication_token VARCHAR(1000) NOT NULL
);

/************ Add Table: parking.reports **************/
CREATE TABLE parking.reports
(
report_id SERIAL PRIMARY KEY,
user_id INTEGER NOT NULL,
report_message TEXT NOT NULL
);

/************ Add Table: parking.notifications **************/
CREATE TABLE parking.notifications
(
notification_id SERIAL PRIMARY KEY,
u_for INTEGER NOT NULL,
u_from INTEGER,
message TEXT NOT NULL,
created_at TIMESTAMP NOT NULL,
viewed BOOLEAN DEFAULT FALSE
);

/* Add Foreign Key: fk_device_user */
ALTER TABLE parking.device ADD CONSTRAINT fk_device_user
FOREIGN KEY(u_user_id) REFERENCES parking.user(user_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

/* Add Foreign Key: fk_authentication_user */
ALTER TABLE parking.authentication ADD CONSTRAINT fk_authentication_user
FOREIGN KEY(u_user_id) REFERENCES parking.user(user_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

/* Add Foreign Key: fk_authentication_device */
ALTER TABLE parking.authentication ADD CONSTRAINT fk_authentication_device
FOREIGN KEY(d_device_id) REFERENCES parking.device(device_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

/* Add Foreign Key: fk_car_user */
ALTER TABLE parking.car ADD CONSTRAINT fk_car_user
FOREIGN KEY(u_user_id) REFERENCES parking.user(user_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

