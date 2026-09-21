# tigergate-test-kotlin

Security-test fixture for Kotlin / Gradle. Deliberately vulnerable — see [SECURITY_FIXTURES.md](SECURITY_FIXTURES.md)
for the full list of planted findings by scanner category (SCA, SAST, secrets, IaC, SBOM).

```bash
./gradlew build                          # compile + tests
./gradlew run --args="admin password123" # CLI credential check + SQL-injection demo
./gradlew cyclonedxBom                   # regenerate sbom/bom.json
```

Requires JDK 17. Nothing in this repository should be deployed, applied, or copied.
# tigergate-test-kotlin
# tigergate-test-kotlin
# tigergate-test-kotlin
# tigergate-test-kotlin
