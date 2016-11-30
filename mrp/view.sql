CREATE VIEW vtotal AS SELECT
m.matnr, m.description, b.base_uom, st.werks, st.lgort,
st.free_stock, st.quality_stock, st.blocked_stock,
t.year, t.month, t.pur_group, t.req_total
FROM mara AS m
INNER JOIN mbew AS b
ON b.matnr = m.matnr
INNER JOIN stocks AS st
ON st.matnr = m.matnr
INNER JOIN totals AS t
ON  t.matnr = st.matnr
GROUP BY m.matnr, t.pur_group, t.year, t.month