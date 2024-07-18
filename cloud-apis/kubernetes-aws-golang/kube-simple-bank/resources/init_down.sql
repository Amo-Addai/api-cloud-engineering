
-- * sample 1st down-script - should only revert changes made by sample 1st 'up.sql' script

DROP TABLE IF EXISTS entries;
DROP TABLE IF EXISTS transfers;
DROP TABLE IF EXISTS accounts;

-- * don't drop the DB itself
