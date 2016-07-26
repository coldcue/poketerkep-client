#!/bin/sh

echo "Cleaning..."
rm -fr revision
rm -f revision.zip

echo "Creating revision folder"
mkdir -v revision

echo "Copying PokemonGo-Map"
cp -rf PokemonGo-Map/ revision/PokemonGo-Map

echo "Copying poketerkep-client.jar"
cp -vf build/libs/poketerkep-client-0.1.jar revision/poketerkep-client.jar

echo "Copying codedeploy"
cp -r codedeploy/ revision/

#echo "Zipping revision"
#cd revision
#zip -r revision.zip *
#mv revision.zip ../
#cd ..