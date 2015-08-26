-- List the available system schemas:

SHOW SCHEMAS FROM system;

-- List the tables in one of the schemas:

SHOW TABLES FROM system.runtime;

-- list of visible nodes in the Presto cluster along with their status:

SELECT * FROM system.runtime.nodes;

-- list of available catalogs.

SELECT * FROM system.metadata.catalogs;

-- information about currently and recently running queries on the Presto cluster. 
-- From this table you can find out:
--- the original query text (SQL), 
--- the identity of the user who ran the query
--- performance information about the query including how long the query was queued and analyzed.

SELECT * FROM system.runtime.queries;

-- information about the tasks involved in a Presto query including where they were executed and and how many rows and bytes each task processed. 

SELECT * FROM system.runtime.tasks;

