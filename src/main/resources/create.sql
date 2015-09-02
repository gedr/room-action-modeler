CREATE TABLE test.entry_points (
id serial PRIMARY KEY
, srv text NOT NULL
, dscr text
, msg text NOT NULL
, room_id integer NOT NULL
, active integer DEFAULT 0
);

CREATE TABLE test.rooms (
id serial PRIMARY KEY
, dscr text
, func text NOT NULL
, finish boolean NOT NULL DEFAULT false
);

CREATE TABLE test.actions (
 id serial PRIMARY KEY
 , entry_points_srv text NOT NULL
 , dscr text
 , akey CHAR NOT NULL
 , current_room_id integer NOT NULL
 , next_room_id integer NOT NULL
);

ALTER TABLE test.entry_points ADD CONSTRAINT FK_entry_points FOREIGN KEY (room_id) REFERENCES test.rooms(id);

ALTER TABLE test.actions ADD CONSTRAINT FK_actions_curr FOREIGN KEY (current_room_id) REFERENCES test.rooms(id);
ALTER TABLE test.actions ADD CONSTRAINT FK_actions_next FOREIGN KEY (next_room_id) REFERENCES test.rooms(id);

--truncate table test.room_headers RESTART IDENTITY CASCADE;
