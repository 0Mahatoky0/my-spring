#!/bin/bash

# Définition des variables
APP_NAME="spring-mvc"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# Nettoyage et création du répertoire temporaire
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR
# Compilation des fichiers Java avec le JAR des Servlets
find $SRC_DIR -name "*.java" > sources.txt
javac -cp $SERVLET_API_JAR -d $BUILD_DIR @sources.txt
rm sources.txt

# Générer le fichier .jar dans le dossier build
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.jar *
rm *.class
cd ..

echo ""

echo "Déploiement terminé. Redémarrez Tomcat si nécessaire."

echo ""
