INSERT INTO ledger_accounts (
    id,
    owner_type,
    owner_id,
    currency,
    created_at
)
VALUES (
           gen_random_uuid(),
           'PLATFORM',
           NULL,
           'PLN',
           CURRENT_TIMESTAMP
       );

INSERT INTO ledger_accounts (
    id,
    owner_type,
    owner_id,
    currency,
    created_at
)
VALUES (
           gen_random_uuid(),
           'PLATFORM',
           NULL,
           'EUR',
           CURRENT_TIMESTAMP
       );