#!/bin/bash 
#
# BibSonomy-Webapp - The web application for BibSonomy.
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
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

PUMA_HOST=$1
FILE_NAME="/var/tmp/$PUMA_HOST"
#PUMA_URL="http://$PUMA_HOST/postBookmark"
PUMA_URL="http://$PUMA_HOST/swrc/"
#TIMES=$3
#SLEEP=`expr 60 / $TIMES`
SLEEP=30
for I in `seq 1 2`; do
	LINES=`wget -t 5 --timeout 15 -O /dev/stdout -o /dev/null --no-cache "$PUMA_URL" | grep 'Ontology' | wc -l`
	if [ $LINES -gt 0 ]; then
		touch "$FILE_NAME"
	else
		if [ -e "$FILE_NAME" ]; then
			rm "$FILE_NAME"
		fi
	fi
	if [ $I -lt 2 ]; then
		sleep $SLEEP
	fi
done
