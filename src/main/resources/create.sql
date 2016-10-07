CREATE SCHEMA graph;

-- MANDATORY TABLES
CREATE TABLE graph.rooms
(
  id serial NOT NULL,
  dscr text,
  func text NOT NULL,
  finish boolean NOT NULL DEFAULT false,
  CONSTRAINT rooms_pkey PRIMARY KEY (id)
);


CREATE TABLE graph.entry_points
(
  id serial NOT NULL,
  prefix text NOT NULL,
  dscr text,
  msg text NOT NULL,
  room_id integer NOT NULL,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT entry_points_pkey PRIMARY KEY (id)
);


CREATE TABLE graph.actions
(
  id serial NOT NULL,
  dscr text,
  akey text NOT NULL,
  current_room_id integer NOT NULL,
  next_room_id integer NOT NULL,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT actions_pkey PRIMARY KEY (id)
)


-- OPTIONAL TABLES FOR MODELER
CREATE TABLE graph.attributes
(
  room_id integer NOT NULL,
  xpos integer NOT NULL DEFAULT 0,
  ypos integer NOT NULL DEFAULT 0,
  color integer NOT NULL DEFAULT 49151,
  CONSTRAINT attributes_pkey PRIMARY KEY (room_id),
  CONSTRAINT fk_entry_points FOREIGN KEY (room_id)
      REFERENCES graph.rooms (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE graph.projections
(
  id serial NOT NULL,
  name character varying(250),
  CONSTRAINT projections_pkey PRIMARY KEY (id)
);


CREATE TABLE graph.projections_rooms
(
  room_id integer NOT NULL,
  projection_id integer NOT NULL,
  CONSTRAINT pk_projections_rooms PRIMARY KEY (room_id, projection_id),
  CONSTRAINT fk_projections_rooms_l FOREIGN KEY (projection_id)
      REFERENCES graph.projections (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_projections_rooms_r FOREIGN KEY (room_id)
      REFERENCES graph.rooms (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


-- TRIGGERS FUNCTIONS
CREATE OR REPLACE FUNCTION graph.tg_rooms_ad()
  RETURNS trigger AS
$BODY$
BEGIN
 DELETE FROM graph.projections_rooms WHERE room_id = OLD.id;
 DELETE FROM graph.attributes WHERE room_id = OLD.id;
 return OLD;
END;
$BODY$


CREATE OR REPLACE FUNCTION graph.tg_projections_ad()
  RETURNS trigger AS
$BODY$
BEGIN
 DELETE FROM graph.projections_rooms WHERE projection_id = OLD.id;
 return OLD;
END;
$BODY$


-- TRIGGERS
CREATE TRIGGER tg_projections_bd
  BEFORE DELETE
  ON graph.projections
  FOR EACH ROW
  EXECUTE PROCEDURE graph.tg_projections_ad();


CREATE TRIGGER tg_room_ad
  BEFORE DELETE
  ON graph.rooms
  FOR EACH ROW
  EXECUTE PROCEDURE graph.tg_rooms_ad();
