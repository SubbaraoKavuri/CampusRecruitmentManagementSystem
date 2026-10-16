# Campusly verification report

Date: 15 October 2026

## Build and runtime

- `mvn -q -DskipTests package` — passed.
- `node --check src/main/resources/static/app.js` — passed.
- Started the packaged application on port 8081 — passed; Spring Boot initialized the file-backed H2 database and served the web application.
- `GET /`, `/app.js`, and `/app.css` returned HTTP 200.

## Manually exercised API flows

Checks were run against the live app over HTTP using local development OTP delivery.

- Admin password plus email OTP login — passed; token carries the ADMIN role.
- Student registration and email OTP verification — passed; token carries the STUDENT role.
- Admin created a college opportunity — passed.
- Student job feed returned the open opportunity — passed.
- Student application submission — passed; initial status was SUBMITTED.
- Duplicate application to the same job — correctly rejected with HTTP 409.
- Admin changed application status to UNDER_REVIEW — passed; student view reflected the update.
- Student profile updates (department, year, CGPA, skills, phone, resume link) — passed.
- Student access to the admin student-directory API — correctly rejected with HTTP 403.
- Student support ticket creation and admin inbox visibility — passed.
- Deleting a job with an application — correctly rejected with HTTP 409.
- External role create/edit/publish/close/delete — passed; its ID appeared in the student feed while OPEN and disappeared when CLOSED.
- Application withdrawal — early-stage withdrawal is allowed; an ACCEPTED application is correctly rejected with HTTP 409.
- Support requests can be submitted without a signed-in account, appear in the admin inbox, and can be marked RESOLVED — passed.
- Password reset using OTP followed by a fresh password-plus-OTP login — passed.
- Invalid past application deadlines — rejected by request validation.
- OTP resend cooldown — rapid resend is rate-limited; the development UI now includes a resend control.
- Admin and student jobs/dashboard/feed APIs are backed by the persistent database; closed and draft roles are excluded from the student feed.
- Configured support email is returned by a public contact endpoint and is used in the authenticated Help & Support screen.

## UI verification limit

The application is running at `http://localhost:8081/`. Local browser interaction was not available in this run because the desktop browser-control permission was denied earlier. The packaged static assets were checked over HTTP and the UI JavaScript syntax was checked with Node; the backend user flows above were exercised directly against the live HTTP endpoints.

## Production configuration

Local development intentionally displays OTP codes in the UI and uses a local seeded admin account. Before real use, configure SMTP, a private support inbox, database credentials, a strong JWT secret, and deployment-specific admin credentials; set `EMAIL_DEV_CODE_ENABLED=false`.
