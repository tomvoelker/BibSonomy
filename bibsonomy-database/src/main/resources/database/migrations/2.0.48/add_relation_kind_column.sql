ALTER TABLE log_gold_standard_publication_references ADD relation_kind TINYINT(1) NOT NULL default '0';
ALTER TABLE gold_standard_publication_references ADD relation_kind TINYINT(1) NOT NULL default '0';

alter table xx drop primary key, add primary key(k1, k2, k3);


RENAME TABLE gold_standard_publication_references TO gold_standard_relations;
RENAME TABLE log_gold_standard_publication_references TO log_gold_standard_relations;