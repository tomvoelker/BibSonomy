ALTER TABLE log_gold_standard_publication_references ADD relation_kind TINYINT(1) NOT NULL default '0';
ALTER TABLE gold_standard_publication_references ADD relation_kind TINYINT(1) NOT NULL default '0';

ALTER TABLE gold_standard_publication_references DROP PRIMARY KEY, ADD PRIMARY KEY(publication, reference, relation_kind);


RENAME TABLE gold_standard_publication_references TO gold_standard_relations;
RENAME TABLE log_gold_standard_publication_references TO log_gold_standard_relations;