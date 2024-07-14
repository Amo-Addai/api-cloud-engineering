
-- TODO: Fix SQL cmds: RETURNING, LIMIT, OFFSET,  ... 


-- name: CreateAccount :one - one label returns 1 row

INSERT INTO 
    accounts (
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


-- name: GetAccount :one

SELECT * FROM accounts WHERE id = $1 LIMIT 1;


-- name: GetAccounts :many - many label returns multiple rows

SELECT *
    FROM accounts
    ORDER BY id
    LIMIT $1
    OFFSET $2; -- skip $2 rows (based on LIMIT of previous query-call) before returning result, for pagination


-- name: UpdateAccount :exec - exec label doesn't return any data (after updating row)

UPDATE
    accounts
    SET
        balance = $2
        -- * ONLY balance can be updated (Finance)
        -- owner = $3,
        -- currency = $4
    WHERE id = $1;


-- name: UpdateAndGetAccount :one - this time, return the updated row

UPDATE accounts
SET balance = $2
WHERE id = $1;
RETURNING *;


-- name: DeleteAccount :exec

DELETE FROM accounts WHERE id = $1;


-- name: RemoveAccount :one

DELETE
FROM accounts
WHERE id = $1
RETURNING *;
