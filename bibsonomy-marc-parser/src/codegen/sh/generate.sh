#
# BibSonomy-MARC-Parser - Marc Parser for BibSonomy
#
# Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
#                               University of Kassel, Germany
#                               http://www.kde.cs.uni-kassel.de/
#                           Data Mining and Information Retrieval Group,
#                               University of Würzburg, Germany
#                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
#                           L3S Research Center,
#                               Leibniz University Hannover, Germany
#                               http://www.l3s.de/
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

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
