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

YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo
echo -e " ${YELLOW}Will run build with the following commands :${NC}"
echo -e ${YELLOW}$gradle_args${NC}
echo

./gradlew $gradle_args
