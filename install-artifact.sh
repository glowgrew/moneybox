#!/bin/bash

# рабочая папка
dir=D:/Servers/!BTOOLS/BuildTools
# где мавен находится
mvn_dir=${dir}/apache-maven-3.6.0/bin
# папка с мавен репозиторием
repo_dir=C:/Users/mcsky/.m2/repository

# какой артифакт загрузить в репозиторий
artifact =${dir}/paper.jar

group_id=org.bukkit
artifact_id=craftbukkit
version=1.7.10-R0.1-SNAPSHOT

cd $mvn_dir
"${mvn_dir}/mvn.cmd" org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file  \
    -Dfile=${artifact} \
    -DgroupId=${group_id} -DartifactId=${artifact_id} \
    -Dversion=${version} -Dpackaging=jar \
    -DlocalRepositoryPath=${repo_dir}