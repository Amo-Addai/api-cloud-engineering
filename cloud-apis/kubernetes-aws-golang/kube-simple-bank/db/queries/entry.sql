
-- TODO: Fix SQL cmds: RETURNING, LIMIT, OFFSET,  ... 


-- name: CreateEntry :one - one label returns 1 row

INSERT INTO 
    entries (
        owner,
        balance,
        currency
    )
    VALUES (
        $1,
        $2,
        $3
    )
    RETURNING *; -- return new row with all cols after insertion 


-- name: GetEntry :one

SELECT * FROM entries WHERE id = $1 LIMIT 1;


-- name: GetEntries :many - many label returns multiple rows

SELECT *
    FROM entries
    ORDER BY id
    LIMIT $1
    OFFSET $2; -- skip $2 rows (based on LIMIT of previous query-call) before returning result, for pagination


-- name: UpdateEntry :exec - exec label doesn't return any data (after updating row)

UPDATE
    entries
    SET
        balance = $2
        -- * ONLY balance can be updated (Finance)
        -- owner = $3,
        -- currency = $4
    WHERE id = $1;


-- name: UpdateAndGetEntry :one - this time, return the updated row

UPDATE entries
SET balance = $2
WHERE id = $1;
RETURNING *;


-- name: DeleteEntry :exec

DELETE FROM entries WHERE id = $1;


-- name: RemoveEntry :one

DELETE
FROM entries
WHERE id = $1
RETURNING *;
