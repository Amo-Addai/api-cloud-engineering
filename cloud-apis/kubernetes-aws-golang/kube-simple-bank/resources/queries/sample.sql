

-- SQL Query Execution Order


SELECT column_a, column_b

FROM t1

JOIN t2

ON t1.column_a = t2.column_a

WHERE constraint_expression

GROUP BY column

HAVING constraint_expression

ORDER BY column ASC/DESC

LIMIT count;
