OUTFILE="../../main/java/org/bibsonomy/marc/$2.java"

echo "package org.bibsonomy.marc;" > "$OUTFILE"
echo "" >> "$OUTFILE"
echo "import org.marc4j.marc.Record;" >> "$OUTFILE"
echo "" >> "$OUTFILE"
cat $1 \
 | grep -vP '^<\?php$' \
 | grep -v '^\s*require_once' \
 | grep -v '^\s*include_once' \
 | sed 's/ extends MarcoriginalRecord/ extends MarcHelperBase/' \
 | sed 's/^class/public class/' \
 | sed 's/^class/public class/' \
 | sed "s/public function __construct(\\\$record)/public $2(Record record)/" \
 | sed "s/'/\"/g" \
 | sed 's/\./+/g' \
 | sed 's/->/\./g' \
 | sed 's/\$//g' \
 | sed 's/parent::__construct.*/super(record);/' \
 | sed 's/foreach (isbns as isbn)/for (String isbn : isbns)/' \
 | sed 's/\([[:alnum:]]\+\) = array();/String[] \1 = array();/' \
 | sed 's/\([[:alnum:]]\+\) *= *"/String \1 = "/' \
 | sed 's/if (strpos(\(.*\), "\(.*\)") !== false){/removeFirstChar(\1,"\2"); if(dummyFalse()) {/' \
 | sed 's/\([^= ]*\) *= *this._getFirstFieldValue(/String \1 = this._getFirstFieldValue(/' \
 | sed 's/\([^= ]*\) *= *this._getFieldArray(/String[] \1 = this._getFieldArray(/' \
 | sed 's/\([^= ]*\) *= *trim(/String \1 = trim(/' \
 | sed 's/ function getJournal(/ String[] getJournal(/' \
 | sed 's/ function get\([^ ]*\)s(/ String[] get\1s(/' \
 | sed 's/ function get\([^ ]*\)(/ String get\1(/' \
 | grep -v 'occurrence = strpos(' \
 | grep -v 'tmp = substr_replac' \
 | grep -v rawrecord \
 | grep -v PicaRecord \
 | grep -v picaarray \
 >> "$OUTFILE"

# | sed 's/protected .*picaarray.*/private Record record;/' \
