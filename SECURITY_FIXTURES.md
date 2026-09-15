# Deliberately insecure test fixtures

This repository exists to validate **SCA, SAST, secret, IaC, and SBOM** scanners (TigerGate). It deliberately
contains outdated dependencies, unsafe code patterns, fake hard-coded credentials, and insecure infrastructure
definitions. **Do not deploy, apply, or reuse anything in here.**

Every credential below is fake: AWS keys are the values from AWS's own documentation, tokens are random strings in
the right shape, and private keys were generated for this repo and used nowhere. Nothing resolves, nothing authenticates.

Use this file as the answer key: a scanner that is working should report (at least) what is listed for its category.

---

## 1. SCA — vulnerable dependencies

Declared in `build.gradle.kts`, resolved in `gradle.lockfile` (48 entries, all configurations locked), and listed in
`sbom/bom.json`. Runtime scope unless noted.

| Package | Version | Expected findings |
|---|---|---|
| `org.apache.logging.log4j:log4j-core` | 2.14.1 | CVE-2021-44228 (Log4Shell, critical), CVE-2021-45046, CVE-2021-45105, CVE-2021-44832 |
| `com.fasterxml.jackson.core:jackson-databind` | 2.9.8 | CVE-2019-12384, CVE-2019-12814, CVE-2019-14379, CVE-2019-14540, CVE-2020-36518 (+ ~40 more) |
| `org.yaml:snakeyaml` | 1.26 | CVE-2022-1471 (RCE via unsafe constructor), CVE-2022-25857, CVE-2022-38749..38752 |
| `org.apache.commons:commons-text` | 1.9 | CVE-2022-42889 (Text4Shell) |
| `commons-io:commons-io` | 2.6 | CVE-2021-29425 (path traversal in `FilenameUtils.normalize`) |
| `commons-collections:commons-collections` | 3.2.1 | CVE-2015-6420, CVE-2017-15708 (deserialisation gadget chain) |
| `com.google.guava:guava` | 19.0 | CVE-2018-10237, CVE-2020-8908, CVE-2023-2976 |
| `com.squareup.okhttp3:okhttp` | 3.12.0 | CVE-2021-0341 (hostname verification bypass) |
| `org.apache.httpcomponents:httpclient` | 4.5.10 | CVE-2020-13956 |
| `org.postgresql:postgresql` | 42.2.5 | CVE-2020-13692, CVE-2022-21724, CVE-2022-26520 |
| `com.h2database:h2` | 1.4.199 | CVE-2021-42392 (JNDI RCE), CVE-2022-23221, CVE-2021-23463 |
| `org.bouncycastle:bcprov-jdk15on` | 1.60 | CVE-2018-1000613, CVE-2020-15522, CVE-2020-26939 |
| `org.springframework:spring-web` | 5.3.15 | CVE-2016-1000027 (deserialisation); transitively `spring-beans` 5.3.15 → CVE-2022-22965 (Spring4Shell) |
| `junit:junit` (test scope) | 4.12 | CVE-2020-15250 (`TemporaryFolder` info disclosure) |

Also for outdated-dependency / EOL checks: Terraform providers `aws 3.0.0`, `azurerm 2.40.0`, `google 3.0.0`; Docker
base images `openjdk:17.0.2-slim-buster` (EOL image on EOL Debian), `postgres:9.6` (EOL); Lambda runtime `python3.6`.

## 2. SAST — code patterns (`src/main/kotlin/com/tigergate/fixture/`)

