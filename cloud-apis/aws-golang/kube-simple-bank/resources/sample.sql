
Table sample {
    id int [pk, increment] -- auto-increment
}

Enum products_status {
    out_of_stock
    in_stock
    running_low [note: 'less than 20']
}

Table users as U {
    id int [pk, increment]
    full_name varchar
    created_at timestamptz
    country_code int
}

Table merchants {
    id int [pk]
    merchant_name varchar
    country_code int
    "created at" varchar
    admin_id int [ref: > U.id]  -- inline relationship (many-to-one)
}

Table countries {
    code int [pk]
    name varchar
    continent_name varchar
}

Table order_items {
    order_id int [ref: > orders.id]
    product_id int
    quantity int [default: 1]
}

Table orders {
    id int [pk]
    user_id int [not null, unique]
    status varchar
    created_at varchar [note: 'When order created']
}

Table products {
    id int [pk]
    name varchar
    merchant_id int [not null]
    price int
    status products_status
    created_at datetime [default: `now()`]

    Indexes {
        (merchant_id, status) [name: 'products_status']
        id [unique]
    }
}

-- References & Relationships (> many-to-one | < one-to-many)

Ref: U.country_code > countries.code
Ref: merchants.country_code > countries.code
Ref: products.merchant_id > merchants.id
Ref: order_items.product_id > products.id
