# Campusly

Campusly is a campus recruitment workspace for career teams and students. It provides email-verified accounts, role-based access, persistent job and application management, and a support inbox.

## Run locally

Requirements: Java 17+ and Maven 3.8+.

```powershell
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The local database is a file-backed H2 database at `./data/campusrecruit`, so accounts, jobs, applications, profiles, and support requests remain after restart. For another port, run with `--server.port=8081`.

Local admin access:

- Email: `admin@campusrecruit.local`
- Password: `CampusAdmin-Local-2026!`

On first startup the application creates this verified administrator if that email is not already registered. Change these defaults before sharing the environment. Local email verification codes appear on screen while SMTP is disabled.

## Roles and workflows

### Students

- Create an account after email OTP verification; sign-in also requires a one-time email code.
- Recover access through an email verification code and password reset.
- Browse open college and external opportunities, review the full job description, and submit one application per job.
- Track live application status and withdraw a submission while it is submitted or under review.
- Maintain department, year, CGPA, skills, phone, and resume link in the student profile.
- Send a support request that is saved in the career-services inbox.

### Administrators

- Create, edit, publish, close, or remove opportunities. Jobs include the employer, source, location, compensation, deadline, and full job description.
- Review all applications, update their pipeline status, and browse verified student profiles.
- Review support requests and mark them open, in progress, or resolved.
- View placement and review metrics on the overview.

## Email and production settings

Local defaults are for development. For a deployed environment, configure:

- `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` for the database.
- `JWT_SECRET` with a randomly generated secret of at least 32 bytes.
- `ADMIN_EMAIL`, `ADMIN_PASSWORD`, and optionally `ADMIN_NAME` for the first administrator.
- `EMAIL_SMTP_ENABLED=true`, `EMAIL_DEV_CODE_ENABLED=false`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, and `MAIL_FROM` for real verification and support emails.
- `SUPPORT_EMAIL` for the career-services inbox, and `PORT` if the platform requires a custom port.

Disable development codes and replace all local admin/JWT credentials before enabling real accounts. The support address shown in the local UI is a development placeholder; set `SUPPORT_EMAIL` and `MAIL_FROM` to college-managed addresses for deployment.

## API

The UI uses the protected REST API under `/api`. OpenAPI docs are available at `/swagger-ui/index.html` while the server is running.

Key endpoints: `/api/auth/*`, `/api/jobs`, `/api/jobs/{id}/apply`, `/api/applications`, `/api/profile`, `/api/admin/students`, and `/api/support`.

## Verification

See [MANUAL_TEST_REPORT.md](MANUAL_TEST_REPORT.md) for build, HTTP flow, and authorization checks.
