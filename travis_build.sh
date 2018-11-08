mvn install
if [[ "$?" -ne 0 ]] ; then
  echo 'mvn install failed. Exited with non-zero value '; exit 42
fi
echo 'finish maven install'
