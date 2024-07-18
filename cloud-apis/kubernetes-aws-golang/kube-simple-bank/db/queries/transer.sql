
-- TODO: Fix SQL cmds: RETURNING, LIMIT, OFFSET,  ... 


-- name: CreateTransfer :one - one label returns 1 row

INSERT INTO 
    transfers (
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


-- name: GetTransfer :one

SELECT * FROM transfers WHERE id = $1 LIMIT 1;


-- name: GetTransfers :many - many label returns multiple rows

SELECT *
    FROM transfers
    ORDER BY id
    LIMIT $1
    OFFSET $2; -- skip $2 rows (based on LIMIT of previous query-call) before returning result, for pagination


-- name: UpdateTransfer :exec - exec label doesn't return any data (after updating row)

UPDATE
    transfers
    SET
        balance = $2
        -- * ONLY balance can be updated (Finance)
        -- owner = $3,
        -- currency = $4
    WHERE id = $1;


-- name: UpdateAndGetTransfer :one - this time, return the updated row

UPDATE transfers
SET balance = $2
WHERE id = $1;
RETURNING *;


-- name: DeleteTransfer :exec

DELETE FROM transfers WHERE id = $1;


-- name: RemoveTransfer :one

DELETE
FROM transfers
WHERE id = $1
RETURNING *;
