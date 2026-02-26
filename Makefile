.PHONY: build release check-clean

check-clean:
	@if [ -n "$$(git status --porcelain)" ]; then \
		echo "Error: Working directory is not clean. Commit or stash changes before releasing."; \
		exit 1; \
	fi

build:
	mvn clean install

release: check-clean build
	@echo "Calculating versions..."
	@current_version=$$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout); \
	release_version=$$(echo $$current_version | sed 's/-SNAPSHOT//'); \
	next_version=$$(echo $$release_version | awk -F. '{$$NF = $$NF + 1;} 1' OFS=.)-SNAPSHOT; \
	echo "Current version: $$current_version"; \
	echo "Release version: $$release_version"; \
	echo "Next snapshot:   $$next_version"; \
	mvn versions:set -DnewVersion=$$release_version -DgenerateBackupPoms=false; \
	git add pom.xml; \
	git commit -m "Release $$release_version"; \
	git tag -a v$$release_version -m "Release $$release_version"; \
	mvn versions:set -DnewVersion=$$next_version -DgenerateBackupPoms=false; \
	git add pom.xml; \
	git commit -m "Prepare for next development iteration $$next_version"; \
	echo "Release $$release_version completed. Now on $$next_version."
