To upload change to Github:
cd ~/project-path-here
COMMIT_VERSION="commit_"`date +%s`
git init
git add app/src/
git commit -am "commit version"
git remote add $COMMIT_VERSION https://github.com/deliamao/Stock-Market-Search-APP/
git push -u $COMMIT_VERSION master

To sync with Github:
cd ~/project-path-here
git pull https://github.com/deliamao/Stock-Market-Search-APP/
