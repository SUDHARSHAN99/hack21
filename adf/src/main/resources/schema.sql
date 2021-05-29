DROP TABLE IF EXISTS lead;
  
CREATE TABLE lead (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  fname VARCHAR(50) NOT NULL,
  lname VARCHAR(50) NOT NULL,
  income int DEFAULT NULL,
  listingId long DEFAULT NULL
  empType VARCHAR(100) NOT NULL,
  empStatus VARCHAR(50) NOT NULL,
  datestamp datetime DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS leadBidStatus;

CREATE TABLE leadBidStatus (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  leadId long NOT NULL,
  bidAmount int(50) NOT NULL,
  listing_id int DEFAULT NULL,
  bidStatus VARCHAR(50) NOT NULL,
  datestamp datetime DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS leadModelScores;

CREATE TABLE leadModelScores (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  leadId long NOT NULL,
  narScore int DEFAULT NULL,
  riskScore int DEFAULT NULL,
  datestamp datetime DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS leadListingRequest;

CREATE TABLE leadListingRequest (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  leadId long NOT NULL,
  request blob DEFAULT NULL,
  datestamp datetime DEFAULT CURRENT_TIMESTAMP
);
