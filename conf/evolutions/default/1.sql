# --- !Ups
CREATE TABLE error_report (
    filename varchar PRIMARY KEY,
    dn varchar,
    appVersion varchar,
    osVersion varchar,
    username varchar,
    logType varchar,
    logText varchar
);

# --- !Downs
DROP TABLE error_report;
