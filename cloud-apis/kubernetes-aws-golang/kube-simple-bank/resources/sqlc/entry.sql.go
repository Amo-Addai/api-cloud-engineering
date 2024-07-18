package db

import "context"

const createEntry = `
	-- name: CreateEntry :one

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
		RETURNING id, owner, balance, currency, created_at
`

type CreateEntryParams struct {
	Owner    string `json:"owner"`
	Balance  int64  `json:"balance"`
	Currency string `json:"currency"`
}

func (
	q *Queries,
) CreateEntry(
	ctx context.Context,
	arg CreateEntryParams,
) (
	Entry,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		createEntry,
		arg.Owner,
		arg.Balance,
		arg.Currency,
	)
	var i Entry
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const getEntry = `
	-- name: GetEntry :one

	SELECT 
		id,
		owner,
		balance,
		currency,
		created_at,
		FROM entries
		WHERE id = $1
		LIMIT 1
`

func (
	q *Queries,
) GetEntry(
	ctx context.Context,
	id int64,
) (
	Entry,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		getEntry,
		id,
	)
	var i Entry
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const getEntries = `
	-- name: GetEntries :many

	SELECT 
		id,
		owner,
		balance,
		currency,
		created_at,
		FROM entries
		ORDER BY id
		LIMIT $1
		OFFSET $2
`

type GetEntriesParams struct {
	Limit  int32 `json:"limit"`
	Offset int32 `json:"offset"`
}

func (
	q *Queries,
) GetEntries(
	ctx context.Context,
	arg GetEntriesParams,
) (
	[]Entry,
	error,
) {
	rows, err := q.db.QueryContext(
		ctx,
		getEntries,
		arg.Limit,
		arg.Offset,
	)
	if err != nil {
		return nil, err
	}

	defer rows.Close()
	var items []Entry
	var i Entry

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

const updateEntry = `
	--name: UpdateEntry :exec

	UPDATE
		entries
		SET balance = $2
		WHERE id = $1
`

type UpdateEntryParams struct {
	ID      int64 `json:"id"`
	Balance int64 `json:"balance"`
}

func (
	q *Queries,
) UpdateEntry(
	ctx context.Context,
	arg UpdateEntryParams,
) error {
	_, err := q.db.ExecContext(
		ctx,
		updateEntry,
		arg.ID,
		arg.Balance,
	)
	return err
}

const updateAndGetEntry = `
	--name: UpdateAndGetEntry :one

	UPDATE entries
	SET balance = $2
	WHERE id = $1
`

func (
	q *Queries,
) UpdateAndGetEntry(
	ctx context.Context,
	arg UpdateEntryParams,
) (
	Entry,
	error,
) {
	row := q.db.QueryRowContext( // * not ExecContext - returns row sql.Result data
		// can't be converted to row sql.Row (to call .Scan(..) after)
		ctx,
		updateAndGetEntry,
		arg.ID,
		arg.Balance,
	)
	var i Entry
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}

const deleteEntry = `
	--name: DeleteEntry :exec

	DELETE FROM entries
	WHERE id = $1
`

func (
	q *Queries,
) DeleteEntry(
	ctx context.Context,
	id int64,
) error {
	_, err := q.db.ExecContext(
		ctx,
		deleteEntry,
		id,
	)
	return err
}

const removeEntry = `
	--name: RemoveEntry :one

	DELETE 
	FROM entries
	WHERE id = $1
	RETURNING id, owner, balance, currency, created_at
`

func (
	q *Queries,
) RemoveEntry(
	ctx context.Context,
	id int64,
) (
	Entry,
	error,
) {
	row := q.db.QueryRowContext(
		ctx,
		removeEntry,
		id,
	)
	var i Entry
	err := row.Scan(
		&i.ID,
		&i.Owner,
		&i.Balance,
		&i.Currency,
		&i.CreatedAt,
	)
	return i, err
}
