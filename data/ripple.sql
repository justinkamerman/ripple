# Create User table
CREATE TABLE User (
       id               INT NOT NULL,
       name             VARCHAR(64),
       screen_name      VARCHAR(32),
       followers_count  INT,
       friends_count    INT,
       statuses_count   INT,
       PRIMARY KEY (id)
) ENGINE=InnoDB;

# Create Followers table
CREATE TABLE Follower (
       followee_id      INT NOT NULL,
       follower_id      INT NOT NULL,
       UNIQUE (follower_id, followee_id)
) ENGINE=InnoDB;

