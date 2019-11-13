# Build with tests
gradle_args="check"
if [ "$TRAVIS_PULL_REQUEST" == "false" ]
then
  # run sonarqube with coverage report when not on PR
  git fetch --unshallow --quiet
  gradle_args+=" jacocoTestReport sonarqube"

  if [ -z "$TRAVIS_TAG" ]
  then
    # publish snapshot version when not on TAG
    gradle_args+=" publish"
  fi
fi

COLOR='\033[0;36m' #Cyan
NC='\033[0m' # No color to reset

echo
echo -e "${COLOR}Will build with the following commands :${NC}"
echo -e ${COLOR}$gradle_args${NC}
echo

./gradlew $gradle_args
