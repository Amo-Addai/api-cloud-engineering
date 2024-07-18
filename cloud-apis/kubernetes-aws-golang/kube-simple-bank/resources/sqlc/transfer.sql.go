package db

import "context"

const createTransfer = `
	-- name: CreateTransfer :one

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
		RETURNING id, owner, balance, currency, created_at
`

type CreateTransferParams struct {
	Owner    string `json:"owner"`
	Balance  int64  `json:"balance"`
	Currency string `json:"currency"`
}

func (
	q *Queries,
) CreateTransfer(
	ctx context.Context,
	arg CreateTransferParams,
) (
	Transfer,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		createTransfer,
		arg.Owner,
		arg.Balance,
		arg.Currency,
	)
	var i Transfer
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const getTransfer = `
	-- name: GetTransfer :one

	SELECT 
		id,
		owner,
		balance,
		currency,
		created_at,
		FROM transfers
		WHERE id = $1
		LIMIT 1
`

func (
	q *Queries,
) GetTransfer(
	ctx context.Context,
	id int64,
) (
	Transfer,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		getTransfer,
		id,
	)
	var i Transfer
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const getTransfers = `
	-- name: GetTransfers :many

	SELECT 
		id,
		owner,
		balance,
		currency,
		created_at,
		FROM transfers
		ORDER BY id
		LIMIT $1
		OFFSET $2
`

type GetTransfersParams struct {
	Limit  int32 `json:"limit"`
	Offset int32 `json:"offset"`
}

func (
	q *Queries,
) GetTransfers(
	ctx context.Context,
	arg GetTransfersParams,
) (
	[]Transfer,
	error,
) {
	rows, err := q.db.QueryContext(
		ctx,
		getTransfers,
		arg.Limit,
		arg.Offset,
	)
	if err != nil {
		return nil, err
	}

	defer rows.Close()
	var items []Transfer
	var i Transfer

	for rows.Next() {
		if err := rows.Scan(
			&i.ID,
			&i.Owner,
			&i.Balance,
			&i.Currency,
			&i.CreatedAt,
		); err != nil {
			return nil, err
			// todo: consider returning remaining items instead
			// * return items, err // (for the other returns too)
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}

	return items, nil
}

const updateTransfer = `
	--name: UpdateTransfer :exec

	UPDATE
		transfers
		SET balance = $2
		WHERE id = $1
`

type UpdateTransferParams struct {
	ID      int64 `json:"id"`
	Balance int64 `json:"balance"`
}

func (
	q *Queries,
) UpdateTransfer(
	ctx context.Context,
	arg UpdateTransferParams,
) error {
	_, err := q.db.ExecContext(
		ctx,
		updateTransfer,
		arg.ID,
		arg.Balance,
	)
	return err
}

const updateAndGetTransfer = `
	--name: UpdateAndGetTransfer :one

	UPDATE transfers
	SET balance = $2
	WHERE id = $1
`

func (
	q *Queries,
) UpdateAndGetTransfer(
	ctx context.Context,
	arg UpdateTransferParams,
) (
	Transfer,
	error,
) {
	row := q.db.QueryRowContext( // * not ExecContext - returns row sql.Result data
		// can't be converted to row sql.Row (to call .Scan(..) after)
		ctx,
		updateAndGetTransfer,
		arg.ID,
		arg.Balance,
	)
	var i Transfer
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const deleteTransfer = `
	--name: DeleteTransfer :exec

	DELETE FROM transfers
	WHERE id = $1
`

func (
	q *Queries,
) DeleteTransfer(
	ctx context.Context,
	id int64,
) error {
	_, err := q.db.ExecContext(
		ctx,
		deleteTransfer,
		id,
	)
	return err
}

const removeTransfer = `
	--name: RemoveTransfer :one

	DELETE 
	FROM transfers
	WHERE id = $1
	RETURNING id, owner, balance, currency, created_at
`

func (
	q *Queries,
) RemoveTransfer(
	ctx context.Context,
	id int64,
) (
	Transfer,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		removeTransfer,
		id,
	)
	var i Transfer
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}
