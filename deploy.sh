#!/bin/bash
#
# BibSonomy - A blue social bookmark and publication sharing system.
#
# Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#


# REGISTRATION
# * if you want the system to archive its webapp register it as archivable
# * if you want to disable mails and security questions before deployment register it in the array $unnoticed

# environment variables
source deploy-functions.sh

# main
checkParams "$@"

targetServer=$1

document $targetServer
WEBAPP_PATH=$BIBSONOMY_PATH/$DEFAULT_WEBAPP
build $WEBAPP_PATH
deploy $WEBAPP_PATH $targetServer $2
sendMail $targetServer