| File | CWE | Pattern |
|---|---|---|
| `App.kt` | CWE-798, CWE-208, CWE-117 | Hard-coded admin credentials, non-constant-time comparison, user input to Log4j |
| `sast/SqlInjection.kt` | CWE-89, CWE-798 | String-concatenated `Statement`, `String.format` into `PreparedStatement`, hard-coded JDBC password. `demo()` runs a real injection against H2 |
| `sast/CommandInjection.kt` | CWE-78 | `Runtime.exec` and `ProcessBuilder("sh","-c", …)` with user input |
| `sast/PathTraversal.kt` | CWE-22 | Base-dir concatenation, bypassable `FilenameUtils.normalize`, Zip Slip |
| `sast/WeakCrypto.kt` | CWE-327, CWE-328, CWE-321, CWE-329, CWE-338, CWE-916 | MD5/SHA-1, DES, AES/ECB, hard-coded key, zero IV, `java.util.Random` token, seeded `SecureRandom`, 10-iteration PBKDF2 |
| `sast/InsecureTls.kt` | CWE-295, CWE-297, CWE-326 | Trust-all `X509TrustManager`, allow-all `HostnameVerifier`, `SSLContext("SSL")`, OkHttp with both |
| `sast/Xxe.kt` | CWE-611 | `DocumentBuilderFactory`, `SAXParserFactory`, `XMLInputFactory`, `TransformerFactory` with default settings |
| `sast/Deserialization.kt` | CWE-502 | `ObjectInputStream.readObject`, Jackson `enableDefaultTyping`, SnakeYAML `Yaml().load` |
| `sast/Ssrf.kt` | CWE-918 | `URL(userInput).openStream()`, OkHttp to user URL, metadata endpoint `169.254.169.254` |
| `sast/CodeInjection.kt` | CWE-94, CWE-95, CWE-470 | `ScriptEngine.eval`, `Class.forName(userInput)`, commons-text `StringSubstitutor.createInterpolator` |
| `sast/LogInjection.kt` | CWE-117, CWE-532 | Unsanitised input to log, password/token logged at debug |
| `sast/LdapInjection.kt` | CWE-90, CWE-798, CWE-319 | Filter concatenation, hard-coded bind password, `ldap://` |
| `sast/VulnerableServlet.kt` | CWE-79, CWE-601, CWE-113, CWE-614, CWE-1004, CWE-209, CWE-352, CWE-862 | Reflected XSS, open redirect, header injection, insecure cookie, stack trace to client, state change with no CSRF/authz |
| `sast/InsecureFiles.kt` | CWE-377, CWE-732, CWE-1333 | Predictable `/tmp` file, world-writable perms, ReDoS regex |
| `src/main/resources/application.properties` | — | Actuator `include=*`, `include-stacktrace=always`, H2 console enabled |
| `scripts/deploy.sh` | CWE-78, CWE-295, CWE-494, CWE-532, CWE-732 | Unquoted var to `ssh`, `curl -k … \| sudo bash`, secrets echoed, `chmod -R 777`, `-p` password on CLI |

## 3. Secrets

Every file below should produce at least one secret finding. Formats are chosen to match common detector signatures
(gitleaks, trufflehog, detect-secrets, GitHub push protection).

