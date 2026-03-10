MVN := mvn

.PHONY: clean compile test package install verify deploy build \
        check-clean release release-dry-run release-rollback

# ── Helpers ───────────────────────────────────────────────────────────────────

check-clean:
	@if [ -n "$$(git status --porcelain)" ]; then \
		echo "Error: Working directory is not clean. Commit or stash changes before releasing."; \
		exit 1; \
	fi

# ── Standard Maven lifecycle ──────────────────────────────────────────────────

clean:
	$(MVN) clean

compile:
	$(MVN) compile

test:
	$(MVN) test

package:
	$(MVN) package

install:
	$(MVN) install

verify:
	$(MVN) verify

deploy:
	$(MVN) deploy

build:
	$(MVN) clean install

# ── Release ───────────────────────────────────────────────────────────────────
#
# maven-release-plugin handles the full release sequence:
#   release:prepare  — strips -SNAPSHOT, commits, tags, bumps to next
#                      -SNAPSHOT version, commits that too
#   release:perform  — checks out the tag, builds, signs, and deploys
#                      to the configured repository
#
# Credentials for the OSSRH/Central staging API must be present in
# ~/.m2/settings.xml under server id "ossrh".

release: check-clean
	$(MVN) release:prepare release:perform

# Rehearse without writing anything (no commits, no tags, no deploy)
release-dry-run:
	$(MVN) release:prepare -DdryRun=true

# Undo a failed/partial release:prepare (removes tag, restores pom.xml)
release-rollback:
	$(MVN) release:rollback
