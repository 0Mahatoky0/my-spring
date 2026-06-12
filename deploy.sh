#!/bin/bash

# Définition des variables
APP_NAME="spring-mvc"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/home/max/tomcat/webapps"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# Nettoyage et création du répertoire temporaire
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/classes
# Compilation des fichiers Java avec le JAR des Servlets
find $SRC_DIR -name "*.java" > sources.txt
javac -cp $SERVLET_API_JAR -d $BUILD_DIR/WEB-INF/classes @sources.txt
rm sources.txt

# Copier les fichiers web (web.xml, JSP, etc.)
cp -r $WEB_DIR/* $BUILD_DIR/


# Générer le fichier .war dans le dossier build
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.jar *
# cd ..

# # Déploiement dans Tomcat
# cp -f $BUILD_DIR/$APP_NAME.war $TOMCAT_WEBAPPS/

# echo ""

echo "Déploiement terminé. Redémarrez Tomcat si nécessaire."

echo ""
