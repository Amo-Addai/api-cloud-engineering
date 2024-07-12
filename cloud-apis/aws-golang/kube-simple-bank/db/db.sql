
Table accounts as A {
    id bigserial [pk]
    owner varchar [not null]
    balance bigint [not null]
    currency varchar [not null]
    created_at timestamptz [not null, default: `now()`]

    Indexes {
        owner
    }
}

Table entries {
    id bigserial [pk]
    account_id bigint [not null, ref: > A.id]
    amount bigint [not null, note: 'can be positive or negative']
    created_at timestamptz [not null, default: `now()`]

    Indexes {
        account_id
    }
}

Table transfers {
    id bigserial [pk]
    from_account_id bigint [not null, ref: > A.id]
    to_account_id bigint [not null, ref: > A.id]
    amount bigint [not null, note: 'must be positive']
    created_at timestamptz [not null, default: `now()`]

    Indexes {
        from_account_id
        to_account_id
        (from_account_id, to_account_id)
    }
}

/* -- TODO: 

- put this in dbdiagram.io & export PostgreSQL (or MySQL / SQL Server)
- export db diagram to a pdf / png file too

*/