| File | Contents |
|---|---|
| `.env` | 20+ tokens: AWS, GitHub PAT, GitLab PAT, npm, Slack bot + webhook, Stripe live/publishable, SendGrid, Mailgun, Twilio SID/token, Google API, Heroku, DigitalOcean, OpenAI-style, JWT secret, DB/Redis URLs with passwords |
| `config/secrets.yaml` | Same families in YAML, plus a signed JWT, a Basic-auth header, and an embedded PKCS#8 private key |
| `config/fixture_rsa.pem` | `BEGIN RSA PRIVATE KEY` (PKCS#1) |
| `config/fixture_deploy_key` | `BEGIN OPENSSH PRIVATE KEY` (ed25519) |
| `config/gcp-service-account.json` | GCP service-account JSON with `private_key` |
| `src/main/resources/application.properties` | Spring datasource/mail/keystore passwords, OAuth client secret, AWS keys, Azure storage connection string |
| `src/main/kotlin/com/tigergate/fixture/Secrets.kt` | Constants: AWS, GitHub, Slack, Stripe, Google, SendGrid, JWT, admin password, credentialed URLs, PEM key |
| `src/main/kotlin/…/SqlInjection.kt`, `LdapInjection.kt` | Passwords inline in `getConnection` / JNDI env |
| `scripts/deploy.sh` | AWS export, PAT, Slack webhook, DB and MySQL passwords |
| `terraform/main.tf`, `secrets.tf`, `compute.tf`, `database.tf`, `azure.tf`, `gcp.tf` | Provider creds, `locals`, variable defaults, `user_data`, RDS/SQL/GKE master passwords |
| `cloudformation/insecure-stack.yaml` | Parameter default, `MasterUserPassword`, `LoginProfile.Password`, Lambda env, secret in `Outputs` |
| `kubernetes/deployment.yaml`, `secret.yaml` | Plaintext env values, `Secret` with base64 + `stringData`, secret in a `ConfigMap` |
| `Dockerfile`, `docker-compose.yml` | `ENV`/`ARG` secrets, compose `environment` secrets |
| `.github/workflows/insecure-ci.yml` | AWS keys in `env`, PAT in `curl`, Docker Hub password |

## 4. IaC

### Terraform (`terraform/`, validates with `terraform validate`)
- **S3** (`s3.tf`): `public-read` ACL, public access block disabled, `Principal:*` bucket policy, no encryption/versioning/logging.
- **Network** (`network.tf`): SG open on 22/3389/5432/all from `0.0.0.0/0` and `::/0`, unrestricted egress, open NACL, VPC without flow logs.
- **Compute** (`compute.tf`): public IP, unencrypted root/EBS volumes, IMDSv1 (`http_tokens = optional`), secrets in `user_data`, no monitoring, Lambda on `python3.6` with secrets in env.
- **Data** (`database.tf`): RDS + Aurora publicly accessible, unencrypted, hard-coded password, no backups/deletion protection/multi-AZ; ElastiCache without encryption; DynamoDB without SSE/PITR; SQS/SNS without KMS; EFS unencrypted.
- **IAM** (`iam.tf`): `Action:*`/`Resource:*` policy, IAM user with access key + `AdministratorAccess`, role assumable by `AWS:*`, `iam:PassRole`, weak account password policy.
- **Logging/KMS** (`logging.tf`): KMS key without rotation and with `*` policy, CloudTrail single-region without validation/KMS, log group without retention, ECR mutable + no scan-on-push, API Gateway stage without logging/WAF.
- **Azure** (`azure.tf`): storage account with HTTPS-only off, TLS 1.0, public blob access, open network rules; NSG allow-all inbound; SQL server with hard-coded admin password and `0.0.0.0–255.255.255.255` firewall; Key Vault without purge/soft-delete protection.
- **GCP** (`gcp.tf`): bucket with `allUsers` reader, firewall `0.0.0.0/0` on all TCP, VM with public IP / OS Login off / serial console on / default SA with `cloud-platform`, Cloud SQL MySQL 5.6 public with SSL off and no backups, GKE with legacy ABAC, basic auth, client cert.

### CloudFormation (`cloudformation/insecure-stack.yaml`, 13 resources)
Public bucket + `*` policy, allow-all SG, EC2 with secrets in `UserData` and unencrypted EBS, public unencrypted RDS with plaintext password, IAM role/user with `*`, access key with secret in `Outputs`, Lambda `python3.6` with secrets in env, SQS/SNS/Logs/KMS without encryption or rotation, parameter default containing a secret without `NoEcho`.

### Kubernetes (`kubernetes/`)
- `deployment.yaml`: `privileged`, `runAsUser: 0`, `allowPrivilegeEscalation`, `capabilities.add: ALL`, `hostNetwork/hostPID/hostIPC`, `hostPort`, `hostPath: /` and docker socket, `:latest` tag, no limits/requests/probes, secrets in `env`, SA token automounted.
- `pod-debug.yaml`: privileged pod in `kube-system`, untagged image, `procMount: Unmasked`, `/` and `/etc` hostPath.
- `rbac.yaml`: `ClusterRole` with `*/*/*`, `cluster-admin` bound to `default` SA and `system:anonymous`/`system:unauthenticated`, Role granting `secrets` read and `pods/exec`.
- `service.yaml`: `NodePort`, `LoadBalancer`, Ingress without TLS, wildcard host, SSL redirect disabled.
- `secret.yaml`: `Secret` committed to source, secret in `ConfigMap`.

### Docker
- `Dockerfile`: EOL base on EOL distro, secrets in `ENV`/`ARG`, unpinned `apt-get` with no cleanup, `sudo`/`openssh-server` installed, root password set, `curl -k`, `ADD` for local files, `chmod -R 777`, `EXPOSE 22`, explicit `USER root`, shell-form `CMD`, no `HEALTHCHECK`, no `.dockerignore` (so `COPY . .` bakes `.git`, `.env`, `config/` into the build stage).
- `docker-compose.yml`: `privileged`, `pid: host`, `cap_add: ALL`, seccomp/AppArmor unconfined, docker socket and `/` mounted, secrets in `environment`, ports bound to `0.0.0.0`, `restart: always`; `postgres:9.6` with `trust` auth on host network; Redis with protected mode off.

### CI (`.github/workflows/insecure-ci.yml`) — gated `if: false`, never executes
`pull_request_target` + checkout of `head.sha` with `persist-credentials`, `permissions: write-all`, expression injection from `issue.title`/`comment.body`/`head_ref`, unpinned `@master`, deprecated `@v1`/`@v2` actions, secrets echoed and passed on the command line, `set-output`, `curl | sudo bash`, `ACTIONS_ALLOW_UNSECURE_COMMANDS`, cache key from untrusted `head_ref`.

## 5. SBOM

- Generated with the CycloneDX Gradle plugin: `./gradlew cyclonedxBom` → `sbom/bom.json` + `sbom/bom.xml` (CycloneDX 1.5, runtime classpath, 26 components, PURLs for every component).
- `gradle.lockfile` gives lockfile-based tools the same graph without running Gradle.
- Expected: every package in section 1 (except test-scoped `junit`) appears with the exact version listed, and vulnerability matching against the SBOM reproduces the section-1 CVEs.

---

## Running it

```bash
./gradlew build            # compile + tests (4 tests, expected to pass)
./gradlew cyclonedxBom     # regenerate sbom/
./gradlew run --args="admin password123"
./gradlew run --args="' OR '1'='1 x"   # live SQL injection demo (returns every row)
docker compose config      # safe; `docker compose up` mounts the host root — don't
```
